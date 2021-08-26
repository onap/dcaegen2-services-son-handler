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

package org.onap.dcaegen2.services.sonhms.child;

import com.fasterxml.jackson.databind.ObjectMapper;
import fj.data.Either;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.dcaegen2.services.sonhms.ConfigPolicy;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.HoMetricsComponent;
import org.onap.dcaegen2.services.sonhms.dao.HandOverMetricsRepository;
import org.onap.dcaegen2.services.sonhms.dao.SonRequestsRepository;
import org.onap.dcaegen2.services.sonhms.dmaap.PolicyDmaapClient;
import org.onap.dcaegen2.services.sonhms.entity.SonRequests;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.exceptions.CpsNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.model.HoDetails;
import org.onap.dcaegen2.services.sonhms.model.PolicyNotification;
import org.onap.dcaegen2.services.sonhms.restclient.AsyncResponseBody;
import org.onap.dcaegen2.services.sonhms.restclient.ConfigInterface;
import org.onap.dcaegen2.services.sonhms.restclient.ConfigurationClient;
import org.onap.dcaegen2.services.sonhms.restclient.SdnrRestClient;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.onap.dcaegen2.services.sonhms.utils.ClusterUtilsTest;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({ BeanUtil.class, SdnrRestClient.class, ConfigurationClient.class })
@SpringBootTest(classes = TestChildThreadUtils.class)
public class TestChildThreadUtils {

	ChildThreadUtils childThreadUtils;

	@Mock
	private SonRequestsRepository repository;

	@Mock
	private PnfUtils pnfUtils;
	@Mock
	private PolicyDmaapClient policyDmaapClient;

	@Mock
	private HandOverMetricsRepository hoMetricRepository;
	
	@Mock
	private HoMetricsComponent hoMetricsComponent;

	@InjectMocks
	private ChildThreadUtils childThreadUtils2;


	@Before
	public void setup() {

		ConfigPolicy configPolicy = ConfigPolicy.getInstance();
		Configuration config = Configuration.getInstance();
		config.setMinCollision(5);
		config.setMinConfusion(5);
		config.setConfigClientType("ConfigDB");
		Map<String, Object> configPolicyMap = new HashMap<>();
		configPolicyMap.put("PCI_MODCONFIG_POLICY_NAME", "ControlLoop-vPCI-fb41f388-a5f2-11e8-98d0-529269fb1459");
		configPolicy.setConfig(configPolicyMap);
		childThreadUtils = new ChildThreadUtils(configPolicy, pnfUtils,  policyDmaapClient, hoMetricsComponent);
		MockitoAnnotations.initMocks(this);
		
	}

	@Test
	public void savePciRequestTest() {
		SonRequests sonRequest = new SonRequests();
		sonRequest.setTransactionId("transactionId");
		sonRequest.setChildThreadId(1L);
		PowerMockito.mockStatic(BeanUtil.class);
		PowerMockito.when(BeanUtil.getBean(SonRequestsRepository.class))
				.thenReturn(repository);
		when(repository.save(sonRequest)).thenReturn(sonRequest);
		childThreadUtils2.saveRequest("transactionId",1L);;
		assertEquals(sonRequest, repository.save(sonRequest));
	}

	@Test
	public void triggerOrWaitTest() {
		Map<String, ArrayList<Integer>> collisionConfusionResult = new HashMap<String, ArrayList<Integer>>();
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(6);
		list.add(7);
		collisionConfusionResult.put("cellId", list);

		Boolean result = childThreadUtils.triggerOrWait(collisionConfusionResult);
		assertTrue(result);
		Map<String, ArrayList<Integer>> collisionConfusionResult1 = new HashMap<String, ArrayList<Integer>>();

		ArrayList<Integer> list1 = new ArrayList<Integer>();
		list1.add(1);
		list1.add(2);
		collisionConfusionResult1.put("cell1", list1);
		result = childThreadUtils.triggerOrWait(collisionConfusionResult1);
		assertFalse(result);

	}

	@Test
	public void getNotificationStringTest() {

		String policy_notif = readFromFile("/policy_notification.json");
		PolicyNotification expected = new PolicyNotification();
		ObjectMapper mapper = new ObjectMapper();

		try {
			expected = mapper.readValue(policy_notif, PolicyNotification.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String pnfName = "ncserver23";
		List<CellPciPair> cellPciPairs = new ArrayList<>();

		cellPciPairs.add(new CellPciPair("Chn0330", 6));
		cellPciPairs.add(new CellPciPair("Chn0331", 7));
		String requestId = "a4130fd5-2291-4a83-8992-04e4c9f32731";
		Long alarmStart = Long.parseLong("1542445563201");

		String result = childThreadUtils.getNotificationString(pnfName, requestId, "payloadString", alarmStart, "ModifyConfig");
		PolicyNotification actual = new PolicyNotification();
		try {
			actual = mapper.readValue(result, PolicyNotification.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("actual :; "+result+"\nexp "+policy_notif);
		Assert.assertEquals(expected.hashCode(), actual.hashCode());
	}
	
	@Test
	public void sendToPolicyTest() throws Exception {

	    PowerMockito.mockStatic(BeanUtil.class);
		PowerMockito.mockStatic(SdnrRestClient.class);
		PowerMockito.mockStatic(ConfigurationClient.class);

		SdnrRestClient sdnr = PowerMockito.spy(new SdnrRestClient());
		Configuration config = Configuration.getInstance();


	    String asyncRspBodyString = readFromFile("/AsyncRespBody.json");
	    ObjectMapper mapper = new ObjectMapper();
	    AsyncResponseBody async = new AsyncResponseBody();
        try {
            async = mapper.readValue(asyncRspBodyString, AsyncResponseBody.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

		PowerMockito.whenNew(SdnrRestClient.class).withAnyArguments().thenReturn(sdnr);
		PowerMockito.when(ConfigurationClient.configClient(config.getConfigClientType()))
				.thenReturn(sdnr);
		PowerMockito.doReturn(3).when(sdnr, "getPci", Mockito.anyString());
		PowerMockito.doReturn("pnfName").when(sdnr, "getPnfName", Mockito.anyString());

        when(policyDmaapClient.sendNotificationToPolicy(Mockito.anyString())).thenReturn(true);
        Map<String,List<CellPciPair>> pnfsMap = new HashMap<String,List<CellPciPair>>();
        CellPciPair cell1 = new CellPciPair("cell0", 1);
        CellPciPair cell2 = new CellPciPair("cell1", 2);
        CellPciPair cell3 = new CellPciPair("cell2", 3);
        List<CellPciPair> pciPairList = new ArrayList<>();
        pciPairList.add(cell1);
        pciPairList.add(cell2);
        pciPairList.add(cell3);
        pnfsMap.put("pnf1", pciPairList);
        when(policyDmaapClient.handlePolicyResponse(Mockito.anyString())).thenReturn(true);
        when(pnfUtils.getPnfs(async.getSolutions())).thenReturn(pnfsMap);
        List<String> remNeighbors = new ArrayList<>();
        remNeighbors.add("EXP006");
        Map<String,List<String>> cellRemNeighborsPair = new HashMap<>();
        cellRemNeighborsPair.put("EXP003", remNeighbors);
        List<Map<String,List<String>>> list = new ArrayList<>();
        list.add(cellRemNeighborsPair);
        Map<String, List<Map<String,List<String>>>> expected = new HashMap<>();
        expected.put("pnfName", list);
        when(pnfUtils.getPnfsForAnrSolutions(async.getSolutions().getAnrSolutions())).thenReturn(expected);
        HoDetails hoDetails = new HoDetails();
        hoDetails.setDstCellId("EXP006");
        List<HoDetails> hoDetailsList = new ArrayList<>();
        hoDetailsList.add(hoDetails);
        Either<List<HoDetails>, Integer> hoMetrics = Either.left(hoDetailsList);
        when(hoMetricsComponent.getHoMetrics(Mockito.anyString())).thenReturn(hoMetrics);
        when(hoMetricsComponent.update(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        assertTrue(childThreadUtils2.sendToPolicy(async));
        
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
