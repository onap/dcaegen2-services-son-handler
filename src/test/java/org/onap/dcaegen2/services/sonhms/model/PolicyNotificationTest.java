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

package org.onap.dcaegen2.services.sonhms.model;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.onap.dcaegen2.services.sonhms.utils.ClusterUtilsTest;

public class PolicyNotificationTest {
	 @Test
	 public void policyNotificationTest() {
		 PolicyNotification policyNotification=new PolicyNotification();
		 Map<String, String> aai=new HashMap<>();
		 aai.put("test","test");
		 String target="test";
		 String targetType="targetType";
		 String payload="payload";
		 String closedLoopControlName="closedLoopControlName";
		 String action="action";
		 String version="version";
		 String from="from";
		 String requestId="requestId";
		 String closedLoopEventStatus="closedLoopEventStatus";
		 String closedLoopEventClient="closedLoopEventClient";
		 long closedLoopAlarmStart=96587958;
		 policyNotification.setAai(aai);
		 assertEquals(aai,policyNotification.getAai());
		 policyNotification.setTarget(target);
		 assertEquals(target,policyNotification.getTarget());
		 policyNotification.setTargetType(targetType);
		 assertEquals(targetType,policyNotification.getTargetType());
		 policyNotification.setPayload(payload);
		 assertEquals(payload,policyNotification.getPayload());
		 policyNotification.setClosedLoopControlName(closedLoopControlName);
		 assertEquals(closedLoopControlName,policyNotification.getClosedLoopControlName());
		 policyNotification.setAction(action);
		 assertEquals(action,policyNotification.getAction());
		 policyNotification.setVersion(version);
		 assertEquals(version,policyNotification.getVersion());
		 policyNotification.setFrom(from);
		 assertEquals(from,policyNotification.getFrom());
		 policyNotification.setRequestId(requestId);
		 assertEquals(requestId,policyNotification.getRequestId());
		 policyNotification.setClosedLoopEventStatus(closedLoopEventStatus);
		 assertEquals(closedLoopEventStatus,policyNotification.getClosedLoopEventStatus());
		 policyNotification.setClosedLoopEventClient(closedLoopEventClient);
		 assertEquals(closedLoopEventClient,policyNotification.getClosedLoopEventClient());
		 policyNotification.setClosedLoopAlarmStart(closedLoopAlarmStart);
		 assertEquals(closedLoopAlarmStart,policyNotification.getClosedLoopAlarmStart());
		 
		 String notif1 = readFromFile("/policy_notification.json");
		 String notif2 = readFromFile("/policy_notification.json");
		 PolicyNotification policyNotification1 = new PolicyNotification();
		 PolicyNotification policyNotification2 = new PolicyNotification();
		 PolicyNotification policyNotification3 = new PolicyNotification();
		 ObjectMapper mapper = new ObjectMapper();
		 
		 try {
            policyNotification1 = mapper.readValue(notif1, PolicyNotification.class);
            policyNotification2 = mapper.readValue(notif2, PolicyNotification.class);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		Assert.assertEquals(policyNotification1.hashCode(), policyNotification2.hashCode());
		Assert.assertEquals(policyNotification1, policyNotification2);
		Assert.assertTrue(policyNotification1.equals(policyNotification2));
		Assert.assertFalse(policyNotification1.equals(null));
		Assert.assertNotEquals(policyNotification1.hashCode(), policyNotification3.hashCode());
		 
	 }

	 private static String readFromFile(String file) {
	        String content  = new String();
	        try {
	            
	            InputStream is = ClusterUtilsTest.class.getResourceAsStream(file);
	            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
	            content = bufferedReader.readLine();
	            String temp;
	            while((temp = bufferedReader.readLine()) != null) {
	                content = content.concat(temp);
	            }
	            content = content.trim();
	            bufferedReader.close();
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	            content  = null;
	        }
	        return content;
	    }
}
