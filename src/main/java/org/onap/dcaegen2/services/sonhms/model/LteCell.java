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

public class LteCell {

    @JsonProperty("PNFName")
    private String pnfName;

    @JsonProperty("PLMNID")
    private String plmnId;

    @JsonProperty("CID")
    private String cid;

    @JsonProperty("PhyCellID")
    private int phyCellId;

    @JsonProperty("Blacklisted")
    private String blacklisted;
    
    public LteCell() {
        
    }

    public String getPnfName() {
        return pnfName;
    }

    public void setPnfName(String pnfName) {
        this.pnfName = pnfName;
    }

    public String getPlmnId() {
        return plmnId;
    }

    public void setPlmnId(String plmnId) {
        this.plmnId = plmnId;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getPhyCellId() {
        return phyCellId;
    }

    public void setPhyCellId(int phyCellId) {
        this.phyCellId = phyCellId;
    }

    public String getBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(String blacklisted) {
        this.blacklisted = blacklisted;
    }
    
}