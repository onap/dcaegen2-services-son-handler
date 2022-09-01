/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2022 Wipro Limited.
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

/**
 * Model class for neighbour details within the payload
 *      for A1 based control loop
 */

public class Neighbours {

    @JsonProperty("PNFName")
    private String pnfName;

    @JsonProperty("PLMNID")
    private String plmnId;

    @JsonProperty("CellID")
    private String cellId;

    @JsonProperty("ho-kpi1")
    private int hoKpi;

    public Neighbours() {

    }

    /**
     * Parameterized Constructor.
     */
    public Neighbours(String pnfName, String plmnId, String cellId, int hoKpi) {
        super();
        this.pnfName = pnfName;
        this.plmnId = plmnId;
        this.cellId = cellId;
        this.hoKpi = hoKpi;
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

    public String getCellId() {
         return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public int getHoKpi() {
        return hoKpi;
    }

    public void setHoKpi(int hoKpi) {
        this.hoKpi = hoKpi;
    }

}
