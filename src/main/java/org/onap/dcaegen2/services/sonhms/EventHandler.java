/*******************************************************************************
 * ============LICENSE_START=======================================================
 * pcims
 *  ================================================================================
 *  Copyright (C) 2018 Wipro Limited.
 *  ==============================================================================
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   ============LICENSE_END=========================================================
 ******************************************************************************/

package org.onap.dcaegen2.services.sonhms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fj.data.Either;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.onap.dcaegen2.services.sonhms.model.FapServiceList;
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
     * handle sdnr notification.
     */
    public Boolean handleSdnrNotification(Notification notification) {
        // Check if notification matches with a cluster

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

    private void handleUnMatchingCells(List<FapServiceList> newCells) {
        List<Graph> newClusters = new ArrayList<>();

        for (FapServiceList fapService : newCells) {

            Either<Graph, Integer> existingCluster = clusterUtils.getClusterForCell(fapService, newClusters);
            if (existingCluster.isRight()) {
                try {
                    Graph cluster = clusterUtils.createCluster(fapService);
                    newClusters.add(cluster);
                } catch (ConfigDbNotFoundException e) {
                    log.error("Error connecting with configDB {}", e);
                }
            }

            else {
                Graph cluster = existingCluster.left().value();

                Graph modifiedCluster = clusterUtils.modifyCluster(cluster, fapService);
                newClusters.remove(cluster);
                newClusters.add(modifiedCluster);
            }

        }

        // create new child thread
        
        threadUtils.createNewThread(newClusters, childStatusQueue, pool, this);

    }

    private void handleMatchingCells(Map<FapServiceList, String> cellsInCluster, List<ClusterDetails> clusterDetails) {
        for (Entry<FapServiceList, String> entry : cellsInCluster.entrySet()) {

            FapServiceList fapService = entry.getKey();
            String clusterId = entry.getValue();
            Either<ClusterDetails, Integer> clusterDetail = clusterUtils.getClusterDetailsFromClusterId(clusterId,
                    clusterDetails);

            if (clusterDetail.isRight()) {
                log.error("Cannot find the cluster for Cluster ID");
                return;
            } else {
                long threadId = clusterDetail.left().value().getChildThreadId();

                if (childStatus.get(threadId).equals("triggeredOof")) {
                    log.info("OOF triggered for the cluster, buffering notification");
                    bufferNotification(fapService, clusterId);
                } else {
                    childThreadMap.get(threadId).putInQueue(fapService);
                }
            }
        }
    }

    private void bufferNotification(FapServiceList fapService, String clusterId) {
        ObjectMapper mapper = new ObjectMapper();
        BufferNotificationComponent bufferNotifComponent = new BufferNotificationComponent();
        String serviceListString = "";
        try {
            serviceListString = mapper.writeValueAsString(fapService);
        } catch (JsonProcessingException e) {
            log.debug("JSON processing exception: {}", e);
        }
        bufferNotifComponent.bufferNotification(serviceListString, clusterId);

    }

    /**
     * handle child status update.
     */
    public void handleChildStatusUpdate(List<String> childStatus) {

        // update Child status in data structure
        Long childThreadId = Long.parseLong(childStatus.get(0));
        addChildStatus(childThreadId, childStatus.get(1));

        // if child status is OOF result success, handle buffered notifications
        if (childStatus.get(1).equals("success")) {
            BufferNotificationComponent bufferNotificationComponent = new BufferNotificationComponent();
            ClusterDetailsComponent clusterDetailsComponent = new ClusterDetailsComponent();
            String clusterId = clusterDetailsComponent.getClusterId(childThreadId);
            List<String> bufferedNotifications = bufferNotificationComponent.getBufferedNotification(clusterId);

            if (bufferedNotifications == null || bufferedNotifications.isEmpty()) {
                log.info("No buffered notification for this thread");

                Set<Thread> setOfThread = Thread.getAllStackTraces().keySet();
                for (Thread thread : setOfThread) {
                    if (thread.getId() == childThreadId) {
                        deleteChildStatus(childThreadId);
                        thread.interrupt();
                    }
                }
            } else {
                handleBufferedNotifications(childThreadId, bufferedNotifications);
            }
        }
        // else kill the child thread

    }

    private void handleBufferedNotifications(Long childThreadId, List<String> bufferedNotifications) {

        ObjectMapper mapper = new ObjectMapper();
        for (String notification : bufferedNotifications) {
            FapServiceList fapServiceList;
            try {
                fapServiceList = mapper.readValue(notification, FapServiceList.class);
                log.debug("fapServiceList{}", fapServiceList);

                childThreadMap.get(childThreadId).putInQueueWithNotify(fapServiceList);

            } catch (IOException e) {
                log.error("Error parsing the buffered notification, skipping {}", e);
            }
        }
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
