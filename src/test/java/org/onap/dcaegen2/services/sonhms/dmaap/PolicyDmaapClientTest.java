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

import static org.junit.Assert.*;

import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.utils.ClusterUtilsTest;
import org.onap.dcaegen2.services.sonhms.utils.DmaapUtils;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = PolicyDmaapClient.class)
public class PolicyDmaapClientTest {
    
    @Mock
    Configuration configurationMock;
    
    @Mock
    DmaapUtils dmaapUtilsMock;
    
    @InjectMocks
    PolicyDmaapClient policyDmaapClient;
    
    @Mock
    CambriaConsumer policyResponseCambriaConsumerMock;
    
    @Mock
    CambriaBatchingPublisher cambriaBatchingPublisherMock;
    
    @Mock
    NotificationProducer notificationProducerMock;
    
    @Before
    public void setup() {
        policyDmaapClient = new PolicyDmaapClient(dmaapUtilsMock, configurationMock);
    }
    
    @Test
    public void handlePolicyResponseTest() {
        
        Map<String, Object> streamsSubscribes = new HashMap<>();
        Map<String, String> topics = new HashMap<>();
        Map<String, Object> dmaapInfo = new HashMap<>();
        topics.put("topic_url", "https://message-router.onap.svc.cluster.local:3905/events/DCAE_CL_RSP");
        dmaapInfo.put("dmaap_info", topics);
        streamsSubscribes.put("dcae_cl_response_topic", dmaapInfo);
        System.out.println(streamsSubscribes);
        String policyResponseString = readFromFile("/policy_response.json");
        List<String> responseMsgs = new ArrayList<>();
        responseMsgs.add(policyResponseString);
        Iterable<String> msgs = () -> new Iterator<String>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return responseMsgs.size() > index;
            }

            @Override
            public String next() {
                return responseMsgs.get(index++);
            }
        };
        Mockito.when(configurationMock.getStreamsSubscribes()).thenReturn(streamsSubscribes);
        Mockito.when(dmaapUtilsMock.buildConsumer(configurationMock, "DCAE_CL_RSP")).thenReturn(policyResponseCambriaConsumerMock);
        try {
            Mockito.when(policyResponseCambriaConsumerMock.fetch()).thenReturn(msgs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(policyDmaapClient.handlePolicyResponse("9d2d790e-a5f0-11e8-98d0-529269fb1459"));
    }
    
    @Test
    public void sendNotificationToPolicyTest() {
        Map<String, Object> streamsPublishes = new HashMap<>();
        Map<String, String> topics = new HashMap<>();
        Map<String, Object> dmaapInfo = new HashMap<>();
        topics.put("topic_url", "https://message-router.onap.svc.cluster.local:3905/events/DCAE_CL_OUTPUT");
        dmaapInfo.put("dmaap_info", topics);
        streamsPublishes.put("CL_topic", dmaapInfo);
        Mockito.when(configurationMock.getStreamsPublishes()).thenReturn(streamsPublishes);
        Mockito.when(dmaapUtilsMock.buildPublisher(configurationMock, "DCAE_CL_OUTPUT")).thenReturn(cambriaBatchingPublisherMock);
        try {
            Mockito.when(cambriaBatchingPublisherMock.send("", "hello")).thenReturn(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(policyDmaapClient.sendNotificationToPolicy("hello"));
        
    }
    
    private static String readFromFile(String file) {
        String content = new String();
        try {

            InputStream is = ClusterUtilsTest.class.getResourceAsStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            content = bufferedReader.readLine();
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                content = content.concat(temp);
            }
            content = content.trim();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            content = null;
        }
        return content;
    }
}
