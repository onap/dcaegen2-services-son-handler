/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2019-2020 Wipro Limited.
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
import org.onap.dcaegen2.services.sonhms.exceptions.OofNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.AnrInput;
import org.onap.dcaegen2.services.sonhms.utils.SonHandlerRestTemplate;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({ SonHandlerRestTemplate.class,Configuration.class })
@SpringBootTest(classes = OofRestClientTest.class)
public class OofRestClientTest {
    Configuration configuration = Configuration.getInstance();
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    
	@Test
	public void queryOofTest() {
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
        configuration.setPciOptimizer("pci");
        configuration.setPollingInterval(30);
        configuration.setPollingTimeout(100);
        configuration.setConfigDbService("sdnrService");
        configuration.setSourceId("sourceId");
        String responseBody="{\n" + 
        		"  \"transactionId\": \"xxx-xxx-xxxx\",\n" + 
        		"  \"requestId\": \"yyy-yyy-yyyy\",\n" + 
        		"  \"requestStatus\": \"accepted\",\n" + 
        		"  \"statusMessage\": \"\"\n" + 
        		"}";
        List<String> cellIdList=new ArrayList<String>();
	    cellIdList.add("EXP001");
	    List<String> optimizers=new ArrayList<String>();
	    optimizers.add("pci"); 
	    List<AnrInput> anrInputList = new ArrayList<>();
	    List<String> fixedPciCells = new ArrayList<>();
        
        PowerMockito.mockStatic(SonHandlerRestTemplate.class);
		PowerMockito.mockStatic(Configuration.class);
		PowerMockito.when(Configuration.getInstance()).thenReturn(configuration);
	    PowerMockito.when(SonHandlerRestTemplate.sendPostRequestToOof(Mockito.anyString(),Mockito.anyString() ,Matchers.<ParameterizedTypeReference<String>>any())) 
	            .thenReturn(ResponseEntity.ok(responseBody));
	    

	    try {
	        
			String result=OofRestClient.queryOof(1, "xxx-xxx-xxxx", "create", cellIdList, "NTWK005", optimizers, anrInputList,fixedPciCells);
			assertEquals(ResponseEntity.ok(responseBody).getBody(), result);
			

		} catch (OofNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    PowerMockito.when(SonHandlerRestTemplate.sendPostRequestToOof(Mockito.anyString(),Mockito.anyString() ,Matchers.<ParameterizedTypeReference<String>>any())) 
        .thenReturn(null);
	    try {
			
	        OofRestClient.queryOof(1, "xxx-xxx-xxxx", "create", cellIdList, "NTWK005", optimizers, new ArrayList<>(),fixedPciCells);

		} catch (OofNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    
	}
	
}


    
