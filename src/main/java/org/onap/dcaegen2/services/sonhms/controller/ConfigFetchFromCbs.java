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

package org.onap.dcaegen2.services.sonhms.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.onap.dcaegen2.services.sdk.rest.services.cbs.client.api.CbsClientFactory;
import org.onap.dcaegen2.services.sdk.rest.services.cbs.client.api.CbsRequests;
import org.onap.dcaegen2.services.sdk.rest.services.cbs.client.model.CbsRequest;
import org.onap.dcaegen2.services.sdk.rest.services.cbs.client.model.EnvProperties;
import org.onap.dcaegen2.services.sdk.rest.services.model.logging.RequestDiagnosticContext;
import org.onap.dcaegen2.services.sonhms.ConfigPolicy;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigFetchFromCbs {

    private static Logger log = LoggerFactory.getLogger(ConfigFetchFromCbs.class);

    /**
     * Gets app config from CBS.
     */
    public void getAppConfig() {

        // Generate RequestID and InvocationID which will be used when logging and in
        // HTTP requests
        log.info("getAppconfig start ..");
        RequestDiagnosticContext diagnosticContext = RequestDiagnosticContext.create();
        // Read necessary properties from the environment
        final EnvProperties env = EnvProperties.fromEnvironment();
        log.debug("environments {}",env);
        ConfigPolicy configPolicy = ConfigPolicy.getInstance();
        
        // Create the client and use it to get the configuration
        final CbsRequest request = CbsRequests.getAll(diagnosticContext);
        CbsClientFactory.createCbsClient(env).flatMap(cbsClient -> cbsClient.get(request))
                .subscribe(jsonObject -> {
                    log.debug("configuration from CBS {}", jsonObject);
                    JsonObject config = jsonObject.getAsJsonObject("config");
                    
                    updateConfigurationFromJsonObject(config);
                    
                    Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
                    JsonObject policyJson = jsonObject.getAsJsonObject("policy");
                    Map<String,Object> policy = new Gson().fromJson(policyJson, mapType);
                    configPolicy.setConfig(policy);
                }, throwable -> log.warn("Ooops", throwable)) ;

    }

    private void updateConfigurationFromJsonObject(JsonObject jsonObject) {
        
        log.info("Updating configuration from CBS");
        Configuration configuration = Configuration.getInstance();
        log.info("configuration from CBS {}", jsonObject);
        
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        
        JsonObject subscribes = jsonObject.getAsJsonObject("streams_subscribes");
        Map<String, Object> streamsSubscribes = new Gson().fromJson(subscribes, mapType);
        
        JsonObject publishes = jsonObject.getAsJsonObject("streams_publishes");
        Map<String, Object> streamsPublishes = new Gson().fromJson(publishes, mapType);
        
        int pgPort = jsonObject.get("postgres.port").getAsInt();
        int pollingInterval = jsonObject.get("sonhandler.pollingInterval").getAsInt();
        String pgPassword = jsonObject.get("postgres.password").getAsString();
        int numSolutions = jsonObject.get("sonhandler.numSolutions").getAsInt();
        int minConfusion = jsonObject.get("sonhandler.minConfusion").getAsInt();
        int maximumClusters = jsonObject.get("sonhandler.maximumClusters").getAsInt();
        int minCollision = jsonObject.get("sonhandler.minCollision").getAsInt();
        String sourceId = jsonObject.get("sonhandler.sourceId").getAsString();
        String pgUsername = jsonObject.get("postgres.username").getAsString();
        String pgHost = jsonObject.get("postgres.host").getAsString();

        JsonArray servers = jsonObject.getAsJsonArray("sonhandler.dmaap.server");
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> dmaapServers = new Gson().fromJson(servers, listType);
        
        String cg = jsonObject.get("sonhandler.cg").getAsString();
        int bufferTime = jsonObject.get("sonhandler.bufferTime").getAsInt();
        String cid = jsonObject.get("sonhandler.cid").getAsString();
        String configDbService = jsonObject.get("sonhandler.configDb.service").getAsString();
        String namespace=jsonObject.get("sonhandler.namespace").getAsString();
        String callbackUrl = "http://" + System.getenv("HOSTNAME") +"."+namespace+":8080/callbackUrl";
        
        JsonArray optimizersJson = jsonObject.getAsJsonArray("sonhandler.optimizers");
        List<String> optimizers = new Gson().fromJson(optimizersJson, listType);                   
        
        String oofService = jsonObject.get("sonhandler.oof.service").getAsString();
        int pollingTimeout = jsonObject.get("sonhandler.pollingTimeout").getAsInt();

        int badThreshold = jsonObject.get("sonhandler.badThreshold").getAsInt();
        int poorThreshold = jsonObject.get("sonhandler.poorThreshold").getAsInt();

        configuration.setStreamsSubscribes(streamsSubscribes);
        configuration.setStreamsPublishes(streamsPublishes);
        configuration.setPgPassword(pgPassword);
        configuration.setPgPort(pgPort);
        configuration.setPollingInterval(pollingInterval);
        configuration.setNumSolutions(numSolutions);
        configuration.setMinCollision(minCollision);
        configuration.setMinConfusion(minConfusion);
        configuration.setMaximumClusters(maximumClusters);
        configuration.setPgHost(pgHost);
        configuration.setPgUsername(pgUsername);
        configuration.setSourceId(sourceId);
        configuration.setDmaapServers(dmaapServers);
        configuration.setCg(cg);
        configuration.setCid(cid);
        configuration.setBufferTime(bufferTime);
        configuration.setConfigDbService(configDbService);
        configuration.setCallbackUrl(callbackUrl);
        configuration.setOptimizers(optimizers);
        configuration.setOofService(oofService);
        configuration.setPollingTimeout(pollingTimeout);
        configuration.setBadThreshold(badThreshold);
        configuration.setPoorThreshold(poorThreshold);
        log.info("configuration from CBS {}", configuration.toString());

    }

}
