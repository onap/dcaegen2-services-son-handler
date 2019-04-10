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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.dcaegen2.services.sonhms.dao.HandOverMetricsRepository;
import org.onap.dcaegen2.services.sonhms.dmaap.PolicyDmaapClient;
import org.onap.dcaegen2.services.sonhms.entity.HandOverMetrics;
import org.onap.dcaegen2.services.sonhms.model.Flag;
import org.onap.dcaegen2.services.sonhms.model.PMNotification;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({ BeanUtil.class })
@SpringBootTest(classes = PMNotificationHandlerTest.class)
public class PMNotificationHandlerTest {

    @Mock
    HandOverMetricsRepository handOverMetricsRepositoryMock;
    
    @Mock
    Flag flagMock;
    
    @InjectMocks
    PmNotificationHandler pmNotificationHandler;
    
    @Mock
    PolicyDmaapClient policyDmaapClient;
    
    private static String pmNotificationsString ;
    private static String pmNotificationsString1 ;
    private static PMNotification pmNotification;
    private static PMNotification pmNotification1;

    @BeforeClass
    public static void setup() {
        pmNotificationsString=readFromFile("/pmNotification.json");
        pmNotificationsString1=readFromFile("/pmNotification1.json");
        ObjectMapper mapper = new ObjectMapper();        
        pmNotification = new PMNotification();
        pmNotification1 = new PMNotification();
        
        try {
            pmNotification = mapper.readValue(pmNotificationsString, PMNotification.class);
            pmNotification1 = mapper.readValue(pmNotificationsString1, PMNotification.class);
        } catch(Exception e) {
            e.printStackTrace();      
            }
    }
    
    @Test
    public void handlePmNotificationsTest() {
        PowerMockito.mockStatic(BeanUtil.class);
        PowerMockito.when(BeanUtil
                .getBean(HandOverMetricsRepository.class)).thenReturn(handOverMetricsRepositoryMock);
        PowerMockito.when(BeanUtil
                .getBean(Flag.class)).thenReturn(flagMock);
        when(handOverMetricsRepositoryMock.save(new HandOverMetrics())).thenReturn(null);
        when(flagMock.getHolder()).thenReturn("NONE");
        when(policyDmaapClient.sendNotificationToPolicy(Mockito.anyString())).thenReturn(true);
        assertTrue(pmNotificationHandler.handlePmNotifications(pmNotification, 50));
        assertFalse(pmNotificationHandler.handlePmNotifications(null, 0));
        assertTrue(pmNotificationHandler.handlePmNotifications(pmNotification1, 50));
    }

    private static String readFromFile(String file) { 
        String content = new String();
        try {

            InputStream is = HoMetricsComponentTest.class.getResourceAsStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            content = bufferedReader.readLine();
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                content = content.concat(temp);
            }
            content = content.trim();
            bufferedReader.close();
        } catch (Exception e) {
            content = null;
        }
        return content;
    }
    
}
