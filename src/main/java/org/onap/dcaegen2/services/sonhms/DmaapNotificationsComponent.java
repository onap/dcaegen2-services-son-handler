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
import org.onap.dcaegen2.services.sonhms.dao.DmaapNotificationsRepository;
import org.onap.dcaegen2.services.sonhms.model.Notification;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import fj.data.Either;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmaapNotificationsComponent {
    
    private static Logger log = LoggerFactory.getLogger(DmaapNotificationsComponent.class);
    
    /**
     * Get dmaap notifications.
     */
    public Either<Notification, Integer> getDmaapNotifications() {
        DmaapNotificationsRepository dmaapNotificationsRepository = BeanUtil
                .getBean(DmaapNotificationsRepository.class);
        String notificationString = dmaapNotificationsRepository.getNotificationFromQueue();
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

}
