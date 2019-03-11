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

import org.onap.dcaegen2.services.sonhms.utils.SonHandlerRestTemplate;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;


public class ConfigurationFetcher {
    private static Logger log = LoggerFactory.getLogger(ConfigurationFetcher.class);

    /**
     * method that fetches config from config binding service.
     */
    public void fetchFromCbs() {

        Configuration configuration = Configuration.getInstance();
        String consulHost = configuration.getConsulHost();
        String configBindingService = configuration.getConfigBindingService();
        ResponseEntity<String> response = null;
        String requestUrl = "";
        if ((consulHost  != null) && (configBindingService != null)) {
            requestUrl = consulHost + ":8500/v1/catalog/service/" + configBindingService;
            response = SonHandlerRestTemplate.sendPostRequest(requestUrl, "",
                    new ParameterizedTypeReference<String>() {});
        }
        JSONTokener jsonTokener = new JSONTokener(response.getBody());
        JSONObject cbsjobj = (JSONObject) new JSONArray(jsonTokener).get(0);
        String cbsUrl = "";
        String configurationRequestUrl = "";
        ResponseEntity<String> configurationResponse = null;
        if (cbsjobj.has("ServiceAddress") && cbsjobj.has("ServicePort")) {
            cbsUrl = cbsjobj.getString("ServiceAddress") + ":" + cbsjobj.getInt("ServicePort");

        }
        
        configurationRequestUrl = cbsUrl + "/service_component/" + configuration.getHostName();
        configurationResponse = SonHandlerRestTemplate.sendPostRequest1(configurationRequestUrl, "",
                new ParameterizedTypeReference<String>() {});
        JSONObject config = new JSONObject(new JSONTokener(configurationResponse.getBody()));
        String configString = config.toString();
        log.debug("config:{}",configString);
    }
   
    
}