/*******************************************************************************
 * ============LICENSE_START=======================================================
 * pcims
 *  ================================================================================
 *  Copyright (C) 2018 Wipro Limited.
 *  ==============================================================================
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   ============LICENSE_END=========================================================
 ******************************************************************************/

package com.wipro.www.sonhms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipro.www.sonhms.model.FapServiceList;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChildStatusUpdateState implements SonState {
    private static Logger log = LoggerFactory.getLogger(ChildStatusUpdateState.class);

    @Override
    public void stateChange(SonContext sonContext) {
        if (sonContext.getChildStatus(sonContext.getChildThreadId()).equals("triggeredOof")) {
            WaitState waitState = WaitState.getInstance();
            sonContext.setPciState(waitState);
            sonContext.stateChange(sonContext);
        } else if (sonContext.getChildStatus(sonContext.getChildThreadId()).equals("success")) {
            BufferNotificationComponent bufferNotificationComponent = new BufferNotificationComponent();
            ClusterDetailsComponent clusterDetailsComponent = new ClusterDetailsComponent();
            String clusterId = clusterDetailsComponent.getClusterId(sonContext.getChildThreadId());
            List<String> bufferedNotifications = bufferNotificationComponent.getBufferedNotification(clusterId);
            if ((bufferedNotifications == null) || bufferedNotifications.isEmpty()) {
                // resource clean up
                // kill the child thread cleanup resources for the cluster
                log.debug("no buffered notification to be handled");

                Set<Thread> setOfThread = Thread.getAllStackTraces().keySet();
                for (Thread thread : setOfThread) {
                    if (thread.getId() == sonContext.getChildThreadId()) {
                        sonContext.deleteChildStatus();
                        thread.interrupt();
                    }
                }

                sonContext.setPciState(new ResourceCleanupState());
                sonContext.stateChange(sonContext);

            } else {
                // forward to child thread
                ObjectMapper mapper = new ObjectMapper();
                log.debug("handling buffer notification");
                long childThreadId = sonContext.getChildThreadId();
                for (String notification : bufferedNotifications) {
                    FapServiceList fapServiceList;
                    try {
                        fapServiceList = mapper.readValue(notification, FapServiceList.class);
                        log.debug("fapServiceList{}", fapServiceList);

                        SdnrNotificationHandlingState.getChildThreadMap().get(childThreadId)
                                .putInQueueWithNotify(fapServiceList);
                        sonContext.setPciState(new BufferedNotificationHandlingState());
                        sonContext.stateChange(sonContext);

                    } catch (IOException e) {
                        log.error("caught in child status update {}", e);
                    }

                }
            }
        }
    }

}
