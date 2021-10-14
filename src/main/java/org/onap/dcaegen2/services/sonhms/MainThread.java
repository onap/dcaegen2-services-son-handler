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

import fj.data.Either;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.onap.dcaegen2.services.sonhms.model.FapServiceList;
import org.onap.dcaegen2.services.sonhms.model.Notification;
import org.onap.dcaegen2.services.sonhms.utils.ClusterUtils;
import org.onap.dcaegen2.services.sonhms.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainThread implements Runnable {

    private static Logger log = LoggerFactory.getLogger(MainThread.class);

    private NewSdnrNotification newNotification;

    private NewFmNotification newFmNotification;

    private BlockingQueue<List<String>> childStatusQueue;

    private DmaapNotificationsComponent dmaapNotificationsComponent;

    private FaultNotificationComponent faultNotificationComponent;

    private EventHandler eventHandler;

    private Map<String, FaultEvent> bufferedFmNotificationCells;

    private List<String> sdnrNotificationCells;

    private Boolean isTimer;

    private Timestamp startTimer;

    List<FaultEvent> fmNotificationToBuffer;

    /**
     * parameterized constructor.
     */
    public MainThread(NewSdnrNotification newNotification, NewFmNotification newFmNotification) {
        super();
        this.newFmNotification = newFmNotification;
        this.newNotification = newNotification;
        childStatusQueue = new LinkedBlockingQueue<>();
        dmaapNotificationsComponent = new DmaapNotificationsComponent();
        faultNotificationComponent = new FaultNotificationComponent();
        sdnrNotificationCells = new ArrayList<>();
        fmNotificationToBuffer = new ArrayList<>();
        bufferedFmNotificationCells = new HashMap<>();
        eventHandler = new EventHandler(childStatusQueue,
                Executors.newFixedThreadPool(Configuration.getInstance().getMaximumClusters()), new HashMap<>(),
                new ClusterUtils(), new ThreadUtils());
        isTimer = false;
        startTimer = new Timestamp(System.currentTimeMillis());

    }

    @Override
    public void run() {
        log.info("Starting Main Thread");

        // Check for Notifications from Dmaap and Child thread
        Boolean done = false;

        while (!done) {

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            if (isTimer) {
                Long difference = currentTime.getTime() - startTimer.getTime();
                if (difference > 5000) {
                    log.info("FM handling difference > 5000");

                    for (String sdnrCell : sdnrNotificationCells) {
                        bufferedFmNotificationCells.remove(sdnrCell);
                    }

                    log.info("FM bufferedFMNotificationCells {}", bufferedFmNotificationCells.values());
                    List<FaultEvent> fmNotificationsToHandle = new ArrayList<>(bufferedFmNotificationCells.values());
                    Boolean result = eventHandler.handleFaultNotification(fmNotificationsToHandle);
                    bufferedFmNotificationCells = new HashMap<>();
                    isTimer = false;
                    log.info("FM notification handling {}", result);
                }
            }

            try {
                if (!childStatusQueue.isEmpty()) {
                    List<String> childState = childStatusQueue.poll();
                    if (childState != null) {
                        eventHandler.handleChildStatusUpdate(childState);
                    }
                }

                if (newNotification.getNewNotif()) {
                    Either<Notification, Integer> notification = dmaapNotificationsComponent.getSdnrNotifications();
                    if (notification.isRight()) {
                        if (notification.right().value() == 400) {
                            log.error("Error parsing the notification from SDNR");
                        } else if (notification.right().value() == 404) {
                            newNotification.setNewNotif(false);
                        }
                    } else if (notification.isLeft()) {
                        List<FapServiceList> fapServiceLists = (notification.left().value()).getPayload()
                                .getRadioAccess().getFapServiceList();
                        for (FapServiceList fapServiceList : fapServiceLists) {
                            sdnrNotificationCells.add(fapServiceList.getAlias());

                        }

                        Boolean result = eventHandler.handleSdnrNotification(notification.left().value());
                        log.debug("SDNR notification handling {}", result);

                    }

                }
                if (newFmNotification.getNewNotif()) {
					log.info("newFmNotification has come");

					String faultCellId = "";
					Either<List<FaultEvent>, Integer> fmNotifications = faultNotificationComponent
							.getFaultNotifications();
					if (fmNotifications.isRight()) {
						if (fmNotifications.right().value() == 400) {
							log.info("Error parsing notifications");
						} else if (fmNotifications.right().value() == 404) {
							newFmNotification.setNewNotif(false);
						}
					} else {
						for (FaultEvent fmNotification : fmNotifications.left().value()) {
							if (fmNotification.getEvent().getFaultFields().getSpecificProblem()
									.equals("Optimised PCI")) {
								log.info("PCI problem cleared for :" + fmNotification);
							} else if ((fmNotification.getEvent().getFaultFields().getAlarmCondition()
						   				.equalsIgnoreCase("RanPciCollisionConfusionOccurred")))	
							{
								faultCellId = fmNotification.getEvent().getCommonEventHeader().getSourceName();
								bufferedFmNotificationCells.put(faultCellId, fmNotification);
								log.info("Buffered FM cell {}", faultCellId);
								log.info("fmNotification{}", fmNotification);
							} else {
								log.info("Error resolving faultNotification for :" + fmNotification);
							}
						}

						if (!(bufferedFmNotificationCells.isEmpty())) {
							log.info("bufferedFMNotificationCells before staring timer {}",
									bufferedFmNotificationCells.keySet());

							for (String sdnrCell : sdnrNotificationCells) {
								bufferedFmNotificationCells.remove(sdnrCell);
							}

							startTimer = new Timestamp(System.currentTimeMillis());
							isTimer = true;
							log.info("Buffered FM cell {}", bufferedFmNotificationCells.keySet());
						}
					}

				}

            } catch (Exception e) {
                log.error("Exception in main Thread", e);
                done = true;
            }

        }

    }
}
