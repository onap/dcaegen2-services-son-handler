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

package org.onap.dcaegen2.services.sonhms.dmaap;

import com.att.nsa.cambria.client.CambriaTopicManager;

import static org.mockito.Mockito.when;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.NewNotification;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DmaapClientTest.class)
public class DmaapClientTest {

	@Mock
	private CambriaTopicManager topicManager;

	
	@InjectMocks
	DmaapClient client;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	private Boolean newNotif;

	@Test
	public void getAllTopicsTest() {
		Set<String> topics = new HashSet<String>();
		topics.add("topic1");
		topics.add("topic2");
		Configuration configuration = Configuration.getInstance();
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
		NewNotification newNotification = new NewNotification(newNotif);
		
		try {
			when(topicManager.getTopics()).thenReturn(topics);
			client=Mockito.mock(DmaapClient.class);
			client.initClient(newNotification);
			Mockito.verify(client).initClient(newNotification);      
	       // Mockito.verifycreateAndConfigureTopics();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
