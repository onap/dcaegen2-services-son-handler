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

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.onap.dcaegen2.services.sonhms.model.Notification;
import org.onap.dcaegen2.services.sonhms.utils.ClusterUtils;
import org.onap.dcaegen2.services.sonhms.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainThread implements Runnable {
    private static Logger log = LoggerFactory.getLogger(MainThread.class);

    private NewNotification newNotification;

    private BlockingQueue<List<String>> childStatusQueue;
    
    private DmaapNotificationsComponent dmaapNotificationsComponent;
 
    private EventHandler eventHandler;
    
    /**
     * parameterized constructor.
     */
    public MainThread(NewNotification newNotification) {
        super(); 
        this.newNotification = newNotification;
        childStatusQueue = new LinkedBlockingQueue<>();
        dmaapNotificationsComponent = new DmaapNotificationsComponent();
        eventHandler = new EventHandler(childStatusQueue,                 
                Executors.newFixedThreadPool(Configuration.getInstance().getMaximumClusters()), 
                new HashMap<>(), new ClusterUtils(), new ThreadUtils());
    } 
    
    @Override
    public void run() {
        log.info("Starting Main Thread");

        // Check for Notifications from Dmaap and Child thread
        Boolean done = false; 
        
        while (!done) {
            try {
                if (!childStatusQueue.isEmpty()) {
                    List<String> childState = childStatusQueue.poll();
                    if (childState != null) {
                        eventHandler.handleChildStatusUpdate(childState);
                    }
                }

                if (newNotification.getNewNotif()) {
                    Either<Notification, Integer> notification = dmaapNotificationsComponent.getDmaapNotifications();
                    if (notification.isRight()) {
                        log.error("Error parsing the notification from SDNR");
                    } else if (notification.isLeft()) {
                        Boolean result = eventHandler.handleSdnrNotification(notification.left().value());
                        log.debug("SDNR notification handling {}", result);
                    }

                }

            } catch (Exception e) {
                log.error("Exception in main Thread", e);
                done = true;
            }

        }

    }
}
