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
import java.util.List;

import org.junit.Test;


public class ConfigurationTest {
    Configuration configuration = Configuration.getInstance();

    @Test
    public void configurationTest() {
        configuration.setBufferTime(60);
        configuration.setCallbackUrl("/callbackUrl");

        List<String> list = new ArrayList<String>();
        list.add("server");
        configuration.setDmaapServers(list);
        configuration.setCg("cg");
        configuration.setCid("cid");

        configuration.setMaximumClusters(5);
        configuration.setMinCollision(5);
        configuration.setMinConfusion(5);
        configuration.setNumSolutions(1);
        configuration.setOofService("oofService");
        configuration.setOptimizers(list);
        configuration.setPollingInterval(30);
        configuration.setPollingTimeout(100);
        configuration.setConfigDbService("sdnrService");
        configuration.setSourceId("sourceId");
        assertEquals(60, configuration.getBufferTime());
        assertEquals("/callbackUrl", configuration.getCallbackUrl());
        assertEquals("cg", configuration.getCg());
        assertEquals("cid", configuration.getCid());

        assertEquals(5, configuration.getMaximumClusters());
        assertEquals(5, configuration.getMinCollision());
        assertEquals(5, configuration.getMinConfusion());
        assertEquals(1, configuration.getNumSolutions());
        assertEquals("oofService", configuration.getOofService());
        assertEquals(list, configuration.getOptimizers());

        assertEquals(30, configuration.getPollingInterval());
        assertEquals(100, configuration.getPollingTimeout());
        assertEquals("sdnrService", configuration.getConfigDbService());
        assertEquals(list, configuration.getDmaapServers());
        assertEquals("sourceId", configuration.getSourceId());
    }
}
