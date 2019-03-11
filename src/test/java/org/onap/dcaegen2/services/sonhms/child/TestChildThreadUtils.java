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

package org.onap.dcaegen2.services.sonhms.child;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.onap.dcaegen2.services.sonhms.ConfigPolicy;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.model.PolicyNotification;
import org.onap.dcaegen2.services.sonhms.utils.ClusterUtilsTest;

public class TestChildThreadUtils {
    
    ChildThreadUtils childThreadUtils;
    
    @Before
    public void setup() {
        
        ConfigPolicy configPolicy = ConfigPolicy.getInstance();
        
        Map<String, Object> configPolicyMap = new HashMap<>();
        configPolicyMap.put("PCI_MODCONFIG_POLICY_NAME", "ControlLoop-vPCI-fb41f388-a5f2-11e8-98d0-529269fb1459");
        configPolicy.setConfig(configPolicyMap);
        childThreadUtils = new ChildThreadUtils(configPolicy);
    }
    
    @Test
    public void getNotificationStringTest() {
        
        String policy_notif = readFromFile("/policy_notification.json");
        PolicyNotification expected = new PolicyNotification();
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            expected = mapper.readValue(policy_notif, PolicyNotification.class);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String pnfName = "ncserver23";
        List<CellPciPair> cellPciPairs = new ArrayList<>();
        
        cellPciPairs.add(new CellPciPair("Chn0330", 6));
        cellPciPairs.add(new CellPciPair("Chn0331", 7));
        String requestId = "a4130fd5-2291-4a83-8992-04e4c9f32731";
        Long alarmStart = Long.parseLong("1542445563201");
        
        String result = childThreadUtils.getNotificationString(pnfName, cellPciPairs, requestId, alarmStart);
        PolicyNotification actual = new PolicyNotification();
        try {
            actual = mapper.readValue(result, PolicyNotification.class);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Assert.assertEquals(expected.hashCode(), actual.hashCode());
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
