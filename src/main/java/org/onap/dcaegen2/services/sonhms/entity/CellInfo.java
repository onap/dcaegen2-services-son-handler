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

package org.onap.dcaegen2.services.sonhms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CELL_INFO")
public class CellInfo {

    @Id
    @Column(name = "CELL_ID")
    private String cellId;

    @Column(name = "PNF_NAME")
    private String pnfName;

    public CellInfo() {

    }

    /**
     * CellIdPnf Constructor.
     */

    public CellInfo(String cellId, String pnfName) {
        super();
        this.cellId = cellId;
        this.pnfName = pnfName;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getPnfName() {
        return pnfName;
    }

    public void setPnfName(String pnfName) {
        this.pnfName = pnfName;
    }

}
