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

import org.onap.dcaegen2.services.sonhms.model.AnrInput;


public class CellInfo {
    String networkId = null;
    List<String> cellIdList = new ArrayList<>();
    protected List<AnrInput> anrInputList = new ArrayList<>();
    String trigger;
    
    
    public List<AnrInput> getAnrInputList() {
        return anrInputList;
    }

    public void setAnrInputList(List<AnrInput> anrInputList) {
        this.anrInputList = anrInputList;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public List<String> getCellIdList() {
        return cellIdList;
    }

    public void setCellIdList(List<String> cellIdList) {
        this.cellIdList = cellIdList;
    }

    

}
