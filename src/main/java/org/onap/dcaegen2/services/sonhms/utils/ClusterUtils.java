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

package org.onap.dcaegen2.services.sonhms.utils;

import fj.data.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.onap.dcaegen2.services.sonhms.ClusterDetailsComponent;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.FaultNotificationtoClusterMapping;
import org.onap.dcaegen2.services.sonhms.NotificationToClusterMapping;
import org.onap.dcaegen2.services.sonhms.child.Graph;
import org.onap.dcaegen2.services.sonhms.dao.ClusterDetailsRepository;
import org.onap.dcaegen2.services.sonhms.entity.ClusterDetails;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.exceptions.CpsNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.model.FapServiceList;
import org.onap.dcaegen2.services.sonhms.model.LteNeighborListInUseLteCell;
import org.onap.dcaegen2.services.sonhms.model.Notification;
import org.onap.dcaegen2.services.sonhms.restclient.ConfigurationClient;
import org.onap.dcaegen2.services.sonhms.restclient.CpsClient;
import org.onap.dcaegen2.services.sonhms.restclient.SdnrRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterUtils {

    private static Logger log = LoggerFactory.getLogger(ClusterUtils.class);

    public List<ClusterDetails> getAllClusters() {
        ClusterDetailsComponent clusterDetailsComponent = new ClusterDetailsComponent();
        return clusterDetailsComponent.getClusterDetails();
    }

    /**
     * Get cluster for FM notifications.
     */
    public FaultNotificationtoClusterMapping getClustersForFmNotification(Set<String> cellIds,
                                                                          List<ClusterDetails> clusterDetails) {
        List<String> newCells = new ArrayList<>();
        Map<String, String> cellsInCluster = new HashMap<String, String>();
        FaultNotificationtoClusterMapping faultNotificationtoClusterMapping = new FaultNotificationtoClusterMapping();
        for (String cellId : cellIds) {
            for (ClusterDetails clusterDetail : clusterDetails) {
                Graph cluster = new Graph(clusterDetail.getClusterInfo());

                Set<String> clusterCells = getCellsInCluster(cluster);
                for (String clusterCell : clusterCells) {
                    if (cellId.equals(clusterCell)) {
                        cellsInCluster.put(cellId, clusterDetail.getClusterId());
                        break;

                    }

                }

            }
            if (!cellsInCluster.containsKey(cellId)) {
                newCells.add(cellId);
            }

        }

        faultNotificationtoClusterMapping.setCellsinCluster(cellsInCluster);
        faultNotificationtoClusterMapping.setNewCells(newCells);
        return faultNotificationtoClusterMapping;

    }

    /**
     * Get clusters for notifications.
     */
    public NotificationToClusterMapping getClustersForNotification(Notification notification,
                                                                   List<ClusterDetails> clusterDetails) {

        NotificationToClusterMapping mapping = new NotificationToClusterMapping();

        Map<FapServiceList, String> cellsInCluster = new HashMap<>();
        List<FapServiceList> newCells = new ArrayList<>();

        List<FapServiceList> fapServiceList = notification.getPayload().getRadioAccess().getFapServiceList();

        for (FapServiceList fapService : fapServiceList) {

            for (ClusterDetails clusterDetail : clusterDetails) {

                Set<String> cellsInNotification = getCellsInNotification(fapService);

                Graph cluster = new Graph(clusterDetail.getClusterInfo());

                Set<String> clusterCells = getCellsInCluster(cluster);

                log.debug("cells in cluster {}", clusterCells);

                cellsInNotification.retainAll(clusterCells);

                if (!cellsInNotification.isEmpty()) {
                    log.debug("cell or it's neighbour in the cluster");
                    cellsInCluster.put(fapService, clusterDetail.getClusterId());
                    break;
                }
            }

            if (!cellsInCluster.containsKey(fapService)) {
                newCells.add(fapService);
            }
        }

        mapping.setCellsinCluster(cellsInCluster);
        mapping.setNewCells(newCells);
        return mapping;
    }

    /**
     * Get cluster details from cluster ID.
     */
    public Either<ClusterDetails, Integer> getClusterDetailsFromClusterId(String clusterId,
                                                                          List<ClusterDetails> clusterDetails) {

        for (ClusterDetails clusterDetail : clusterDetails) {
            if (clusterDetail.getClusterId().equals(clusterId)) {
                return Either.left(clusterDetail);
            }
        }
        return Either.right(404);
    }

    /**
     * Get clusters for Cell.
     */
    public Either<Graph, Integer> getClusterForCell(FapServiceList fapService, List<Graph> newClusters) {

        if (newClusters.isEmpty()) {
            return Either.right(404);
        }

        for (Graph cluster : newClusters) {

            Set<String> cellsInNotification = getCellsInNotification(fapService);
            Set<String> clusterCells = getCellsInCluster(cluster);

            cellsInNotification.retainAll(clusterCells);

            if (!(cellsInNotification.isEmpty())) {
                return Either.left(cluster);
            }

        }

        return Either.right(404);
    }

    /**
     * Get clusters for FM Cell.
     */
    public Either<Graph, Integer> getClusterForFmCell(String cellId, List<Graph> newClusters) {
        if (newClusters.isEmpty()) {
            log.info("getClusterForFMCell 404");
            return Either.right(404);
        }
        for (Graph cluster : newClusters) {

            Set<String> clusterCells = getCellsInCluster(cluster);
            for (String clusterCell : clusterCells) {
                if (cellId.equals(clusterCell)) {
                    return Either.left(cluster);

                }

            }

        }
        return Either.right(404);

    }

    private Set<String> getCellsInNotification(FapServiceList fapService) {
        Set<String> cellsInNotification = new HashSet<>();
        cellsInNotification.add(fapService.getAlias());
        List<LteNeighborListInUseLteCell> nbrList = fapService.getCellConfig().getLte().getRan().getNeighborListInUse()
                .getLteNeighborListInUseLteCell();
        for (LteNeighborListInUseLteCell nbr : nbrList) {
            cellsInNotification.add(nbr.getAlias());
        }

        return cellsInNotification;
    }

    private Set<String> getCellsInCluster(Graph cluster) {
        Map<CellPciPair, ArrayList<CellPciPair>> cellPciNeighbourMap = cluster.getCellPciNeighbourMap();
        log.debug("cell_pci_map {}", cellPciNeighbourMap);
        Set<CellPciPair> keys = cellPciNeighbourMap.keySet();
        Set<String> clusterCells = new HashSet<>();
        for (CellPciPair cellPciPair : keys) {
            log.debug("cells {}", cellPciPair.getCellId());
            clusterCells.add(cellPciPair.getCellId());
        }

        return clusterCells;
    }

    /**
     * Create cluster.
     */
    public Graph createCluster(Map<CellPciPair, ArrayList<CellPciPair>> clusterMap) throws ConfigDbNotFoundException, CpsNotFoundException {

        Graph cluster = new Graph();
        log.debug("cluster formation started");

        Set<CellPciPair> keySet = clusterMap.keySet();
        Iterator<CellPciPair> iterate = keySet.iterator();
        CellPciPair val = (CellPciPair) iterate.next();

        List<CellPciPair> firstNeighbourlist = clusterMap.get(val);

        for (int i = 0; i < firstNeighbourlist.size(); i++) {
            String cell = firstNeighbourlist.get(i).getCellId();
            int phy = firstNeighbourlist.get(i).getPhysicalCellId();

            CellPciPair val1 = new CellPciPair();
            val1.setCellId(cell);
            val1.setPhysicalCellId(phy);
            cluster.addEdge(val, val1);

            List<CellPciPair> nbrList = ConfigurationClient.configClient(Configuration.getInstance().getConfigClientType()).getNbrList(cell);

            for (CellPciPair nbr : nbrList) {
                String cid = nbr.getCellId();
                int pci = nbr.getPhysicalCellId();
                CellPciPair val3 = new CellPciPair();
                val3.setCellId(cid);
                val3.setPhysicalCellId(pci);

                cluster.addEdge(val1, val3);
            }
        }

        log.debug("final cluster: {}", cluster);
        return cluster;
    }

    /**
     * Save cluster.
     */
    public String saveCluster(Graph cluster, UUID clusterId, Long threadId) {

        String cellPciNeighbourString = cluster.getPciNeighbourJson();

        log.debug("cluster hahsmap to string : {}", cellPciNeighbourString);
        cluster.setGraphId(clusterId);

        ClusterDetails details = new ClusterDetails();
        details.setClusterId(clusterId.toString());
        details.setClusterInfo(cellPciNeighbourString);
        details.setChildThreadId(threadId);

        ClusterDetailsRepository clusterDetailsRepository = BeanUtil.getBean(ClusterDetailsRepository.class);
        clusterDetailsRepository.save(details);

        return clusterId.toString();
    }

    /**
     * Update cluster.
     */
    public void updateCluster(Graph cluster) {
        String cellPciNeighbourString = cluster.getPciNeighbourJson();
        UUID clusterId = cluster.getGraphId();
        ClusterDetailsRepository clusterDetailsRepository = BeanUtil.getBean(ClusterDetailsRepository.class);
        clusterDetailsRepository.updateCluster(cellPciNeighbourString, clusterId.toString());
    }

    /**
     * Find cluster Map.
     */
    public Map<CellPciPair, ArrayList<CellPciPair>> findClusterMap(String cellId) throws ConfigDbNotFoundException, CpsNotFoundException {
        log.info("indide clusterMap");
        int phyCellId = ConfigurationClient.configClient(Configuration.getInstance().getConfigClientType()).getPci(cellId);
        CellPciPair main = new CellPciPair();
        main.setCellId(cellId);
        main.setPhysicalCellId(phyCellId);
        ArrayList<CellPciPair> cellPciPairs;
        cellPciPairs = (ArrayList<CellPciPair>) ConfigurationClient.configClient(Configuration.getInstance().getConfigClientType()).getNbrList(cellId);
        Map<CellPciPair, ArrayList<CellPciPair>> clusterMap = new HashMap<>();
        clusterMap.put(main, cellPciPairs);
        log.info("clusterMap{}", clusterMap);

        return clusterMap;
    }

    /**
     * Modify cluster.
     */
    public Graph modifyCluster(Graph cluster, Map<CellPciPair, ArrayList<CellPciPair>> clusterMap) {

        Set<CellPciPair> keySet = clusterMap.keySet();
        Iterator<CellPciPair> iterate = keySet.iterator();
        CellPciPair mainCellPciPair = (CellPciPair) iterate.next();
        String cellId = mainCellPciPair.getCellId();
        List<CellPciPair> newNeighbourList = clusterMap.get(mainCellPciPair);

        Map<CellPciPair, ArrayList<CellPciPair>> existingClusterMap;
        existingClusterMap = cluster.getCellPciNeighbourMap();
        // coe

        List<CellPciPair> tempCellPair = new ArrayList<>();
        for (Map.Entry<CellPciPair, ArrayList<CellPciPair>> entry : existingClusterMap.entrySet()) {
            CellPciPair oldClusterKeys = entry.getKey();
            tempCellPair.add(oldClusterKeys);
        }

        for (CellPciPair entry : tempCellPair) {
            String cell = entry.getCellId();
            int physicalCell = entry.getPhysicalCellId();
            CellPciPair mapVal = new CellPciPair();
            mapVal.setCellId(cell);
            mapVal.setPhysicalCellId(physicalCell);

            if (cellId.equals(cell)) {

                // removes the old neighbours and adds new neighbours for that cell
                cluster.updateVertex(mapVal, mainCellPciPair);

            }

        }

        /////// update cluster with new pci values for the same cell

        if (existingClusterMap.containsKey(mainCellPciPair)) {
            ArrayList<CellPciPair> oldClusterArray;
            oldClusterArray = existingClusterMap.get(mainCellPciPair);
            oldClusterArray.clear();

            for (int i = 0; i < newNeighbourList.size(); i++) {
                String cid = newNeighbourList.get(i).getCellId();
                int phy = newNeighbourList.get(i).getPhysicalCellId();
                CellPciPair val2 = new CellPciPair();
                val2.setCellId(cid);
                val2.setPhysicalCellId(phy);
                cluster.addEdge(mainCellPciPair, val2);
            }

        }

        for (CellPciPair entry : tempCellPair) {
            String cell = entry.getCellId();
            int physicalCell = entry.getPhysicalCellId();
            CellPciPair mapVal = new CellPciPair();
            mapVal.setCellId(cell);
            mapVal.setPhysicalCellId(physicalCell);
            for (int j = 0; j < newNeighbourList.size(); j++) {
                String cid1 = newNeighbourList.get(j).getCellId();
                int phy1 = newNeighbourList.get(j).getPhysicalCellId();
                CellPciPair val3 = new CellPciPair();
                val3.setCellId(cid1);
                val3.setPhysicalCellId(phy1);

                if (cid1.equals(cell)) {

                    // removes the old neighbours and adds new neighbours for that cell
                    cluster.updateVertex(mapVal, val3);

                }

            }
        }

        for (int j = 0; j < newNeighbourList.size(); j++) {
            String cid1 = newNeighbourList.get(j).getCellId();
            int phy1 = newNeighbourList.get(j).getPhysicalCellId();
            CellPciPair val3 = new CellPciPair();
            val3.setCellId(cid1);
            val3.setPhysicalCellId(phy1);
            if (existingClusterMap.containsKey(val3)) {
                cluster.addEdge(mainCellPciPair, val3);
            }

        }

        for (int k = 0; k < newNeighbourList.size(); k++) {
            String cid2 = newNeighbourList.get(k).getCellId();
            int phy2 = newNeighbourList.get(k).getPhysicalCellId();
            CellPciPair val5 = new CellPciPair();
            val5.setCellId(cid2);
            val5.setPhysicalCellId(phy2);
            cluster.addEdge(mainCellPciPair, val5);
        }

        log.info("Modified Cluster {}", cluster);

        return cluster;
    }

}