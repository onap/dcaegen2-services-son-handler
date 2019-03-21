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

package org.onap.dcaegen2.services.sonhms.restclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class PolicyRequestBodyTest {
    @Test
    public void policyRequestBodyTest() {
        PolicyRequestBody policyRequestBody = new PolicyRequestBody();
        policyRequestBody.setConfigName("PCIMS_CONFIG_POLICY");
        policyRequestBody.setPolicyName("com.PCIMS_CONFIG_POLICY");
        policyRequestBody.setRequestId("60fe7fe6-2649-4f6c-8468-30eb03fd0527");
        assertEquals("PCIMS_CONFIG_POLICY", policyRequestBody.getConfigName());
        assertEquals("com.PCIMS_CONFIG_POLICY", policyRequestBody.getPolicyName());
        assertEquals("60fe7fe6-2649-4f6c-8468-30eb03fd0527", policyRequestBody.getRequestId());

    }

}
