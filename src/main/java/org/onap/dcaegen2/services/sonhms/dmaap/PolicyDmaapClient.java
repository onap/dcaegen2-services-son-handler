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

import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.utils.DmaapUtils;

import java.io.IOException;
import java.util.Map;

public class PolicyDmaapClient {

	DmaapUtils dmaapUtils = new DmaapUtils();

	/**
	 * Method stub for sending notification to policy.
	 */
	@SuppressWarnings("unchecked")
	public boolean sendNotificationToPolicy(String msg) {

		Map<String,Object> streamSubscribes= Configuration.getInstance().getStreamsPublishes();
		String policyTopicUrl =((Map<String,String>)((Map<String,Object>)streamSubscribes.get("CL_topic")).get("dmaap_info")).get("topic_url");
		String[] policyTopicSplit=policyTopicUrl.split("\\/");
		String policyTopic=policyTopicSplit[policyTopicSplit.length-1];
		Configuration configuration = Configuration.getInstance();
		CambriaBatchingPublisher cambriaBatchingPublisher;
		try {

			cambriaBatchingPublisher = dmaapUtils.buildPublisher(configuration, policyTopic);

			NotificationProducer notificationProducer = new NotificationProducer(cambriaBatchingPublisher);
			notificationProducer.sendNotification(msg);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
