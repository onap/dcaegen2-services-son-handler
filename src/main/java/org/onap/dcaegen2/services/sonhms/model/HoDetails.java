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

public class HoDetails {
    
    private String dstCellId;
    private int attemptsCount;
    private int successCount;
    private float successRate;
    
    /**
     * default constructor
     */
    public HoDetails() {
        
    }
    
    public String getDstCellId() {
        return dstCellId;
    }
    public void setDstCellId(String dstCellId) {
        this.dstCellId = dstCellId;
    }
    public int getAttemptsCount() {
        return attemptsCount;
    }
    public void setAttemptsCount(int attemptsCount) {
        this.attemptsCount = attemptsCount;
    }
    public int getSuccessCount() {
        return successCount;
    }
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    public float getSuccessRate() {
        return successRate;
    }
    public void setSuccessRate(float successRate) {
        this.successRate = successRate;
    }
    

}
