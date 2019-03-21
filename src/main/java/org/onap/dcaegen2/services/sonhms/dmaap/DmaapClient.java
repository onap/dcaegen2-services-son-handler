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

import com.att.nsa.cambria.client.CambriaConsumer;

import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.NewNotification;
import org.onap.dcaegen2.services.sonhms.dao.DmaapNotificationsRepository;
import org.onap.dcaegen2.services.sonhms.entity.DmaapNotifications;
import org.onap.dcaegen2.services.sonhms.utils.DmaapUtils;


import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DmaapClient {

	@Autowired
	private DmaapNotificationsRepository dmaapNotificationsRepository;
	private Configuration configuration;
	private static Logger log = LoggerFactory.getLogger(DmaapClient.class);
	
	@Autowired
	private NewNotification newNotification;
	private DmaapUtils dmaapUtils;

	public class NotificationCallback {
		DmaapClient dmaapClient;

		public NotificationCallback(DmaapClient dmaapClient) {
			this.dmaapClient = dmaapClient;
		}

		public void activateCallBack(String msg) {
			handleNotification(msg);
		}

		private void handleNotification(String msg) {
			DmaapNotifications dmaapNotification = new DmaapNotifications();
			dmaapNotification.setNotification(msg);
			if (log.isDebugEnabled()) {
				log.debug(dmaapNotification.toString());
			}
			dmaapNotificationsRepository.save(dmaapNotification);
			newNotification.setNewNotif(true);
		}
	}

	/**
	 * init dmaap client.
	 */
	public void initClient() {
		log.debug("initializing client");
		configuration = Configuration.getInstance();
		if (log.isDebugEnabled()) {
			log.debug(configuration.toString());
		}

		startClient();
	}


	/**
	 * start dmaap client.
	 */
	@SuppressWarnings("unchecked")
	private synchronized void startClient() {
		
		Map<String,Object> streamSubscribes= Configuration.getInstance().getStreamsSubscribes();
		String sdnrTopicUrl =((Map<String,String>)((Map<String,Object>)streamSubscribes.get("nbr_list_change_topic")).get("dmaap_info")).get("topic_url");
		String[] sdnrTopicSplit=sdnrTopicUrl.split("\\/");
		String sdnrTopic=sdnrTopicSplit[sdnrTopicSplit.length-1];
		ScheduledExecutorService executorPool;
		CambriaConsumer cambriaConsumer = null;

		cambriaConsumer = dmaapUtils.buildConsumer(configuration, sdnrTopic );
		/*
		 * cambriaConsumer = new ConsumerBuilder()
		 * .authenticatedBy(configuration.getPcimsApiKey(),
		 * configuration.getPcimsSecretKey()) .knownAs(configuration.getCg(),
		 * configuration.getCid()).onTopic(configuration.getSdnrTopic())
		 * .usingHosts(configuration.getServers()).withSocketTimeout(configuration.
		 * getPollingTimeout() * 1000) .build();
		 */

		// create notification consumers for SNDR and policy
		NotificationConsumer notificationConsumer = new NotificationConsumer(cambriaConsumer,
				new NotificationCallback(this));

		// start notification consumer threads
		executorPool = Executors.newScheduledThreadPool(10);
		executorPool.scheduleAtFixedRate(notificationConsumer, 0, configuration.getPollingInterval(), TimeUnit.SECONDS);

	}
	
}
