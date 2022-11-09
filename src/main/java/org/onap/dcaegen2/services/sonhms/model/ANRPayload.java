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
 *  Model class for payload for A1 based control loop
 */

public class ANRPayload {

  @JsonProperty("policy_id")
  private String policy_id;

  @JsonProperty("policytype_id")
  private String policy_type_id;

  @JsonProperty("ric_id")
  private String ric_id;

  @JsonProperty("policy_data")
  private PolicyData policy_data;

  @JsonProperty("service_id")
  private String service_id;

  @JsonProperty("transient")
  private boolean transient1;

  @JsonProperty("status_notification_uri")
  private String status;

  /**
   * Constructor
   */

  public ANRPayload() {

  }

    

  public ANRPayload(String policy_id, String policy_type_id, String ric_id, PolicyData policy_data,
      String service_id, boolean transient1, String status) {
    super();
    this.policy_id = policy_id;
    this.policy_type_id = policy_type_id;
    this.ric_id = ric_id;
    this.policy_data = policy_data;
    this.service_id = service_id;
    this.transient1 = transient1;
    this.status = status;
  }

  public String getPolicy_id() {
    return policy_id;
  }



  public void setPolicy_id(String policy_id) {
    this.policy_id = policy_id;
  }



  public String getPolicy_type_id() {
    return policy_type_id;
  }



  public void setPolicy_type_id(String policy_type_id) {
    this.policy_type_id = policy_type_id;
  }



  public String getRic_id() {
    return ric_id;
  }



  public void setRic_id(String ric_id) {
    this.ric_id = ric_id;
  }



  public PolicyData getPolicy_data() {
    return policy_data;
  }



  public void setPolicy_data(PolicyData policy_data) {
    this.policy_data = policy_data;
  }



  public String getService_id() {
    return service_id;
  }



  public void setService_id(String service_id) {
    this.service_id = service_id;
  }



  public boolean isTransient1() {
    return transient1;
  }



  public void setTransient1(boolean transient1) {
    this.transient1 = transient1;
  }



  public String getStatus() {
    return status;
  }



  public void setStatus(String status) {
    this.status = status;
  }



  @Override
  public String toString() {
    return "ANRPayload [ policy_id=" + policy_id + ", policy_type_id=" + policy_type_id
        + ", ric_id=" + ric_id + ", policy_data=" + policy_data + ", service_id=" + service_id + ", transient1="
        + transient1 + ", status=" + status + "]";
  }

   

}

