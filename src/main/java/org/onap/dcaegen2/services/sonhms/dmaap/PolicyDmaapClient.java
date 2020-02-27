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

import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.model.Payload;
import org.onap.dcaegen2.services.sonhms.model.PolicyNotification;
import org.onap.dcaegen2.services.sonhms.utils.DmaapUtils;
import org.slf4j.Logger;

public class PolicyDmaapClient {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PolicyDmaapClient.class);

    private DmaapUtils dmaapUtils;

    private Configuration configuration;

    public PolicyDmaapClient(DmaapUtils dmaapUtils, Configuration configuration) {
        this.dmaapUtils = dmaapUtils;
        this.configuration = configuration;
    }

    /**
     * Method stub for sending notification to policy.
     */
    @SuppressWarnings("unchecked")
    public boolean sendNotificationToPolicy(String msg) {
        Map<String, Object> streamsPublishes = configuration.getStreamsPublishes();
        String policyTopicUrl = ((Map<String, String>) ((Map<String, Object>) streamsPublishes.get("CL_topic"))
                .get("dmaap_info")).get("topic_url");
        String[] policyTopicSplit = policyTopicUrl.split("\\/");
        String policyTopic = policyTopicSplit[policyTopicSplit.length - 1];
        CambriaBatchingPublisher cambriaBatchingPublisher;
        try {

            cambriaBatchingPublisher = dmaapUtils.buildPublisher(configuration, policyTopic);

            NotificationProducer notificationProducer = new NotificationProducer(cambriaBatchingPublisher);
            notificationProducer.sendNotification(msg);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Handles policy response.
     */
    @SuppressWarnings("unchecked")
    public Boolean handlePolicyResponse(String requestId) {

        Map<String, Object> streamSubscribes = configuration.getStreamsSubscribes();
        String policyResponseTopicUrl = ((Map<String, String>) ((Map<String, Object>) streamSubscribes
                .get("dcae_cl_response_topic")).get("dmaap_info")).get("topic_url");
        String[] policyResponseTopicUrlSplit = policyResponseTopicUrl.split("\\/");
        String policyResponseTopic = policyResponseTopicUrlSplit[policyResponseTopicUrlSplit.length - 1];
        log.debug("policyResponse Topic : {}", policyResponseTopic);
        CambriaConsumer policyResponseCambriaConsumer = null;
        policyResponseCambriaConsumer = dmaapUtils.buildConsumer(configuration, policyResponseTopic);
        Timestamp startTimer = new Timestamp(System.currentTimeMillis());

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Long difference = currentTime.getTime() - startTimer.getTime();
        int policyRespTimer = configuration.getPolicyRespTimer(); // Timer in seconds
        while (difference < (policyRespTimer * 1000)) {
            Iterable<String> policyResponseMessages;
            try {
                policyResponseMessages = policyResponseCambriaConsumer.fetch();
                for (String msg : policyResponseMessages) {
                    ObjectMapper mapper = new ObjectMapper();
                    PolicyNotification policyResponse = mapper.readValue(msg, PolicyNotification.class);
                    if (requestId.equals(policyResponse.getRequestId())) {
                        String payload = policyResponse.getPayload();
                        Payload payloadObject = mapper.readValue(payload, Payload.class);
                        int status = payloadObject.getConfiguration().get(0).getStatus().getCode();
                        String statusToString = Integer.toString(status);
                        log.info("response from policy, status code {}", statusToString);
                        return true;

                    }
                }
            } catch (IOException e) {
                log.info("caught io exception while fetching policy response");
            }
            currentTime = new Timestamp(System.currentTimeMillis());
            difference = currentTime.getTime() - startTimer.getTime();
        }
        log.info("no response from policy, timer expired");
        return true;

    }
}
