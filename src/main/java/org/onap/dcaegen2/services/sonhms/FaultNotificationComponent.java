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

package org.onap.dcaegen2.services.sonhms;

import com.fasterxml.jackson.databind.ObjectMapper;

import fj.data.Either;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.onap.dcaegen2.services.sonhms.dao.FaultNotificationsRepository;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FaultNotificationComponent {

	private static Logger log = LoggerFactory.getLogger(FaultNotificationComponent.class);

	/**
	 * Get fault notifications.
	 */
	public Either<List<FaultEvent>,Integer> getFaultNotifications() {
		FaultNotificationsRepository faultNotificationsRepository = BeanUtil
				.getBean(FaultNotificationsRepository.class);
		String notificationString = faultNotificationsRepository.getFaultNotificationFromQueue();
		log.info("get fault notifications method");
		if (notificationString == null) {
		    return Either.right(404);
		}
		ObjectMapper mapper = new ObjectMapper();
		FaultEvent faultEvent = new FaultEvent();
		List<FaultEvent> faultEvents = new ArrayList<>();
		try {
            faultEvent = mapper.readValue(notificationString,FaultEvent.class);
            log.info("Parsing FM notification");
            
        } catch (IOException e) {
            log.error("Exception in parsing Notification {}", e);
            return Either.right(400);
        }
		
		faultEvents.add(faultEvent);
		
		return Either.left(faultEvents);

	}

}
