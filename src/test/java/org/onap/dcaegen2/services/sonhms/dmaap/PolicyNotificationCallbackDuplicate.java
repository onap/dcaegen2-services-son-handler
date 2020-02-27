/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2020 Wipro Limited.
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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.dao.FixedPciCellsRepository;
import org.onap.dcaegen2.services.sonhms.dao.PciUpdateRepository;
import org.onap.dcaegen2.services.sonhms.entity.FixedPciCells;
import org.onap.dcaegen2.services.sonhms.model.Configurations;
import org.onap.dcaegen2.services.sonhms.model.Payload;
import org.onap.dcaegen2.services.sonhms.model.PolicyNotification;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.slf4j.Logger;

public class PolicyNotificationCallbackDuplicate extends NotificationCallback {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(PolicyNotificationCallback.class);

	@Override
	public void activateCallBack(String msg) {
		handlePolicyNotification(msg);
	}

	private static FixedPciCells cell;

	public FixedPciCells returnResult() {
		return cell;
	}
	
	public void setDefaultResult() {
		cell =null;;
	}

	private void handlePolicyNotification(String msg) {
		PciUpdateRepository pciUpdateRepository = BeanUtil.getBean(PciUpdateRepository.class);
		Configuration configuration = Configuration.getInstance();
		try {
			ObjectMapper mapper = new ObjectMapper();
			PolicyNotification policyResponse = mapper.readValue(msg, PolicyNotification.class);
			String payload = policyResponse.getPayload();
			Payload payloadObject = mapper.readValue(payload, Payload.class);
			List<Configurations> configurationList = payloadObject.getConfiguration();
			for (Configurations config : configurationList) {
				int status = config.getStatus().getCode();
				if (status != 200) {
					String cellId = config.getData().getFapservice().getAlias();
					int negativeAckCount = pciUpdateRepository.getNegativeAckCountforCellId(cellId);
					if (negativeAckCount > configuration.getPolicyNegativeAckThreshold()) {
						long fixedPci = pciUpdateRepository.getOldPciforCellId(cellId);
						FixedPciCellsRepository fixedPciCellsRepository = BeanUtil
								.getBean(FixedPciCellsRepository.class);
						FixedPciCells fixedPciCells = new FixedPciCells();
						fixedPciCells.setCellId(cellId);
						fixedPciCells.setFixedPci(fixedPci);
						cell = fixedPciCellsRepository.save(fixedPciCells);
						pciUpdateRepository.deleterecordforCellId(cellId);
					} else {

						pciUpdateRepository.increaseNegativeAckCountforCellId(++negativeAckCount, cellId);
					}
				} else {

					String cellId = config.getData().getFapservice().getAlias();
					pciUpdateRepository.deleterecordforCellId(cellId);
				}

				String statusToString = Integer.toString(status);
				log.info("Handled response from policy, status code {}", statusToString);
			}
		} catch (IOException e) {
			log.info("caught io exception while fetching policy response");

		}
	}
}
