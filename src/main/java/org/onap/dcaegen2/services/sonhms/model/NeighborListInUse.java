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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class NeighborListInUse {
    @JsonProperty("LTENeighborListInUseLTECell")
    private List<LteNeighborListInUseLteCell> lteNeighborListInUseLteCell;

    @JsonProperty("LTECell")
    private List<LteCell> lteCell;
    
    @JsonProperty("LTECellNumberOfEntries")
    private String lteCellNumberOfEntries;

    public NeighborListInUse() {

    }

    /**
     * Parameterized Constructor.
     */
    public NeighborListInUse(List<LteNeighborListInUseLteCell> lteNeighborListInUseLteCell, List<LteCell> lteCell,
            String lteCellNumberOfEntries) {
        super();
        this.lteNeighborListInUseLteCell = lteNeighborListInUseLteCell;
        this.lteCell = lteCell;
        this.lteCellNumberOfEntries = lteCellNumberOfEntries;
    }
   

    public List<LteNeighborListInUseLteCell> getLteNeighborListInUseLteCell() {
        return lteNeighborListInUseLteCell;
    }

    public void setLteNeighborListInUseLteCell(List<LteNeighborListInUseLteCell> lteNeighborListInUseLteCell) {
        this.lteNeighborListInUseLteCell = lteNeighborListInUseLteCell;
    }

    public String getLteCellNumberOfEntries() {
        return lteCellNumberOfEntries;
    }

    public void setLteCellNumberOfEntries(String lteCellNumberOfEntries) {
        this.lteCellNumberOfEntries = lteCellNumberOfEntries;
    }

}
