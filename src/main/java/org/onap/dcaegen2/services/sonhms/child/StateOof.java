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

package org.onap.dcaegen2.services.sonhms.child;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.exceptions.OofNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.AnrInput;
import org.onap.dcaegen2.services.sonhms.restclient.OofRestClient;
import org.slf4j.Logger;

public class StateOof {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(StateOof.class);
    private BlockingQueue<List<String>> childStatusUpdate;
    public StateOof() {

    }

    /**
     * Parameterized Constructor.
     *
     */
    public StateOof(BlockingQueue<List<String>> childStatusUpdate) {
        super();
        this.childStatusUpdate = childStatusUpdate;
    }

    /**
     * Triggers OOF for pci.
     * @throws OofNotFoundException  when trigger oof fails
     */
    public UUID triggerOof(List<String> cellidList, 
            String networkId, List<AnrInput> anrInputList) throws OofNotFoundException, InterruptedException {
        
        log.info("Triggering oof");
        
        log.debug("the cells triggering the oof are {}", cellidList);

        UUID transactionId = UUID.randomUUID();

        Configuration config = Configuration.getInstance();
        int numSolutions = config.getNumSolutions();
        List<String> optimizers = config.getOptimizers();

        String oofResponse = OofRestClient.queryOof(numSolutions, transactionId.toString(), "create", cellidList,
                networkId, optimizers, anrInputList);
        log.info("Synchronous Response {}", oofResponse);

        List<String> childStatus = new ArrayList<>();
        childStatus.add(Long.toString(Thread.currentThread().getId()));
        childStatus.add("triggeredOof");
        try {
            childStatusUpdate.put(childStatus);
        } catch (InterruptedException e1) {
            log.debug("Interrupted execption {}", e1);
            Thread.currentThread().interrupt();

        }
        
        return transactionId;
    }
    

}
