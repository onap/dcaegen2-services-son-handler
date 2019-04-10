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

import fj.data.Either;

import org.onap.dcaegen2.services.sonhms.dmaap.PolicyDmaapClient;
import org.onap.dcaegen2.services.sonhms.model.PMNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PmThread implements Runnable {

    private static Logger log = LoggerFactory.getLogger(PmThread.class);

    private NewPmNotification newPmNotification;
    

    private DmaapNotificationsComponent dmaapNotificationsComponent;

    private PmNotificationHandler pmNotificationHandler;

    /**
     * parameterized constructor.
     */
    public PmThread(NewPmNotification newPmNotification) {
        super();
        this.newPmNotification = newPmNotification;
        dmaapNotificationsComponent = new DmaapNotificationsComponent();
        pmNotificationHandler = new PmNotificationHandler(new PolicyDmaapClient());
    }

    @Override
    public void run() {
        log.info("PM thread starting ...");
        // check for PM notifications
        Boolean done = false;
        while (!done) {
            try {
                Thread.sleep(1000);
                if (newPmNotification.getNewNotif()) {
                    log.info("New PM notification from Dmaap");
                    Either<PMNotification, Integer> pmNotification = dmaapNotificationsComponent.getPmNotifications();
                    if (pmNotification.isRight()) {
                        if (pmNotification.right().value() == 400) {
                            log.error("error parsing pm notifications");
                        } else if (pmNotification.right().value() == 404) {
                            log.info("Queue is empty");
                            newPmNotification.setNewNotif(false);
                        }
                    } else if (pmNotification.isLeft()) {
                        Configuration configuration = Configuration.getInstance();
                        Boolean result = pmNotificationHandler.handlePmNotifications(pmNotification.left().value(),
                                configuration.getBadThreshold());
                        log.info("pm notification handler result {}", result);
                    }

                }
            } catch (Exception e) {
                log.error("Exception in PM Thread ", e);
                done = true;
            }
        }

    }

}
