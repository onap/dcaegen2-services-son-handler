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
public class CommonEventHeader {

    private String version;
    private String vesEventListenerVersion;
    private String domain;
    private String eventName;
    private String eventId;
    private int sequence;
    private String priority;
    private String reportingEntityId;
    private String reportingEntityName;
    private String sourceId;
    private String sourceName;
    private long startEpochMicrosec;
    private long lastEpochMicrosec;
    private String timeZoneOffset;
    private String nfNamingCode;
    private String nfVendorName;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVesEventListenerVersion() {
        return vesEventListenerVersion;
    }

    public void setVesEventListenerVersion(String vesEventListenerVersion) {
        this.vesEventListenerVersion = vesEventListenerVersion;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getReportingEntityId() {
        return reportingEntityId;
    }

    public void setReportingEntityId(String reportingEntityId) {
        this.reportingEntityId = reportingEntityId;
    }

    public String getReportingEntityName() {
        return reportingEntityName;
    }

    public void setReportingEntityName(String reportingEntityName) {
        this.reportingEntityName = reportingEntityName;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public long getStartEpochMicrosec() {
        return startEpochMicrosec;
    }

    public void setStartEpochMicrosec(long startEpochMicrosec) {
        this.startEpochMicrosec = startEpochMicrosec;
    }

    public long getLastEpochMicrosec() {
        return lastEpochMicrosec;
    }

    public void setLastEpochMicrosec(long lastEpochMicrosec) {
        this.lastEpochMicrosec = lastEpochMicrosec;
    }

    public String getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(String timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public String getNfNamingCode() {
        return nfNamingCode;
    }

    public void setNfNamingCode(String nfNamingCode) {
        this.nfNamingCode = nfNamingCode;
    }

    public String getNfVendorName() {
        return nfVendorName;
    }

    public void setNfVendorName(String nfVendorName) {
        this.nfVendorName = nfVendorName;
    }

    @Override
    public String toString() {
        return "CommonEventHeader [version=" + version + ", vesEventListenerVersion=" + vesEventListenerVersion
                + ", domain=" + domain + ", eventName=" + eventName + ", eventId=" + eventId + ", sequence=" + sequence
                + ", priority=" + priority + ", reportingEntityId=" + reportingEntityId + ", reportingEntityName="
                + reportingEntityName + ", sourceId=" + sourceId + ", sourceName=" + sourceName
                + ", startEpochMicrosec=" + startEpochMicrosec + ", lastEpochMicrosec=" + lastEpochMicrosec
                + ", timeZoneOffset=" + timeZoneOffset + ", nfNamingCode=" + nfNamingCode + ", nfVendorName="
                + nfVendorName + "]";
    }

}
