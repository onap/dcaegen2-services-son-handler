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

import org.onap.dcaegen2.services.sonhms.NewFmNotification;
import org.onap.dcaegen2.services.sonhms.dao.FaultNotificationsRepository;
import org.onap.dcaegen2.services.sonhms.entity.FaultNotifications;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FmNotificationCallback extends NotificationCallback {

    private static Logger log = LoggerFactory.getLogger(FmNotificationCallback.class);

    @Override
    public void activateCallBack(String msg) {
        handleNotification(msg);
    }

    private void handleNotification(String msg) {

        FaultNotificationsRepository faultNotificationsRepository = BeanUtil
                .getBean(FaultNotificationsRepository.class);

        
        FaultNotifications faultNotification = new FaultNotifications();
        faultNotification.setNotification(msg);
        if (log.isDebugEnabled()) {
            log.debug(faultNotification.toString());
        }
        NewFmNotification newNotification = BeanUtil.getBean(NewFmNotification.class);
        faultNotificationsRepository.save(faultNotification);
        newNotification.setNewNotif(true);
    }

}
