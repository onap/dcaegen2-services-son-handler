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

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.dcaegen2.services.sonhms.exceptions.OofNotFoundException;
import org.onap.dcaegen2.services.sonhms.restclient.OofRestClient;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class) 
@PrepareForTest({OofRestClient.class})
@SpringBootTest(classes = StateOof.class)
public class StateOofTest {

	StateOof oof;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		oof = new StateOof(new LinkedBlockingQueue<>());
	}

	
	UUID transactionId;

	@SuppressWarnings("unchecked")
    @Test
	public void triggerOofTest() {
		ArrayList<String> cellList = new ArrayList<>();
		cellList.add("cell1");
		PowerMockito.mockStatic(OofRestClient.class);
		try {
			PowerMockito.when(OofRestClient.queryOof(Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(),
					Mockito.anyString(), Mockito.anyList(), Mockito.anyList())).thenReturn("oofResponse");
		} catch (OofNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			transactionId = oof.triggerOof(cellList, "networkId", new ArrayList<>());
		} catch (OofNotFoundException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(transactionId);
	}
}
