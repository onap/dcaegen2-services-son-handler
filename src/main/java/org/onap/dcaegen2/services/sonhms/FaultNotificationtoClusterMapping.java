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

package org.onap.dcaegen2.services.sonhms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FaultNotificationtoClusterMapping {
    public Map<String, String> getCellsinCluster() {
        return cellsinCluster;
    }

    public void setCellsinCluster(Map<String, String> cellsinCluster) {
        this.cellsinCluster = cellsinCluster;
    }

    public List<String> getNewCells() {
        return newCells;
    }

    public void setNewCells(List<String> newCells) {
        this.newCells = newCells;
    }

    private Map<String, ArrayList<Integer>> collisionConfusionMap;

    // map that returns cellid and its matching cluster id

    public Map<String, ArrayList<Integer>> getCollisionConfusionMap() {
        return collisionConfusionMap;
    }

    public void setCollisionConfusionMap(Map<String, ArrayList<Integer>> collisionConfusionMap) {
        this.collisionConfusionMap = collisionConfusionMap;
    }

    private Map<String, String> cellsinCluster;

    // cells that dont match
    private List<String> newCells;
}
