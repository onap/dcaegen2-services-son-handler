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

package org.onap.dcaegen2.services.sonhms.utils;

import java.util.Collections;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SonHandlerRestTemplate {
    @Autowired
    static RestTemplate restTemplate;

    private static final String AUTH = "Authorization";
    private static final String EXCEPTION_MSG = "Exception caught during request {}";
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SonHandlerRestTemplate.class);

    private SonHandlerRestTemplate() {
        
    }
    
    /**
     * Send Post Request.
     */

    public static <T> ResponseEntity<T> sendPostRequest(String requestUrl, String requestBody,
            ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
        try {
            return restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, responseType);
        } catch (Exception e) {
            log.debug(EXCEPTION_MSG, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Send Post Request to policy.
     */
    public static <T> ResponseEntity<T> sendPostToPolicy(String requestUrl, String requestBody,
            ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("ClientAuth", "cHl0aG9uOnRlc3Q=");
        headers.add(AUTH, "Basic dGVzdHBkcDphbHBoYTEyMw== ");
        headers.add("Environment", "TEST");
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
        try {
            return restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, responseType);
        } catch (Exception e) {
            log.debug(EXCEPTION_MSG, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Send Get Request.
     */

    public static <T> ResponseEntity<T> sendGetRequest(String requestUrl, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            return restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity, responseType);
        } catch (Exception e) {
            log.debug(EXCEPTION_MSG, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Send Get Request to SDNR.
     */

    public static <T> ResponseEntity<T> sendGetRequest(String requestUrl, String requestBody,
            ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(AUTH, "Basic SW5mcmFQb3J0YWxDbGllbnQ6cGFzc3dvcmQxJA==");
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
        try {
            return restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, responseType);
        } catch (Exception e) {
            log.debug(EXCEPTION_MSG, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Send Post Request to oof.
     */

    public static <T> ResponseEntity<T> sendPostRequestToOof(String requestUrl, String requestBody,
            ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(AUTH, "Basic cGNpX3Rlc3Q6cGNpX3Rlc3Rwd2Q=");
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
        try {
            return restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, responseType);
        } catch (Exception e) {
            log.debug(EXCEPTION_MSG, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
