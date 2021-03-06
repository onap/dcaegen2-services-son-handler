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

package org.onap.dcaegen2.services.sonhms;

import com.fasterxml.jackson.databind.ObjectMapper;

import fj.data.Either;

import java.io.IOException;

import org.json.JSONObject;
import org.onap.dcaegen2.services.sonhms.dao.DmaapNotificationsRepository;
import org.onap.dcaegen2.services.sonhms.dao.PerformanceNotificationsRepository;
import org.onap.dcaegen2.services.sonhms.model.Notification;
import org.onap.dcaegen2.services.sonhms.model.PmNotification;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmaapNotificationsComponent {

	private static Logger log = LoggerFactory.getLogger(DmaapNotificationsComponent.class);

	/**
	 * Get sdnr notifications.
	 */
	public Either<Notification, Integer> getSdnrNotifications() {
		DmaapNotificationsRepository dmaapNotificationsRepository = BeanUtil
				.getBean(DmaapNotificationsRepository.class);
		String notificationString = dmaapNotificationsRepository.getNotificationFromQueue();
		if (notificationString == null) {
			return Either.right(404);
		}
		ObjectMapper mapper = new ObjectMapper();

		Notification notification = new Notification();
		try { 
			notification = mapper.readValue(notificationString, Notification.class);
			return Either.left(notification);
		} catch (IOException e) {
			log.error("Exception in parsing notification", notificationString, e);
			return Either.right(400);
		}
	}

	/**
	 * Get pm notifications.
	 */
	public Either<PmNotification, Integer> getPmNotifications() {
		PerformanceNotificationsRepository pmNotificationRepository = BeanUtil
				.getBean(PerformanceNotificationsRepository.class);
		String pmNotificationString = pmNotificationRepository.getPerformanceNotificationFromQueue();
		if (pmNotificationString == null) {
			return Either.right(404);
		}
		try {
			JSONObject obj = new JSONObject(pmNotificationString);
                        Configuration configuration = Configuration.getInstance();
			String configNfNamingCode = configuration.getNfNamingCode();
			String nfNamingCode = obj.getJSONObject("event").getJSONObject("commonEventHeader")
					.getString("nfNamingCode");
			if (!nfNamingCode.equalsIgnoreCase(configNfNamingCode)) {
				return Either.right(404);
			}
			ObjectMapper mapper = new ObjectMapper();
			PmNotification pmNotification = new PmNotification();
			pmNotification = mapper.readValue(pmNotificationString, PmNotification.class);
			return Either.left(pmNotification);
		} catch (Exception e) {
			log.error("Exception in parsing pm notification ", pmNotificationString, e);
			return Either.right(400);
		}
	}

}

