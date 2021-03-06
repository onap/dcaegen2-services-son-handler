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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;


public class ConfigurationTest {
    Configuration configuration = Configuration.getInstance();

    @Test
    public void configurationTest() {
        configuration.setBufferTime(60);
        configuration.setCallbackUrl("/callbackUrl");

        List<String> list = new ArrayList<String>();
        list.add("server");
        Map<String, Object> subscribes = new HashMap<>();
        
        configuration.setStreamsSubscribes(subscribes);
        configuration.setStreamsPublishes(subscribes);
        configuration.setDmaapServers(list);
        configuration.setCg("cg");
        configuration.setCid("cid");
        configuration.setAafPassword("password");
        configuration.setAafUsername("user");
        configuration.setPgHost("pg");
        configuration.setPgPort(5432);
        configuration.setPgPassword("password");
        configuration.setPgUsername("user");
        configuration.setMaximumClusters(5);
        configuration.setMinCollision(5);
        configuration.setMinConfusion(5);
        configuration.setNumSolutions(1);
        configuration.setOofService("oofService");
        configuration.setOofEndpoint("/api/oof/v1/pci");
        configuration.setOofTriggerCountTimer(30);
        configuration.setOofTriggerCountThreshold(5);
        configuration.setBadThreshold(50);
        configuration.setPoorThreshold(70);
        configuration.setBadCountThreshold(3);
        configuration.setPoorCountThreshold(3);
        configuration.setPciOptimizer("pci");
        configuration.setPciAnrOptimizer("pci-anr");
        configuration.setPollingInterval(30);
        configuration.setPollingTimeout(100);
        configuration.setConfigDbService("sdnrService");
        configuration.setSourceId("sourceId");
        configuration.setPolicyNegativeAckThreshold(3);
        configuration.setPolicyFixedPciTimeInterval(5000);
        assertEquals(60, configuration.getBufferTime());
        assertEquals("/callbackUrl", configuration.getCallbackUrl());
        assertEquals("cg", configuration.getCg());
        assertEquals("cid", configuration.getCid());
        assertEquals("user", configuration.getAafUsername());
        assertEquals("password", configuration.getAafPassword());
        assertEquals(5, configuration.getMaximumClusters());
        assertEquals(5, configuration.getMinCollision());
        assertEquals(5, configuration.getMinConfusion());
        assertEquals(1, configuration.getNumSolutions());
        assertEquals("oofService", configuration.getOofService());
        assertEquals("/api/oof/v1/pci", configuration.getOofEndpoint());
        assertEquals(30, configuration.getOofTriggerCountTimer());
        assertEquals(5, configuration.getOofTriggerCountThreshold());
        assertEquals("pci", configuration.getPciOptimizer());
        assertEquals("pci-anr", configuration.getPciAnrOptimizer());
        assertEquals("user", configuration.getPgUsername());
        assertEquals("password", configuration.getPgPassword());
        assertEquals("pg", configuration.getPgHost());
        assertEquals(5432, configuration.getPgPort());
        assertEquals(30, configuration.getPollingInterval());
        assertEquals(100, configuration.getPollingTimeout());
        assertEquals("sdnrService", configuration.getConfigDbService());
        assertEquals(list, configuration.getDmaapServers());
        assertEquals("sourceId", configuration.getSourceId());
        assertEquals(50, configuration.getBadThreshold());
        assertEquals(70, configuration.getPoorThreshold());
        assertEquals(3, configuration.getBadCountThreshold());
        assertEquals(3, configuration.getPoorCountThreshold());
        assertEquals(subscribes, configuration.getStreamsSubscribes());
        assertEquals(subscribes, configuration.getStreamsPublishes());
        assertEquals(3,configuration.getPolicyNegativeAckThreshold());
        assertEquals(5000, configuration.getPolicyFixedPciTimeInterval());
    }
}
