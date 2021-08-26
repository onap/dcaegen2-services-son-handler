/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2021 Wipro Limited.
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.exceptions.CpsNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.utils.SonHandlerRestTemplate;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*" })
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({ SonHandlerRestTemplate.class, Configuration.class })
@SpringBootTest(classes = CpsClientTest.class)
public class CpsClientTest {

    CpsClient cps = new CpsClient();
    Configuration configuration = Configuration.getInstance();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CpsClient.class);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getNbrListTest() {

        String responseBody = "[{\"idNRCellRelation\":\"cell1\",\"attributes\":{\"nRTCI\":\"cell1\",\"nRPCI\":1,\"isHOAllowed\":true}},{\"idNRCellRelation\":\"cell2\",\"attributes\":{\"nRTCI\":\"cell2\",\"nRPCI\":2,\"isHOAllowed\":true}}]";

        System.out.println(responseBody);
        PowerMockito.mockStatic(SonHandlerRestTemplate.class);
        PowerMockito.mockStatic(Configuration.class);
        PowerMockito.when(Configuration.getInstance()).thenReturn(configuration);
        PowerMockito
                .when(SonHandlerRestTemplate.sendPostRequest(Mockito.anyString(), Mockito.anyString(),
                        Matchers.<ParameterizedTypeReference<String>>any()))
                .thenReturn(ResponseEntity.ok(responseBody));
        try {
            List<CellPciPair> result = cps.getNbrList("1");
            List<CellPciPair> nbrList = new ArrayList<>();
            String response = ResponseEntity.ok(responseBody).getBody();
            System.out.println("response" + response);
            JSONArray nbrListObj = new JSONArray(response);
            System.out.println(nbrListObj);
            for (int i = 0; i < nbrListObj.length(); i++) {
                JSONObject cellObj = nbrListObj.getJSONObject(i);
                JSONObject obj = cellObj.getJSONObject("attributes");
                if (obj.getBoolean("isHOAllowed")) {
                    CellPciPair cell = new CellPciPair(obj.getString("nRTCI"), obj.getInt("nRPCI"));
                    nbrList.add(cell);
                }
            }
            assertEquals(nbrList, result);
        } catch (Exception e) {
            log.debug("CpsNotFoundException {}", e.toString());
            ;
        }

    }

    @Test
    public void getPciTest() {

        String responseBody = "{\n" + "  \"attribute-name\": \"string\",\n" + "  \"value\": 0\n" + "}";
        PowerMockito.mockStatic(SonHandlerRestTemplate.class);
        PowerMockito.mockStatic(Configuration.class);
        PowerMockito.when(Configuration.getInstance()).thenReturn(configuration);
        PowerMockito
                .when(SonHandlerRestTemplate.sendPostRequest(Mockito.anyString(), Mockito.anyString(),
                        Matchers.<ParameterizedTypeReference<String>>any()))
                .thenReturn(ResponseEntity.ok(responseBody));

        try {
            int result = cps.getPci("1");
            String response = ResponseEntity.ok(responseBody).getBody();
            JSONObject respObj = new JSONObject(response);
            assertEquals(respObj.getInt("value"), result);
        } catch (CpsNotFoundException e) {
            log.debug("CpsNotFoundException {}", e.toString());
            ;
        }

    }

    @Test
    public void getPnfNameTest() {

        String responseBody = "{\n" + "  \"attribute-name\": \"string\",\n" + "  \"value\": \"string\"\n" + "}";
        PowerMockito.mockStatic(SonHandlerRestTemplate.class);
        PowerMockito.mockStatic(Configuration.class);
        PowerMockito.when(Configuration.getInstance()).thenReturn(configuration);
        PowerMockito
                .when(SonHandlerRestTemplate.sendPostRequest(Mockito.anyString(), Mockito.anyString(),
                        Matchers.<ParameterizedTypeReference<String>>any()))
                .thenReturn(ResponseEntity.ok(responseBody));
        try {
            String result = cps.getPnfName("1");
            String response = ResponseEntity.ok(responseBody).getBody();
            JSONObject respObj = new JSONObject(response);
            assertEquals(respObj.getString("value"), result);
        } catch (CpsNotFoundException e) {
            log.debug("CpsNotFoundException {}", e.toString());
            ;
        }
    }

    @Test
    public void getCellData() {
        String responseBody = "{\"networkId\":\"netw1000\"}";
        PowerMockito.mockStatic(SonHandlerRestTemplate.class);
        PowerMockito.mockStatic(Configuration.class);
        PowerMockito.when(Configuration.getInstance()).thenReturn(configuration);
        PowerMockito
                .when(SonHandlerRestTemplate.sendPostRequest(Mockito.anyString(), Mockito.anyString(),
                        Matchers.<ParameterizedTypeReference<String>>any()))
                .thenReturn(ResponseEntity.ok(responseBody));
        try {
            JSONObject result = cps.getCellData("1");
            String response = ResponseEntity.ok(responseBody).getBody();
            JSONObject respObj = new JSONObject(response);
            assertEquals(respObj.get("networkId"), result.get("networkId"));
        } catch (CpsNotFoundException e) {
            log.debug("CpsNotFoundException {}", e.toString());

        }

    }

}
