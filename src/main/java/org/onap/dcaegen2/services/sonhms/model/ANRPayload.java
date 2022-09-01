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

public class ANRPayload {

    @JsonProperty("action")
    private String action;

    @JsonProperty("policy_id")
    private int policy_id;

    @JsonProperty("policy_type_id")
    private int policy_type_id;

    @JsonProperty("ric_id")
    private String ric_id;

    @JsonProperty("policy_data")
    private PolicyData policy_data;

    public ANRPayload() {

    }

    public ANRPayload(String action, int policy_id, int policy_type_id, String ric_id, PolicyData policy_data) {
        super();
        this.action = action;
	this.policy_id = policy_id;
	this.policy_type_id = policy_type_id;
	this.ric_id = ric_id;
	this.policy_data=policy_data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getPolicyId(){
	return policy_id;
    }
    public void setPolicyId(int policy_id){
	this.policy_id = policy_id;
    }

    public int getPolicyTypeId(){
        return policy_type_id;
    }

    public void setPolicyTypeId(int policy_type_id){
        this.policy_type_id = policy_type_id;
    }

    public String getRicId(){
        return ric_id;
    }

    public void setRicId(String ric_id){
        this.ric_id = ric_id;
    }

    public PolicyData getPolicyData(){
        return policy_data;
    }

    public void setPolicyData(PolicyData policy_data){
        this.policy_data = policy_data;
    }

    @Override
    public String toString() {
        return "Payload [=" + action + "]";

    }

}
