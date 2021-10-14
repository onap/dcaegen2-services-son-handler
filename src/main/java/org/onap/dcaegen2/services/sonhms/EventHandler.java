/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2019-2021 Wipro Limited.
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

package org.onap.dcaegen2.services.sonhms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fj.data.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.onap.dcaegen2.services.sonhms.child.ChildThread;
import org.onap.dcaegen2.services.sonhms.child.Graph;
import org.onap.dcaegen2.services.sonhms.entity.ClusterDetails;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.model.ClusterMap;
import org.onap.dcaegen2.services.sonhms.exceptions.CpsNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.FapServiceList;
import org.onap.dcaegen2.services.sonhms.model.LteNeighborListInUseLteCell;
import org.onap.dcaegen2.services.sonhms.model.Notification;
import org.onap.dcaegen2.services.sonhms.utils.ClusterUtils;
import org.onap.dcaegen2.services.sonhms.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHandler {

    private static Logger log = LoggerFactory.getLogger(EventHandler.class);

    private static Map<Long, ChildThread> childThreadMap = new HashMap<>();

    private BlockingQueue<List<String>> childStatusQueue;

    private Map<Long, String> childStatus;

    private ExecutorService pool;

    private ClusterUtils clusterUtils;

    private ThreadUtils threadUtils;

    /**
     * Constructor.
     */
    public EventHandler(BlockingQueue<List<String>> childStatusQueue, ExecutorService pool,
            Map<Long, String> childStatus, ClusterUtils clusterUtils, ThreadUtils threadUtils) {

        this.childStatusQueue = childStatusQueue;
        this.childStatus = childStatus;
        this.pool = pool;
        this.clusterUtils = clusterUtils;
        this.threadUtils = threadUtils;
    }

    /**
     * Handles fault notifications.
     */
    public Boolean handleFaultNotification(List<FaultEvent> fmNotification) {

        log.info("Handling Fault notification");
        log.info("fm notification {}", fmNotification);

        Set<String> cellIds = new HashSet<>();
        List<ClusterDetails> clusterDetails = clusterUtils.getAllClusters();
        String networkId = "";
        Map<String, ArrayList<Integer>> collisionConfusionMap = new HashMap<>();
        for (FaultEvent faultEvent : fmNotification) {
            String cellId = faultEvent.getEvent().getCommonEventHeader().getSourceName();
            cellIds.add(cellId);
            networkId = faultEvent.getEvent().getFaultFields().getAlarmAdditionalInformation().getNetworkId();

            ArrayList<Integer> counts = new ArrayList<>();
            counts.add(faultEvent.getEvent().getFaultFields().getEventCategory().contains("PCICollision")?1:0);
            counts.add(faultEvent.getEvent().getFaultFields().getEventCategory().contains("PCIConfusion")?1:0);
            collisionConfusionMap.put(cellId, counts);
        }
        FaultNotificationtoClusterMapping faultNotificationtoClusterMapping = clusterUtils
                .getClustersForFmNotification(cellIds, clusterDetails);
        faultNotificationtoClusterMapping.setCollisionConfusionMap(collisionConfusionMap);

        // matching cells
        if (faultNotificationtoClusterMapping.getCellsinCluster() != null 
                && !faultNotificationtoClusterMapping.getCellsinCluster().isEmpty()) {
            try {
                handleMatchedFmCells(faultNotificationtoClusterMapping, clusterDetails);
            } catch (ConfigDbNotFoundException | CpsNotFoundException e) {
                log.error("Config DB Exception {} or Cps Exception {} ", e);
            }
        }
        // unmatched new cells
        if (faultNotificationtoClusterMapping.getNewCells() != null 
                && !faultNotificationtoClusterMapping.getNewCells().isEmpty()) {
           handleUnmatchedFmCells(faultNotificationtoClusterMapping, networkId);
        }

        return true;
    }

    /**
     * handle matched fm cells.
     * 
     */
    private void handleMatchedFmCells(FaultNotificationtoClusterMapping faultNotificationtoClusterMapping,
            List<ClusterDetails> clusterDetails) throws ConfigDbNotFoundException, CpsNotFoundException {
        Map<String, String> cellsinCluster = faultNotificationtoClusterMapping.getCellsinCluster();
        log.info("Handling Matching cells for FM notification");

        for (Entry<String, String> entry : cellsinCluster.entrySet()) {

            String cellId = entry.getKey();
            String clusterId = entry.getValue();
            Map<CellPciPair, ArrayList<CellPciPair>> clusterMap = clusterUtils.findClusterMap(cellId);

            Either<ClusterDetails, Integer> clusterDetail = clusterUtils.getClusterDetailsFromClusterId(clusterId,
                    clusterDetails);

            if (clusterDetail.isRight()) {
                log.error("Cannot find the cluster for Cluster ID");
                return;
            } else {
                long threadId = clusterDetail.left().value().getChildThreadId();

                if (childStatus.get(threadId).equals("triggeredOof")) {
                    log.info("OOF triggered for the cluster, buffering notification");
                    bufferNotification(clusterMap, clusterId);
                } else {
                    childThreadMap.get(threadId).putInQueue(clusterMap);
                }
            }
        }

    }

    /**
     * handle unmatched fm cells.
     * 
     */
    private void handleUnmatchedFmCells(FaultNotificationtoClusterMapping faultNotificationtoClusterMapping,
            String networkId) {
        List<String> newCells = faultNotificationtoClusterMapping.getNewCells();
        log.info("Handle Unmatching cells for FM notificatins newCells{}", newCells);
        List<Graph> newClusters = new ArrayList<>();

        for (String cellId : newCells) {
            ArrayList<Integer> collisionConfusionCount = faultNotificationtoClusterMapping.getCollisionConfusionMap()
                    .get(cellId);
            log.info("Handle Unmatching cells for FM notificatins,collisionConfusionCount{}", collisionConfusionCount);

            Either<Graph, Integer> existingCluster = clusterUtils.getClusterForFmCell(cellId, newClusters);
            if (existingCluster.isRight()) {
                try {
                    Map<CellPciPair, ArrayList<CellPciPair>> clusterMap = clusterUtils.findClusterMap(cellId);
                    Graph cluster = clusterUtils.createCluster(clusterMap);
                    cluster.setNetworkId(networkId);
                    Map<String, ArrayList<Integer>> collisionConfusionMap = new HashMap<>();
                    collisionConfusionMap.put(cellId, collisionConfusionCount);
                    cluster.setCollisionConfusionMap(collisionConfusionMap);

                    newClusters.add(cluster);
                } catch (ConfigDbNotFoundException | CpsNotFoundException e) {
                    log.error("Error connecting with configDB {}", e);
                }
            }

            else {
                Graph cluster = existingCluster.left().value();

                Graph modifiedCluster = null;
                try {
                    modifiedCluster = clusterUtils.modifyCluster(cluster, clusterUtils.findClusterMap(cellId));
                    Map<String, ArrayList<Integer>> collisionConfusionMap = cluster.getCollisionConfusionMap();
                    collisionConfusionMap.put(cellId, collisionConfusionCount);
                    cluster.setCollisionConfusionMap(collisionConfusionMap);
                } catch (ConfigDbNotFoundException | CpsNotFoundException e) {
                    log.error("Config DB or CPS not found {}", e);
                }
                newClusters.remove(cluster);
                newClusters.add(modifiedCluster);
            }

        }

        // create new child thread
        log.info("New clusters {}", newClusters);

        threadUtils.createNewThread(newClusters, childStatusQueue, pool, this, null);

    }

    /**
     * handle sdnr notification.
     */
    public Boolean handleSdnrNotification(Notification notification) {
        // Check if notification matches with a cluster
        log.info("Handling SDNR notification");
        try {
            List<ClusterDetails> clusterDetails = clusterUtils.getAllClusters();

            NotificationToClusterMapping mapping = clusterUtils.getClustersForNotification(notification,
                    clusterDetails);

            // Matching cells
            if (mapping.getCellsinCluster() != null) {
                handleMatchingCells(mapping.getCellsinCluster(), clusterDetails);
            }

            // unmatched cells
            if (mapping.getNewCells() != null) {
                handleUnMatchingCells(mapping.getNewCells());
            }
        } catch (Exception e) {
            log.error("Exception in sdnr notification handling {}", e);
            return false;
        }

        return true;

    }

    private void handleUnMatchingCells(List<FapServiceList> newCells) throws ConfigDbNotFoundException, CpsNotFoundException {

        log.info("handling unmatched cells");

        List<Graph> newClusters = new ArrayList<>();

        for (FapServiceList fapService : newCells) {

            Map<CellPciPair, ArrayList<CellPciPair>> clusterMap = clusterUtils.findClusterMap(fapService.getAlias());
            Either<Graph, Integer> existingCluster = clusterUtils.getClusterForCell(fapService, newClusters);
            if (existingCluster.isRight()) {
                try {
                    Graph cluster = clusterUtils.createCluster(clusterMap);
                    cluster.setNetworkId(fapService.getCellConfig().getLte().getRan().getNeighborListInUse()
                            .getLteNeighborListInUseLteCell().get(0).getPlmnid());
                    cluster.setCollisionConfusionMap(new HashMap<>());
                    newClusters.add(cluster);
                } catch (ConfigDbNotFoundException | CpsNotFoundException e) {
                    log.error("Error connecting with configDB {} or CPS {}", e);
                }
            }

            else {
                Graph cluster = existingCluster.left().value();
                Graph modifiedCluster = clusterUtils.modifyCluster(cluster,
                        clusterUtils.findClusterMap(fapService.getAlias()));
                newClusters.remove(cluster);
                newClusters.add(modifiedCluster);
            }

        }

        // create new child thread

        threadUtils.createNewThread(newClusters, childStatusQueue, pool, this, null);

    }

    private void handleMatchingCells(Map<FapServiceList, String> cellsInCluster, List<ClusterDetails> clusterDetails)
            throws ConfigDbNotFoundException {

        log.info("handling matching cells");

        for (Entry<FapServiceList, String> entry : cellsInCluster.entrySet()) {

            FapServiceList fapService = entry.getKey();
            String clusterId = entry.getValue();
            String cellId = fapService.getAlias();
            int pci = fapService.getX0005b9Lte().getPhyCellIdInUse();
            ArrayList<CellPciPair> neighbours = new ArrayList<>();
            for (LteNeighborListInUseLteCell neighbour : fapService.getCellConfig().getLte().getRan()
                    .getNeighborListInUse().getLteNeighborListInUseLteCell()) {
                String neighbourCellId = neighbour.getAlias();
                int neighbourPci = neighbour.getPhyCellId();
                neighbours.add(new CellPciPair(neighbourCellId, neighbourPci));

            }
            Map<CellPciPair, ArrayList<CellPciPair>> clusterMap = new HashMap<>();
            clusterMap.put(new CellPciPair(cellId, pci), neighbours);

            Either<ClusterDetails, Integer> clusterDetail = clusterUtils.getClusterDetailsFromClusterId(clusterId,
                    clusterDetails);

            if (clusterDetail.isRight()) {
                log.error("Cannot find the cluster for Cluster ID");
                return;
            } else {
                long threadId = clusterDetail.left().value().getChildThreadId();

                if (childStatus.get(threadId).equals("triggeredOof")) {
                    log.info("OOF triggered for the cluster, buffering notification");

                    bufferNotification(clusterMap, clusterId);
                } else {
                    log.info("Forwarding notification to child thread {}", threadId);
                    childThreadMap.get(threadId).putInQueue(clusterMap);
                }
            }
        }
    }

    private void bufferNotification(Map<CellPciPair, ArrayList<CellPciPair>> clusterMap, String clusterId) {

        log.info("Buffering notifications ...");
        ObjectMapper mapper = new ObjectMapper();
        String serviceListString = "";

        ClusterMap clusterMapJson = new ClusterMap();

        clusterMapJson.setCell(clusterMap.keySet().iterator().next());
        clusterMapJson.setNeighbourList(clusterMap.get(clusterMap.keySet().iterator().next()));

        try {
            serviceListString = mapper.writeValueAsString(clusterMapJson);
        } catch (JsonProcessingException e) {
            log.error("JSON processing exception: {}", e);
        }
        BufferNotificationComponent bufferNotifComponent = new BufferNotificationComponent();
        bufferNotifComponent.bufferNotification(serviceListString, clusterId);

    }

    /**
     * handle child status update.
     */
    public void handleChildStatusUpdate(List<String> childStatus) {

        log.info("Handling child status update");

        Long childThreadId = Long.parseLong(childStatus.get(0));
        addChildStatus(childThreadId, childStatus.get(1));

        // if child status is OOF result success, handle buffered notifications
        if (childStatus.get(1).equals("done")) {
            deleteChildStatus(childThreadId);
        }
        // else kill the child thread

    }

    public static void addChildThreadMap(Long childThreadId, ChildThread child) {
        childThreadMap.put(childThreadId, child);
    }

    public static Map<Long, ChildThread> getChildThreadMap() {
        return childThreadMap;
    }

    public void addChildStatus(Long threadId, String status) {
        this.childStatus.put(threadId, status);
    }

    public String getChildStatus(Long threadId) {
        return childStatus.get(threadId);

    }

    public void deleteChildStatus(Long childThreadId) {
        this.childStatus.remove(childThreadId);

    }

    public ExecutorService getPool() {
        return pool;
    }

}
