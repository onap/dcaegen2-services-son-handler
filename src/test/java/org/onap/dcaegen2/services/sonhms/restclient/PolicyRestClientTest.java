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
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.restclient.PolicyRestClient;
import org.onap.dcaegen2.services.sonhms.utils.SonHandlerRestTemplate;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({ SonHandlerRestTemplate.class, Configuration.class })
@SpringBootTest(classes = PolicyRestClientTest.class)
public class PolicyRestClientTest {

	Configuration configuration = Configuration.getInstance();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void fetchConfigFromPolicyTest() {

		configuration.setBufferTime(60);
		configuration.setCallbackUrl("/callbackUrl");
		configuration.setConfigName("configName");
		List<String> list = new ArrayList<String>();
		list.add("server");
		configuration.setServers(list);
		configuration.setCg("cg");
		configuration.setCid("cid");
		configuration.setManagerApiKey("managerApiKey");
		configuration.setManagerSecretKey("managerSecretKey");
		configuration.setMaximumClusters(5);
		configuration.setMinCollision(5);
		configuration.setMinConfusion(5);
		configuration.setNumSolutions(1);
		configuration.setOofService("oofService");
		configuration.setOptimizers(list);
		configuration.setPcimsApiKey("pcimsApiKey");
		configuration.setPcimsSecretKey("pcimsSecretKey");
		configuration.setPolicyName("policyName");
		configuration.setPolicyService("policyService");
		configuration.setPolicyTopic("policyTopic");
		configuration.setPollingInterval(30);
		configuration.setPollingTimeout(100);
		configuration.setSdnrService("sdnrService");
		configuration.setSdnrTopic("sdnrTopic");
		configuration.setSourceId("sourceId");
        String responseBody="{\n" + 
        		"\"policyName\": \"com.Config_PCIMS_CONFIG_POLICY\",\n" + 
        		"\"policyVersion\": \"1\",\n" + 
        		"\"configBody\": \"{ \\\"PCI_NEIGHBOR_CHANGE_CLUSTER_TIMEOUT_IN_SECS\\\":60,\n" + 
        		"\\\"PCI_MODCONFIG_POLICY_NAME\\\":\\\"ControlLoop-vPCI-fb41f388-a5f2-11e8-98d0-\n" + 
        		"529269fb1459\\\", \\\"PCI_OPTMIZATION_ALGO_CATEGORY_IN_OOF\\\":\\\"OOF-PCI-\n" + 
        		"OPTIMIZATION\\\", \\\"PCI_SDNR_TARGET_NAME\\\":\\\"SDNR\\\" }\",\n" + 
        		"\"policyClass\": \"Config\",\n" + 
        		"\"policyConfigType\": \"Base\",\n" + 
        		"\"ttlDate\": \"2018-08-29T06:28:16.830Z\",\n" + 
        		"\"onapName\": \"DCAE\",\n" + 
        		"\"configName\": \"PCIMS_CONFIG_POLICY\",\n" + 
        		"\"configBodyType\": \"JSON\"\n" + 
        		"}\n" + 
        		"";
        PowerMockito.mockStatic(SonHandlerRestTemplate.class);
		PowerMockito.mockStatic(Configuration.class);
		PowerMockito.when(Configuration.getInstance()).thenReturn(configuration);

		PowerMockito.when(SonHandlerRestTemplate.sendPostToPolicy(Mockito.anyString(),Mockito.anyString() ,Matchers.<ParameterizedTypeReference<String>>any())) 
        .thenReturn(ResponseEntity.ok(responseBody));
		String result=PolicyRestClient.fetchConfigFromPolicy();
		assertEquals(ResponseEntity.ok(responseBody).getBody(), result);
	    


	

	}

}
