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

package org.onap.dcaegen2.services.sonhms.restclient;

import java.util.ArrayList;
import java.util.List;

public class AnrSolutions {

    String cellId;
    List<String> removeableNeighbors = new ArrayList<>();

    public AnrSolutions() {

    }

    /**
     * constructor.
     */
    public AnrSolutions(String cellId, List<String> removeableNeighbors) {
        super();
        this.cellId = cellId;
        this.removeableNeighbors = removeableNeighbors;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public List<String> getRemoveableNeighbors() {
        return removeableNeighbors;
    }

    public void setRemoveableNeighbors(List<String> removeableNeighbors) {
        this.removeableNeighbors = removeableNeighbors;
    }

    @Override
    public String toString() {
        return "AnrSolutions [cellId=" + cellId + ", removeableNeighbors=" + removeableNeighbors + "]";
    }

}
