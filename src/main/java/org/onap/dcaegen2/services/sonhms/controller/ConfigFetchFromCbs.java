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

import java.util.List;
import java.util.Map;

import org.onap.dcaegen2.services.sdk.rest.services.cbs.client.api.CbsClientFactory;
import org.onap.dcaegen2.services.sdk.rest.services.cbs.client.model.EnvProperties;
import org.onap.dcaegen2.services.sdk.rest.services.model.logging.RequestDiagnosticContext;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigFetchFromCbs {

    private static Logger log = LoggerFactory.getLogger(ConfigFetchFromCbs.class);

    /**
     * Gets app config from CBS.
     */
    @SuppressWarnings("unchecked")
	public void getAppConfig() {

        // Generate RequestID and InvocationID which will be used when logging and in
        // HTTP requests
    	log.debug("getAppconfig start ..");
        RequestDiagnosticContext diagnosticContext = RequestDiagnosticContext.create();
        log.debug("fiagnostic context : {}",diagnosticContext.toString());
        // Read necessary properties from the environment
        final EnvProperties env = EnvProperties.fromEnvironment();
        log.debug("environments {}",env.toString());
        Configuration configuration = Configuration.getInstance();
        
        // Create the client and use it to get the configuration
        CbsClientFactory.createCbsClient(env).flatMap(cbsClient -> cbsClient.get(diagnosticContext))
                .subscribe(jsonObject -> {
                    log.debug("configuration from CBS {}", jsonObject.toString());
                    final Map<String,Object> streamsSubscribes=(Map<String, Object>) jsonObject.get("streams_subscribes");
                    final Map<String,Object> streamsPublishes=(Map<String, Object>) jsonObject.get("streams_publishes");
                    final int pgPort = jsonObject.get("postgres.port").getAsInt();
                    final int pollingInterval=jsonObject.get("sonhandler.pollingInterval").getAsInt();
                    final String pgPassword = jsonObject.get("postgres.password").getAsString();
                    final int numSolutions=jsonObject.get("sonhandler.numSolutions").getAsInt();
                    final int minConfusion = jsonObject.get("sonhandler.minConfusion").getAsInt();
                    final int maximumClusters =jsonObject.get("sonhandler.maximumClusters").getAsInt();
                    final int minCollision = jsonObject.get("sonhandler.minCollision").getAsInt();
                    final String sourceId = jsonObject.get("sonhandler.sourceId").getAsString();
                    final String pgUsername = jsonObject.get("postgres.username").getAsString();
                    final String pgHost = jsonObject.get("postgres.host").getAsString();
                    final List<String> dmaapServers = (List<String>) jsonObject.get("sonhandler.dmaap.server");
                    final String cg=jsonObject.get("sonhandler.cg").getAsString();
                    final int bufferTime=jsonObject.get("sonhandler.bufferTime").getAsInt();
                    final String cid =jsonObject.get("sonhandler.cid").getAsString();
                    final String configDbService=jsonObject.get("sonhandler.configDb.service").getAsString();
                    final String callbackUrl=jsonObject.get("sonhandler.callbackUrl").getAsString();
                    final List<String> optimizers = (List<String>) jsonObject.get("sonhandler.optimizers");
                    final String oofService=jsonObject.get("sonhandler.oof.service").getAsString();
                    final int pollingTimeout=jsonObject.get("sonhandler.pollingTimeout").getAsInt();

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
                    
                    log.debug("configuration {}", configuration);
                }, throwable -> {  	
                log.warn("Ooops", throwable);
                });
        
    }

}
