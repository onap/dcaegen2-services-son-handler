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

import java.util.HashMap;
import java.util.Map;

public class PolicyNotification {

    private String closedLoopControlName;
    private long closedLoopAlarmStart;
    private String closedLoopEventClient;
    private String closedLoopEventStatus;

    @JsonProperty("target_type")
    private String targetType;

    private String target;

    @JsonProperty("requestID")
    private String requestId;

    @JsonProperty("AAI")
    private Map<String, String> aai;

    private String from;
    private String version;

    @JsonProperty("Action")
    private String action;
    private String payload;

    /**
     * constructor.
     */
    public PolicyNotification() {

    }

    /**
     * Constructor.
     *
     */
    public PolicyNotification(String closedLoopControlName, String requestId, Long alarmStartTime, String pnfName) {
        this.closedLoopControlName = closedLoopControlName;
        this.requestId = requestId;
        this.closedLoopEventClient = "microservice.PCI";
        this.closedLoopEventStatus = "ONSET";
        this.closedLoopAlarmStart = alarmStartTime;
        this.from = "PCIMS";
        this.version = "1.0.2";
        this.action = "ModifyConfig";
        this.target = "generic-vnf.vnf-id";
        this.targetType = "VNF";
        this.aai = new HashMap<>();
        aai.put("generic-vnf.is-closed-loop-disabled", "false");
        aai.put("generic-vnf.prov-status", "ACTIVE");
        aai.put("generic-vnf.vnf-id", pnfName);
    }

    public long getClosedLoopAlarmStart() {
        return closedLoopAlarmStart;
    }

    public void setClosedLoopAlarmStart(long closedLoopAlarmStart) {
        this.closedLoopAlarmStart = closedLoopAlarmStart;
    }

    public String getClosedLoopEventClient() {
        return closedLoopEventClient;
    }

    public void setClosedLoopEventClient(String closedLoopEventClient) {
        this.closedLoopEventClient = closedLoopEventClient;
    }

    public String getClosedLoopEventStatus() {
        return closedLoopEventStatus;
    }

    public void setClosedLoopEventStatus(String closedLoopEventStatus) {
        this.closedLoopEventStatus = closedLoopEventStatus;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getClosedLoopControlName() {
        return closedLoopControlName;
    }

    public void setClosedLoopControlName(String closedLoopControlName) {
        this.closedLoopControlName = closedLoopControlName;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Map<String, String> getAai() {
        return aai;
    }

    public void setAai(Map<String, String> aai) {
        this.aai = aai;
    }

    @Override
    public String toString() {
        return "PolicyNotification [closedLoopControlName=" + closedLoopControlName + ", closedLoopAlarmStart="
                + closedLoopAlarmStart + ", closedLoopEventClient=" + closedLoopEventClient + ", closedLoopEventStatus="
                + closedLoopEventStatus + ", targetType=" + targetType + ", target=" + target + ", requestId="
                + requestId + ", aai=" + aai + ", from=" + from + ", version=" + version + ", action=" + action
                + ", payload=" + payload + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((aai == null) ? 0 : aai.hashCode());
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + (int) (closedLoopAlarmStart ^ (closedLoopAlarmStart >>> 32));
        result = prime * result + ((closedLoopControlName == null) ? 0 : closedLoopControlName.hashCode());
        result = prime * result + ((closedLoopEventClient == null) ? 0 : closedLoopEventClient.hashCode());
        result = prime * result + ((closedLoopEventStatus == null) ? 0 : closedLoopEventStatus.hashCode());
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((payload == null) ? 0 : payload.hashCode());
        result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        result = prime * result + ((targetType == null) ? 0 : targetType.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

}
