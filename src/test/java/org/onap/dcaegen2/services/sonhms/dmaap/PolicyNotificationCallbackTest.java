
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
package org.onap.dcaegen2.services.sonhms.dmaap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.dao.FixedPciCellsRepository;
import org.onap.dcaegen2.services.sonhms.dao.PciUpdateRepository;
import org.onap.dcaegen2.services.sonhms.entity.FixedPciCells;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = PolicyNotificationCallbackDuplicate.class)
@PrepareForTest({ BeanUtil.class, PolicyNotificationCallbackDuplicate.class, Configuration.class })
public class PolicyNotificationCallbackTest {

	@Mock
	PciUpdateRepository pciUpdateRepositoryMock;
	@Mock
	FixedPciCellsRepository fixedPciCellsRepositoryMock;

	@Test
	public void policyNotificationCallbackTest() {
		PolicyNotificationCallbackDuplicate policyCallBackMock = PowerMockito
				.spy(new PolicyNotificationCallbackDuplicate());
		PowerMockito.mockStatic(BeanUtil.class);

		Configuration configuration = Configuration.getInstance();
		configuration = Configuration.getInstance();
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
		configuration.setPolicyNegativeAckThreshold(3);

		PowerMockito.mockStatic(Configuration.class);

		Mockito.when(Configuration.getInstance()).thenReturn(configuration);

		PowerMockito.when(BeanUtil.getBean(PciUpdateRepository.class)).thenReturn(pciUpdateRepositoryMock);
		Mockito.when(pciUpdateRepositoryMock.getNegativeAckCountforCellId(Mockito.any())).thenReturn(5);
		Mockito.when(pciUpdateRepositoryMock.getOldPciforCellId(Mockito.any())).thenReturn((long) 9);
		PowerMockito.when(BeanUtil.getBean(FixedPciCellsRepository.class)).thenReturn(fixedPciCellsRepositoryMock);

		String positivePolicyNotification = "{\"closedLoopControlName\":null,\"closedLoopAlarmStart\":0,\"closedLoopEventClient\":null,\"closedLoopEventStatus\":null,\"target\":null,\"from\":null,\"version\":null,\"policyName\":null,\"policyVersion\":null,\"payload\":\"{\\\"Configurations\\\":[{\\\"data\\\":{\\\"FAPService\\\":{\\\"alias\\\":\\\"Chn0001\\\",\\\"X0005b9Lte\\\":{\\\"phyCellIdInUse\\\":999,\\\"pnfName\\\":\\\"ncsServer-1\\\"},\\\"CellConfig\\\":{\\\"LTE\\\":{\\\"RAN\\\":{\\\"Common\\\":{\\\"CellIdentity\\\":\\\"Chn0001\\\"},\\\"NeighborListInUse\\\":null}}}}},\\\"Status\\\":{\\\"Code\\\":200,\\\"Value\\\":\\\"fail\\\"}}]}\",\"target_type\":null,\"requestID\":null,\"AAI\":null,\"Action\":null}";
		policyCallBackMock.activateCallBack(positivePolicyNotification);
		assertNull(policyCallBackMock.returnResult());

	}

	public void negativePolicyNotificationCallbackTest() {

		PolicyNotificationCallbackDuplicate policyCallBackMock = PowerMockito
				.spy(new PolicyNotificationCallbackDuplicate());

		PowerMockito.mockStatic(BeanUtil.class);

		Configuration configuration = Configuration.getInstance();

		configuration = Configuration.getInstance();
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
		configuration.setPolicyNegativeAckThreshold(3);

		PowerMockito.mockStatic(Configuration.class);

		Mockito.when(Configuration.getInstance()).thenReturn(configuration);

		PowerMockito.when(BeanUtil.getBean(PciUpdateRepository.class)).thenReturn(pciUpdateRepositoryMock);
		Mockito.when(pciUpdateRepositoryMock.getOldPciforCellId(Mockito.any())).thenReturn((long) 9);
		PowerMockito.when(BeanUtil.getBean(FixedPciCellsRepository.class)).thenReturn(fixedPciCellsRepositoryMock);

		Mockito.when(pciUpdateRepositoryMock.getNegativeAckCountforCellId(Mockito.any())).thenReturn(2);
		String negativePolicyNotificationNegCount = "{\"closedLoopControlName\":null,\"closedLoopAlarmStart\":0,\"closedLoopEventClient\":null,\"closedLoopEventStatus\":null,\"target\":null,\"from\":null,\"version\":null,\"policyName\":null,\"policyVersion\":null,\"payload\":\"{\\\"Configurations\\\":[{\\\"data\\\":{\\\"FAPService\\\":{\\\"alias\\\":\\\"Chn0001\\\",\\\"X0005b9Lte\\\":{\\\"phyCellIdInUse\\\":999,\\\"pnfName\\\":\\\"ncsServer-1\\\"},\\\"CellConfig\\\":{\\\"LTE\\\":{\\\"RAN\\\":{\\\"Common\\\":{\\\"CellIdentity\\\":\\\"Chn0001\\\"},\\\"NeighborListInUse\\\":null}}}}},\\\"Status\\\":{\\\"Code\\\":400,\\\"Value\\\":\\\"fail\\\"}}]}\",\"target_type\":null,\"requestID\":null,\"AAI\":null,\"Action\":null}";
		policyCallBackMock.activateCallBack(negativePolicyNotificationNegCount);
		assertNull(policyCallBackMock.returnResult());

		Mockito.when(pciUpdateRepositoryMock.getNegativeAckCountforCellId(Mockito.any())).thenReturn(7);
		Mockito.when(fixedPciCellsRepositoryMock.save(Mockito.any())).thenReturn(new FixedPciCells());
		String negativePolicyNotification = "{\"closedLoopControlName\":null,\"closedLoopAlarmStart\":0,\"closedLoopEventClient\":null,\"closedLoopEventStatus\":null,\"target\":null,\"from\":null,\"version\":null,\"policyName\":null,\"policyVersion\":null,\"payload\":\"{\\\"Configurations\\\":[{\\\"data\\\":{\\\"FAPService\\\":{\\\"alias\\\":\\\"Chn0001\\\",\\\"X0005b9Lte\\\":{\\\"phyCellIdInUse\\\":999,\\\"pnfName\\\":\\\"ncsServer-1\\\"},\\\"CellConfig\\\":{\\\"LTE\\\":{\\\"RAN\\\":{\\\"Common\\\":{\\\"CellIdentity\\\":\\\"Chn0001\\\"},\\\"NeighborListInUse\\\":null}}}}},\\\"Status\\\":{\\\"Code\\\":400,\\\"Value\\\":\\\"fail\\\"}}]}\",\"target_type\":null,\"requestID\":null,\"AAI\":null,\"Action\":null}";
		policyCallBackMock.activateCallBack(negativePolicyNotification);
		assertNotNull(policyCallBackMock.returnResult());
	}

}
