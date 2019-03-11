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
import com.att.nsa.cambria.client.CambriaClientBuilders.PublisherBuilder;
import org.onap.dcaegen2.services.sonhms.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class PolicyDmaapClient {


    /**
     * Method stub for sending notification to policy.
     */
    public boolean sendNotificationToPolicy(String msg) {

        Configuration configuration = Configuration.getInstance();
        CambriaBatchingPublisher cambriaBatchingPublisher;
        try {
            cambriaBatchingPublisher = new PublisherBuilder().usingHosts(configuration.getServers())
                    .onTopic(configuration.getPolicyTopic())
                    .authenticatedBy(configuration.getPcimsApiKey(), configuration.getPcimsSecretKey()).build();
            NotificationProducer notificationProducer = new NotificationProducer(cambriaBatchingPublisher);
            notificationProducer.sendNotification(msg);
        } catch (GeneralSecurityException | IOException e) {
            return false;
        }
        return true;
    }
}
