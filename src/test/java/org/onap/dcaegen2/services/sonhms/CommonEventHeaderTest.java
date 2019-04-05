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

public class CommonEventHeaderTest {

    CommonEventHeader common = new CommonEventHeader();

    @Test
    public void commonEventHeaderTest() {
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
        assertEquals("domain", common.getDomain());
        assertEquals("eventId", common.getEventId());
        assertEquals("eventName", common.getEventName());
        assertEquals(2L, common.getLastEpochMicrosec());
        assertEquals("nfNamingCode", common.getNfNamingCode());
        assertEquals("nfVendorName", common.getNfVendorName());
        assertEquals("priority", common.getPriority());
        assertEquals("reportingEntityId", common.getReportingEntityId());
        assertEquals("reportingEntityName", common.getReportingEntityName());
        assertEquals("sourceId", common.getSourceId());
        assertEquals("sourceName", common.getSourceName());
        assertEquals("timeZoneOffset", common.getTimeZoneOffset());
        assertEquals("version", common.getVersion());
        assertEquals("vesEventListenerVersion", common.getVesEventListenerVersion());
        assertEquals(1L, common.getStartEpochMicrosec());
        assertEquals(1, common.getSequence());

    }

}
