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

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.utils.DmaapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DmaapClient {

    private Configuration configuration;
    private static Logger log = LoggerFactory.getLogger(DmaapClient.class);

    private DmaapUtils dmaapUtils;

    /**
     * init dmaap client.
     */
    @PostConstruct
    public void initClient() {
        log.debug("initializing client");
        dmaapUtils = new DmaapUtils();
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

        Map<String, Object> streamSubscribes = Configuration.getInstance().getStreamsSubscribes();
        String sdnrTopicUrl = ((Map<String, String>) ((Map<String, Object>) streamSubscribes
                .get("nbr_list_change_topic")).get("dmaap_info")).get("topic_url");
        String[] sdnrTopicSplit = sdnrTopicUrl.split("\\/");
        String sdnrTopic = sdnrTopicSplit[sdnrTopicSplit.length - 1];
        log.debug("sdnr topic : {}", sdnrTopic);
        String fmTopicUrl = ((Map<String, String>) ((Map<String, Object>) streamSubscribes
                .get("fault_management_topic")).get("dmaap_info")).get("topic_url");
        String[] fmTopicSplit = fmTopicUrl.split("\\/");
        String fmTopic = fmTopicSplit[sdnrTopicSplit.length - 1];
        log.debug("fm topic : {}", fmTopic);
        String pmTopicUrl = ((Map<String, String>) ((Map<String, Object>) streamSubscribes
                .get("performance_management_topic")).get("dmaap_info")).get("topic_url");
        String[] pmTopicSplit = pmTopicUrl.split("\\/");
        String pmTopic = pmTopicSplit[sdnrTopicSplit.length - 1];
        log.debug("pm topic : {}", pmTopic);
        CambriaConsumer sdnrNotifCambriaConsumer = null;
        CambriaConsumer fmNotifCambriaConsumer = null;
        CambriaConsumer pmNotifCambriaConsumer = null;

        sdnrNotifCambriaConsumer = dmaapUtils.buildConsumer(configuration, sdnrTopic);
        fmNotifCambriaConsumer = dmaapUtils.buildConsumer(configuration, fmTopic);
        pmNotifCambriaConsumer = dmaapUtils.buildConsumer(configuration, pmTopic);

        // create notification consumers for SNDR and policy
        NotificationConsumer sdnrNotificationConsumer = new NotificationConsumer(sdnrNotifCambriaConsumer,
                new SdnrNotificationCallback());
        // start sdnr notification consumer threads
        ScheduledExecutorService executorPool;
        executorPool = Executors.newScheduledThreadPool(10);
        executorPool.scheduleAtFixedRate(sdnrNotificationConsumer, 0, configuration.getPollingInterval(),
                TimeUnit.SECONDS);

        // create notification consumers for FM
        NotificationConsumer fmNotificationConsumer = new NotificationConsumer(fmNotifCambriaConsumer,
                new FMNotificationCallback());
        // start fm notification consumer threads
        executorPool = Executors.newScheduledThreadPool(10);
        executorPool.scheduleAtFixedRate(fmNotificationConsumer, 0, configuration.getPollingInterval(),
                TimeUnit.SECONDS);

        // create notification consumers for PM
        NotificationConsumer pmNotificationConsumer = new NotificationConsumer(pmNotifCambriaConsumer,
                new PMNotificationCallback());
        // start pm notification consumer threads
        executorPool = Executors.newScheduledThreadPool(10);
        executorPool.scheduleAtFixedRate(pmNotificationConsumer, 0, configuration.getPollingInterval(),
                TimeUnit.SECONDS);

    }

}
