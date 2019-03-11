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

import org.onap.dcaegen2.services.sonhms.ConfigPolicy;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.dao.SonRequestsRepository;
import org.onap.dcaegen2.services.sonhms.dmaap.PolicyDmaapClient;
import org.onap.dcaegen2.services.sonhms.entity.PciRequests;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.exceptions.OofNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.restclient.AsyncResponseBody;
import org.onap.dcaegen2.services.sonhms.restclient.OofRestClient;
import org.onap.dcaegen2.services.sonhms.restclient.Solution;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

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
     * Triggers OOF.
     * @throws OofNotFoundException  when trigger oof fails
     */
    public void triggerOof(Map<String, ArrayList<Integer>> result, 
            String networkId) throws OofNotFoundException, InterruptedException {
        // check for 0 collision and 0 confusion
        ArrayList<String> cellidList = new ArrayList<>();
        ArrayList<String> cellIds = new ArrayList<>();

        for (Map.Entry<String, ArrayList<Integer>> entry : result.entrySet()) {
            String key = entry.getKey();
            ArrayList<Integer> arr;
            arr = entry.getValue();
            if (!arr.isEmpty()) {
                Set<Integer> set = new HashSet<>(arr);
                if (((set.size() == 1) && !set.contains(0)) || (set.size() != 1)) {
                    cellIds.add(key);

                }
            }

        }

        for (String cell : cellIds) {
            log.debug("cellidList entries: {}", cell);
            cellidList.add(cell);
        }
        log.debug("the cells triggering the oof are {}", cellidList);

        UUID transactionId = UUID.randomUUID();

        Configuration config = Configuration.getInstance();
        int numSolutions = config.getNumSolutions();
        List<String> optimizers = config.getOptimizers();

        String oofResponse = OofRestClient.queryOof(numSolutions, transactionId.toString(), "create", cellidList,
                networkId, optimizers);
        log.debug("Synchronous Response {}", oofResponse);

        List<String> childStatus = new ArrayList<>();
        childStatus.add(Long.toString(Thread.currentThread().getId()));
        childStatus.add("triggeredOof");
        try {
            childStatusUpdate.put(childStatus);
        } catch (InterruptedException e1) {
            log.debug("Interrupted execption {}", e1);
            Thread.currentThread().interrupt();

        }

        // Store Request details in Database

        PciRequests pciRequest = new PciRequests();

        long childThreadId = Thread.currentThread().getId();
        pciRequest.setTransactionId(transactionId.toString());
        pciRequest.setChildThreadId(childThreadId);
        SonRequestsRepository pciRequestsRepository = BeanUtil.getBean(SonRequestsRepository.class);
        pciRequestsRepository.save(pciRequest);

        while (!ChildThread.getResponseMap().containsKey(childThreadId)) {
            Thread.sleep(100);
        }

        AsyncResponseBody asynResponseBody = ChildThread.getResponseMap().get(childThreadId);

        try {
            sendToPolicy(asynResponseBody);
        } catch (ConfigDbNotFoundException e1) {
            log.debug("Config DB is unreachable: {}", e1);
        }

        pciRequestsRepository = BeanUtil.getBean(SonRequestsRepository.class);
        pciRequestsRepository.deleteByChildThreadId(childThreadId);

        childStatus = new ArrayList<>();
        childStatus.add(Long.toString(Thread.currentThread().getId()));
        childStatus.add("success");
        try {
            childStatusUpdate.put(childStatus);
        } catch (InterruptedException e) {
            log.debug("InterruptedException {}", e);
            Thread.currentThread().interrupt();

        }

    }

    /**
     * Sends Dmaap notification to Policy.
     *
     * @throws ConfigDbNotFoundException
     *             when config db is unreachable
     */
    private void sendToPolicy(AsyncResponseBody async) throws ConfigDbNotFoundException {

        if (log.isDebugEnabled()) {
            log.debug(async.toString());
        }

        List<Solution> solutions;
        solutions = async.getSolutions();
        
        PnfUtils pnfUtils = new PnfUtils();
        Map<String, List<CellPciPair>> pnfs = pnfUtils.getPnfs(solutions);

        for (Map.Entry<String, List<CellPciPair>> entry : pnfs.entrySet()) {
            String pnfName = entry.getKey();
            List<CellPciPair> cellPciPairs = entry.getValue();

            ChildThreadUtils childThreadUtils = new ChildThreadUtils(ConfigPolicy.getInstance());
            String notification = childThreadUtils.getNotificationString(pnfName, cellPciPairs, 
                    UUID.randomUUID().toString(), System.currentTimeMillis());
            log.debug("Policy Notification: {}", notification);
            PolicyDmaapClient policy = new PolicyDmaapClient();
            boolean status = policy.sendNotificationToPolicy(notification);
            log.debug("sent Message: {}", status);
            if (status) {
                log.debug("Message sent to policy");
            } else {
                log.debug("Sending notification to policy failed");
            }

        }
    }

    

}
