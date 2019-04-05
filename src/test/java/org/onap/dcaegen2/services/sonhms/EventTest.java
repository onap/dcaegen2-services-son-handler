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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.dcaegen2.services.sonhms.CommonEventHeader;
import org.onap.dcaegen2.services.sonhms.FaultEvent;
import org.onap.dcaegen2.services.sonhms.FaultFields;

public class EventTest {

    FaultEvent faultEvent = new FaultEvent();

    @Test
    public void faultEventTest() {
        FaultFields faultFields = new FaultFields();
        faultFields.setAlarmCondition("alarmCondition");
        AlarmAdditionalInformation alarmAdditionalInformation = new AlarmAdditionalInformation();
        alarmAdditionalInformation.setCollisions(1);
        alarmAdditionalInformation.setConfusions(3);
        alarmAdditionalInformation.setNetworkId("networkId");
        faultFields.setAlarmAdditionalInformation(alarmAdditionalInformation);

        faultFields.setEventSeverity("eventSeverity");
        faultFields.setEventSourceType("eventSourceType");
        faultFields.setFaultFieldsVersion(0);
        faultFields.setSpecificProblem("specificProblem");
        faultFields.setVfStatus("vfStatus");

        CommonEventHeader common = new CommonEventHeader();

        common.setDomain("domain");
        common.setEventId("eventId");
        common.setEventName("eventName");
        common.setLastEpochMicrosec(2L);
        common.setNfNamingCode("nfNamingCode");
        common.setNfVendorName("nfVendorName");
        common.setPriority("priority");
        common.setReportingEntityId("reportingEntityId");
        common.setReportingEntityName("reportingEntityName");
        common.setSequence(1);
        common.setSourceId("sourceId");
        common.setSourceName("sourceName");
        common.setStartEpochMicrosec(1L);
        common.setTimeZoneOffset("timeZoneOffset");
        common.setVersion("version");
        common.setVesEventListenerVersion("vesEventListenerVersion");
        Event event=new Event();
        event.setCommonEventHeader(common);
        event.setFaultFields(faultFields);
        faultEvent.setEvent(event);
      
        assertEquals(common, faultEvent.getEvent().getCommonEventHeader());
        assertEquals(faultFields, faultEvent.getEvent().getFaultFields());

    }
}
