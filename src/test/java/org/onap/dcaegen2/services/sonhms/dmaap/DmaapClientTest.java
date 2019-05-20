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

import static org.mockito.Mockito.when;

import com.att.nsa.cambria.client.CambriaTopicManager;

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
import org.mockito.MockitoAnnotations;
import org.onap.dcaegen2.services.sonhms.Configuration;
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

	@Test
	public void getAllTopicsTest() {
		Set<String> topics = new HashSet<String>();
		topics.add("topic1");
		topics.add("topic2");
		Configuration configuration = Configuration.getInstance();
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
		configuration.setPollingInterval(30);
		configuration.setPollingTimeout(100);
		configuration.setConfigDbService("sdnrService");
		configuration.setSourceId("sourceId");
		
		try {
			when(topicManager.getTopics()).thenReturn(topics);
			client=Mockito.mock(DmaapClient.class);
			client.initClient();
			Mockito.verify(client).initClient();      
	       // Mockito.verifycreateAndConfigureTopics();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
