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

package org.onap.dcaegen2.services.sonhms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FaultFields {

    @Override
    public String toString() {
        return "FaultFields [faultFieldsVersion=" + faultFieldsVersion + ", alarmCondition=" + alarmCondition
                + ", eventSourceType=" + eventSourceType + ", eventCategory=" + eventCategory  + ", specificProblem=" + specificProblem + ", eventSeverity="
                + eventSeverity + ", vfStatus=" + vfStatus + ", alarmAdditionalInformation="
                + alarmAdditionalInformation + "]";
    }

    private double faultFieldsVersion;
    private String alarmCondition;
    private String eventSourceType;
    private String eventCategory;
    private String specificProblem;
    private String eventSeverity;
    private String vfStatus;
    private AlarmAdditionalInformation alarmAdditionalInformation;

    public AlarmAdditionalInformation getAlarmAdditionalInformation() {
        return alarmAdditionalInformation;
    }

    public void setAlarmAdditionalInformation(AlarmAdditionalInformation alarmAdditionalInformation) {
        this.alarmAdditionalInformation = alarmAdditionalInformation;
    }

    public double getFaultFieldsVersion() {
        return faultFieldsVersion;
    }

    public void setFaultFieldsVersion(double faultFieldsVersion) {
        this.faultFieldsVersion = faultFieldsVersion;
    }

    public String getAlarmCondition() {
        return alarmCondition;
    }

    public void setAlarmCondition(String alarmCondition) {
        this.alarmCondition = alarmCondition;
    }

    public String getEventSourceType() {
        return eventSourceType;
    }

    public void setEventSourceType(String eventSourceType) {
        this.eventSourceType = eventSourceType;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }
    
    public String getSpecificProblem() {
        return specificProblem;
    }

    public void setSpecificProblem(String specificProblem) {
        this.specificProblem = specificProblem;
    }

    public String getEventSeverity() {
        return eventSeverity;
    }

    public void setEventSeverity(String eventSeverity) {
        this.eventSeverity = eventSeverity;
    }

    public String getVfStatus() {
        return vfStatus;
    }

    public void setVfStatus(String vfStatus) {
        this.vfStatus = vfStatus;
    }

}
