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

package org.onap.dcaegen2.services.sonhms.model;

import java.util.ArrayList;

public class ClusterMap {
    
    CellPciPair cell;
    ArrayList<CellPciPair> neighbourList;
    
    public ClusterMap() {
        
    }
    
    /**
     * Constructor.
     */
    public ClusterMap(CellPciPair cell, ArrayList<CellPciPair> neighbourList) {
        super();
        this.cell = cell;
        this.neighbourList = neighbourList;
    }

    public CellPciPair getCell() {
        return cell;
    }
    
    public void setCell(CellPciPair cell) {
        this.cell = cell;
    }
    
    public ArrayList<CellPciPair> getNeighbourList() {
        return neighbourList;
    }
    
    public void setNeighbourList(ArrayList<CellPciPair> neighbourList) {
        this.neighbourList = neighbourList;
    }
    
    
}
