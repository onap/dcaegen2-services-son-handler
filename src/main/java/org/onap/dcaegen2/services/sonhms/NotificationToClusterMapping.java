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

import java.util.List;
import java.util.Map;

import org.onap.dcaegen2.services.sonhms.model.FapServiceList;


public class NotificationToClusterMapping {
    
    private Map<FapServiceList, String> cellsinCluster;
    
    private List<FapServiceList> newCells;

    public Map<FapServiceList, String> getCellsinCluster() {
        return cellsinCluster;
    }

    public void setCellsinCluster(Map<FapServiceList, String> cellsinCluster) {
        this.cellsinCluster = cellsinCluster;
    }

    public List<FapServiceList> getNewCells() {
        return newCells;
    }

    public void setNewCells(List<FapServiceList> newCells) {
        this.newCells = newCells;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cellsinCluster == null) ? 0 : cellsinCluster.hashCode());
        result = prime * result + ((newCells == null) ? 0 : newCells.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NotificationToClusterMapping other = (NotificationToClusterMapping) obj;
        if (cellsinCluster == null) {
            if (other.cellsinCluster != null) {
                return false;
            }
        } else if (!cellsinCluster.equals(other.cellsinCluster)) {
            return false;
        }
        if (newCells == null) {
            if (other.newCells != null) {
                return false;
            }
        } else if (!newCells.equals(other.newCells)) {
            return false;
        }
        return true;
    }
    
    
    

}
