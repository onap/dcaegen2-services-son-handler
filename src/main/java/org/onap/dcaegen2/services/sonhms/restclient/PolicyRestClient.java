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

package org.onap.dcaegen2.services.sonhms.restclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.utils.SonHandlerRestTemplate;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class PolicyRestClient {
    private static Logger log = LoggerFactory.getLogger(PolicyRestClient.class);

    private PolicyRestClient() {

    }

    /**
     * Fetches configuration from policy.
     */
    public static String fetchConfigFromPolicy() {
        log.debug("inside fetconfig from policy");

        Configuration configuration = Configuration.getInstance();
        ResponseEntity<String> response = null;
        String configName = configuration.getConfigName();
        String policyName = configuration.getPolicyName();

        try {
            PolicyRequestBody policyRequestBody = new PolicyRequestBody();
            policyRequestBody.setConfigName(configName);
            policyRequestBody.setPolicyName(policyName);
            UUID requestUuid = UUID.randomUUID();
            String requestId = requestUuid.toString();
            policyRequestBody.setRequestId(requestId);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody;
            requestBody = mapper.writeValueAsString(policyRequestBody);

            log.debug("policyRequestBody{}", requestBody);
            String requestUrl = configuration.getPolicyService() + "/pdp/api/getConfig";
            response = SonHandlerRestTemplate.sendPostToPolicy(requestUrl, requestBody,new ParameterizedTypeReference<String>() {});
            log.debug("policy response{}", response);

            return response.getBody();
        } catch (JsonProcessingException e) {
            log.debug("exception", e);
        }
        return response.getBody();

    }

}
