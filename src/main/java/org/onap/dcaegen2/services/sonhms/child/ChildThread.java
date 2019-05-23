/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2019 Wipro Limited.
 *   ==============================================================================
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *  
 *          http://www.apache.org/licenses/LICENSE-2.0
 *  
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *     ============LICENSE_END=========================================================
 *  
 *******************************************************************************/

package org.onap.dcaegen2.services.sonhms.child;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fj.data.Either;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.onap.dcaegen2.services.sonhms.BufferNotificationComponent;
import org.onap.dcaegen2.services.sonhms.ClusterDetailsComponent;
import org.onap.dcaegen2.services.sonhms.ConfigPolicy;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.HoMetricsComponent;
import org.onap.dcaegen2.services.sonhms.Timer;
import org.onap.dcaegen2.services.sonhms.dao.ClusterDetailsRepository;
import org.onap.dcaegen2.services.sonhms.dao.SonRequestsRepository;
import org.onap.dcaegen2.services.sonhms.dmaap.PolicyDmaapClient;
import org.onap.dcaegen2.services.sonhms.entity.HandOverMetrics;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.exceptions.OofNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.AnrInput;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.model.ClusterMap;
import org.onap.dcaegen2.services.sonhms.model.Flag;
import org.onap.dcaegen2.services.sonhms.model.HoDetails;
import org.onap.dcaegen2.services.sonhms.model.ThreadId;
import org.onap.dcaegen2.services.sonhms.restclient.AsyncResponseBody;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.onap.dcaegen2.services.sonhms.utils.ClusterUtils;
import org.onap.dcaegen2.services.sonhms.utils.DmaapUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class ChildThread implements Runnable {

    private BlockingQueue<List<String>> childStatusUpdate;
    private BlockingQueue<Map<CellPciPair, ArrayList<CellPciPair>>> queue = new LinkedBlockingQueue<>();

    private static Map<Long, AsyncResponseBody> responseMap = new HashMap<>();
    private Graph cluster;
    private ThreadId threadId;
    Map<CellPciPair, ArrayList<CellPciPair>> clusterMap;
    HoMetricsComponent hoMetricsComponent;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ChildThread.class);

    /**
     * Constructor with parameters.
     */
    public ChildThread(BlockingQueue<List<String>> childStatusUpdate, Graph cluster,
            BlockingQueue<Map<CellPciPair, ArrayList<CellPciPair>>> queue, ThreadId threadId,
            HoMetricsComponent hoMetricsComponent) {
        super();
        this.childStatusUpdate = childStatusUpdate;
        this.queue = queue;
        this.threadId = threadId;
        this.cluster = cluster;
        this.hoMetricsComponent = hoMetricsComponent;
    }

    public ChildThread() {

    }

    /**
     * Puts notification in queue.
     */
    // change this interface to send cell and neighbours to keep it generic for sdnr
    // and fm

    public void putInQueue(Map<CellPciPair, ArrayList<CellPciPair>> clusterMap) {
        try {
            queue.put(clusterMap);
        } catch (InterruptedException e) {
            log.error(" The Thread is Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Puts notification in queue with notify.
     */
    public void putInQueueWithNotify(Map<CellPciPair, ArrayList<CellPciPair>> clusterMap) {
        synchronized (queue) {
            try {
                queue.put(clusterMap);
                queue.notifyAll();
            } catch (InterruptedException e) {
                log.error(" The Thread is Interrupted", e);
                Thread.currentThread().interrupt();
            }

        }

    }

    /**
     * Puts response in queue.
     */
    public static void putResponse(Long threadId, AsyncResponseBody obj) {
        synchronized (responseMap) {
            responseMap.put(threadId, obj);
        }

    }

    public static Map<Long, AsyncResponseBody> getResponseMap() {
        return responseMap;
    }

    @Override
    public void run() {

        threadId.setChildThreadId(Thread.currentThread().getId());
        synchronized (threadId) {
            threadId.notifyAll();
        }

        MDC.put("logFileName", Thread.currentThread().getName());
        log.info("Starting child thread");

        StateOof oof = new StateOof(childStatusUpdate);
        ClusterUtils clusterUtils = new ClusterUtils();
        Detection detect = new Detection();
        ChildThreadUtils childUtils = new ChildThreadUtils(ConfigPolicy.getInstance(), new PnfUtils(),
                new PolicyDmaapClient(new DmaapUtils(), Configuration.getInstance()), new HoMetricsComponent());

        try {
            String networkId = cluster.getNetworkId();

            Boolean done = false;

            Map<String, ArrayList<Integer>> collisionConfusionResult;
            while (!done) {
                if (cluster.getCollisionConfusionMap().isEmpty()) {

                    collisionConfusionResult = detect.detectCollisionConfusion(cluster);
                } else {
                    collisionConfusionResult = cluster.getCollisionConfusionMap();
                }

                Boolean trigger = childUtils.triggerOrWait(collisionConfusionResult);
                ConfigPolicy configPolicy = ConfigPolicy.getInstance();
                double timer = 60;
                try {
                    timer = (double) configPolicy.getConfig().get("PCI_NEIGHBOR_CHANGE_CLUSTER_TIMEOUT_IN_SECS");
                } catch (NullPointerException e) {
                    log.info("Policy config not available. Using default timeout - 60 seconds");
                }
                if (!trigger) {
                    try {
                        Thread.sleep((long) timer * 1000);
                    } catch (InterruptedException e) {
                        log.error("Interrupted Exception while waiting for more notifications {}", e);
                        Thread.currentThread().interrupt();
                    }

                    while (!queue.isEmpty()) {
                        Map<CellPciPair, ArrayList<CellPciPair>> newNotification;
                        newNotification = queue.poll();
                        log.info("New notification from SDNR {}", newNotification);
                        cluster = clusterUtils.modifyCluster(cluster, newNotification);

                        // update cluster in DB
                        clusterUtils.updateCluster(cluster);
                        collisionConfusionResult = detect.detectCollisionConfusion(cluster);

                    }

                }
                ArrayList<String> cellidList = new ArrayList<>();
                ArrayList<String> cellIds = new ArrayList<>();

                for (Map.Entry<String, ArrayList<Integer>> entry : collisionConfusionResult.entrySet()) {
                    String key = entry.getKey();
                    ArrayList<Integer> arr;
                    arr = entry.getValue();
                    if (!arr.isEmpty()) {
                        Set<Integer> set = new HashSet<>(arr);
                        if (((set.size() == 1) && !set.contains(0)) || (set.size() != 1)) {
                            cellIds.add(key);

                        }
                    }

                }

                for (String cell : cellIds) {
                    log.debug("cellidList entries: {}", cell);
                    cellidList.add(cell);
                }
                UUID transactionId;

                Flag policyTriggerFlag = BeanUtil.getBean(Flag.class);
                while (policyTriggerFlag.getHolder().equals("PM")) {
                    Thread.sleep(100);
                }
                policyTriggerFlag.setHolder("CHILD");
                policyTriggerFlag.setNumChilds(policyTriggerFlag.getNumChilds() + 1);

                Timer timerOof = BeanUtil.getBean(Timer.class);
                if (!timerOof.getIsTimer()) {
                    log.info("Starting timer");
                    timerOof.setIsTimer(true);
                    Timestamp startTime = new Timestamp(System.currentTimeMillis());
                    timerOof.setStartTime(startTime);
                    timerOof.setCount(0);
                    log.info("startTime {}", startTime);

                }
                int timerThreshold = (Configuration.getInstance().getOofTriggerCountTimer() * 60000);
                int triggerCountThreshold = Configuration.getInstance().getOofTriggerCountThreshold();
                log.info("Time threshold {}, triggerCountThreshold {}", timerThreshold, triggerCountThreshold);
                log.info("oof trigger count {}", timerOof.getCount());
                timerOof.setCount(timerOof.getCount() + 1);
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                Long difference = currentTime.getTime() - timerOof.getStartTime().getTime();
                if (difference < timerThreshold && timerOof.getCount() > triggerCountThreshold) {
                    log.info("difference {}", difference);

                    Either<List<AnrInput>, Integer> anrTriggerResponse = checkAnrTrigger();
                    if (anrTriggerResponse.isRight()) {
                        log.info("ANR trigger response right {}", anrTriggerResponse.right().value());
                        if (anrTriggerResponse.right().value() == 404) {
                            log.info("No poor neighbors found");
                        } else if (anrTriggerResponse.right().value() == 500) {
                            log.info("Failed to fetch HO details from DB ");
                        }
                        transactionId = oof.triggerOof(cellidList, networkId, new ArrayList<>());

                    } else {
                        log.info("ANR trigger response left {}", anrTriggerResponse.left().value());
                        List<AnrInput> anrInputList = anrTriggerResponse.left().value();
                        log.info("Trigger oof for joint optimization");
                        transactionId = oof.triggerOof(cellidList, networkId, anrInputList);

                    }

                } else {

                    transactionId = oof.triggerOof(cellidList, networkId, new ArrayList<>());

                    if (difference > timerThreshold) {
                        timerOof.setIsTimer(false);
                        timerOof.setCount(0);
                    }
                }

                long childThreadId = Thread.currentThread().getId();
                childUtils.saveRequest(transactionId.toString(), childThreadId);
                while (!ChildThread.getResponseMap().containsKey(childThreadId)) {
                    Thread.sleep(100);
                }

                AsyncResponseBody asynResponseBody = ChildThread.getResponseMap().get(childThreadId);

                try {
                    childUtils.sendToPolicy(asynResponseBody);
                    policyTriggerFlag.setNumChilds(policyTriggerFlag.getNumChilds() - 1);
                    if (policyTriggerFlag.getNumChilds() == 0) {
                        policyTriggerFlag.setHolder("NONE");
                    }

                } catch (ConfigDbNotFoundException e1) {
                    log.debug("Config DB is unreachable: {}", e1);
                }

                SonRequestsRepository sonRequestsRepository = BeanUtil.getBean(SonRequestsRepository.class);
                sonRequestsRepository.deleteByChildThreadId(childThreadId);

                List<String> childStatus = new ArrayList<>();
                childStatus.add(Long.toString(Thread.currentThread().getId()));
                childStatus.add("success");
                try {
                    childStatusUpdate.put(childStatus);
                } catch (InterruptedException e) {
                    log.debug("InterruptedException during childStatus update {}", e);
                    Thread.currentThread().interrupt();

                }

                Either<List<String>, Integer> bufferedNotifications = getBufferedNotifications();

                if (bufferedNotifications.isRight()) {
                    log.info("No buffered notifications");
                    done = true;
                } else {
                    List<Map<CellPciPair, ArrayList<CellPciPair>>> clusterMaps = getClusterMapsFromNotifications(
                            bufferedNotifications.left().value());
                    for (Map<CellPciPair, ArrayList<CellPciPair>> bufferedClusterMap : clusterMaps) {
                        cluster = clusterUtils.modifyCluster(cluster, bufferedClusterMap);
                    }
                    String cellPciNeighbourString = cluster.getPciNeighbourJson();
                    UUID clusterId = cluster.getGraphId();
                    ClusterDetailsRepository clusterDetailsRepository = BeanUtil
                            .getBean(ClusterDetailsRepository.class);
                    clusterDetailsRepository.updateCluster(cellPciNeighbourString, clusterId.toString());
                }

            }

        } catch (OofNotFoundException e) {
            log.error("OOF not found, Removing flag and cleaning up");
            Flag policyTriggerFlag = BeanUtil.getBean(Flag.class);
            policyTriggerFlag.setNumChilds(policyTriggerFlag.getNumChilds() - 1);
            if (policyTriggerFlag.getNumChilds() == 0) {
                policyTriggerFlag.setHolder("NONE");
            }
        } catch (Exception e) {
            log.error("{}", e);
            
            
        }

        cleanup();
    }

    private List<Map<CellPciPair, ArrayList<CellPciPair>>> getClusterMapsFromNotifications(List<String> notifications) {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<CellPciPair, ArrayList<CellPciPair>>> clusterMaps = new ArrayList<>();
        for (String notification : notifications) {
            Map<CellPciPair, ArrayList<CellPciPair>> clusterMap = new HashMap<>();
            ClusterMap clusterMapJson = new ClusterMap();
            try {
                clusterMapJson = mapper.readValue(notification, ClusterMap.class);
                clusterMap.put(clusterMapJson.getCell(), clusterMapJson.getNeighbourList());

                log.debug("clusterMap{}", clusterMap);
                clusterMaps.add(clusterMap);
            } catch (IOException e) {
                log.error("Error parsing the buffered notification, skipping {}", e);
            }
        }
        return clusterMaps;
    }

    private Either<List<String>, Integer> getBufferedNotifications() {
        log.info("Check if notifications are buffered");
        BufferNotificationComponent bufferNotificationComponent = new BufferNotificationComponent();
        ClusterDetailsComponent clusterDetailsComponent = new ClusterDetailsComponent();
        String clusterId = clusterDetailsComponent.getClusterId(Thread.currentThread().getId());
        List<String> bufferedNotifications = bufferNotificationComponent.getBufferedNotification(clusterId);
        if (bufferedNotifications == null || bufferedNotifications.isEmpty()) {
            return Either.right(404);
        } else {
            return Either.left(bufferedNotifications);
        }

    }

    /**
     * cleanup resources.
     */
    private void cleanup() {
        log.info("cleaning up database and killing child thread");
        List<String> childStatus = new ArrayList<>();
        childStatus.add(Long.toString(Thread.currentThread().getId()));
        childStatus.add("done");
        try {
            childStatusUpdate.put(childStatus);
        } catch (InterruptedException e) {
            log.debug("InterruptedException during cleanup{}", e);
            Thread.currentThread().interrupt();

        }
        ClusterDetailsRepository clusterDetailsRepository = BeanUtil.getBean(ClusterDetailsRepository.class);
        clusterDetailsRepository.deleteByChildThreadId(threadId.getChildThreadId());
        log.info("Child thread :{} {}", Thread.currentThread().getId(), "completed");
        MDC.remove("logFileName");

    }

    /**
     * Buffer Notification.
     */
    public List<Map<CellPciPair, ArrayList<CellPciPair>>> bufferNotification() {

        // Processing Buffered notifications

        List<Map<CellPciPair, ArrayList<CellPciPair>>> clusterMapList = new ArrayList<>();

        Configuration config = Configuration.getInstance();

        int bufferTime = config.getBufferTime();

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        log.debug("Current time {}", currentTime);

        Timestamp laterTime = new Timestamp(System.currentTimeMillis());
        log.debug("Later time {}", laterTime);

        long difference = laterTime.getTime() - currentTime.getTime();
        while (difference < bufferTime) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("InterruptedException {}", e);
                Thread.currentThread().interrupt();

            }
            laterTime = new Timestamp(System.currentTimeMillis());
            difference = laterTime.getTime() - currentTime.getTime();

            log.debug("Timer has run for  seconds {}", difference);

            if (!queue.isEmpty()) {
                Map<CellPciPair, ArrayList<CellPciPair>> clusterMap;
                clusterMap = queue.poll();
                clusterMapList.add(clusterMap);
            }

        }
        return clusterMapList;
    }

    /**
     * Check if ANR to be triggered.
     */
    public Either<List<AnrInput>, Integer> checkAnrTrigger() {

        List<AnrInput> anrInputList = new ArrayList<>();
        Configuration configuration = Configuration.getInstance();
        List<HoDetails> hoDetailsList;
        Either<List<HandOverMetrics>, Integer> hoMetrics = hoMetricsComponent.getAll();
        if (hoMetrics.isRight()) {
            log.error("Error in getting HO details from db");
            return Either.right(500);
        }
        List<HandOverMetrics> hoMetricsList = hoMetrics.left().value();
        for (HandOverMetrics hoMetric : hoMetricsList) {
            String hoDetailsListString = hoMetric.getHoDetails();
            ObjectMapper mapper = new ObjectMapper();
            try {
                hoDetailsList = mapper.readValue(hoDetailsListString, new TypeReference<ArrayList<HoDetails>>() {
                });
            } catch (Exception e) {
                log.error("Exception in parsing HO metrics", hoDetailsListString, e);
                continue;
            }
            List<String> removeableNeighbors = new ArrayList<>();
            log.info("Checking poor count for src cell {}", hoMetric.getSrcCellId());
            for (HoDetails hoDetail : hoDetailsList) {
                if (hoDetail.getPoorCount() >= configuration.getPoorCountThreshold()) {
                    removeableNeighbors.add(hoDetail.getDstCellId());
                }
            }

            if (!removeableNeighbors.isEmpty()) {
                AnrInput anrInput = new AnrInput(hoMetric.getSrcCellId(), removeableNeighbors);
                anrInputList.add(anrInput);
            }
        }
        if (!anrInputList.isEmpty()) {
            return Either.left(anrInputList);
        }
        return Either.right(404);
    }
}