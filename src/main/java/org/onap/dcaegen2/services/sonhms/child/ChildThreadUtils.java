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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.onap.dcaegen2.services.sonhms.ConfigPolicy;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.dao.SonRequestsRepository;
import org.onap.dcaegen2.services.sonhms.dmaap.PolicyDmaapClient;
import org.onap.dcaegen2.services.sonhms.entity.SonRequests;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellConfig;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.model.Common;
import org.onap.dcaegen2.services.sonhms.model.Configurations;
import org.onap.dcaegen2.services.sonhms.model.Data;
import org.onap.dcaegen2.services.sonhms.model.FapService;
import org.onap.dcaegen2.services.sonhms.model.Lte;
import org.onap.dcaegen2.services.sonhms.model.LteCell;
import org.onap.dcaegen2.services.sonhms.model.NeighborListInUse;
import org.onap.dcaegen2.services.sonhms.model.Payload;
import org.onap.dcaegen2.services.sonhms.model.PolicyNotification;
import org.onap.dcaegen2.services.sonhms.model.Ran;
import org.onap.dcaegen2.services.sonhms.model.X0005b9Lte;
import org.onap.dcaegen2.services.sonhms.restclient.AnrSolutions;
import org.onap.dcaegen2.services.sonhms.restclient.AsyncResponseBody;
import org.onap.dcaegen2.services.sonhms.restclient.SdnrRestClient;
import org.onap.dcaegen2.services.sonhms.restclient.Solutions;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.slf4j.Logger;

public class ChildThreadUtils {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ChildThreadUtils.class);
    private ConfigPolicy configPolicy;
    private PnfUtils pnfUtils;
    private PolicyDmaapClient policyDmaapClient;

    /**
     * Parameterized constructor.
     */
    public ChildThreadUtils(ConfigPolicy configPolicy, PnfUtils pnfUtils, PolicyDmaapClient policyDmaapClient) {
        this.configPolicy = configPolicy;
        this.pnfUtils = pnfUtils;
        this.policyDmaapClient = policyDmaapClient;
    }

    /**
     * Save request.
     */
    public void saveRequest(String transactionId, long childThreadId) {
        SonRequestsRepository sonRequestsRepository = BeanUtil.getBean(SonRequestsRepository.class);

        SonRequests sonRequest = new SonRequests();
        sonRequest.setTransactionId(transactionId);
        sonRequest.setChildThreadId(childThreadId);
        sonRequestsRepository.save(sonRequest);
    }

    /**
     * Determines whether to trigger Oof or wait for notifications.
     */
    public Boolean triggerOrWait(Map<String, ArrayList<Integer>> collisionConfusionResult) {
        // determine collision or confusion

        Configuration configuration = Configuration.getInstance();
        int collisionSum = 0;
        int confusionSum = 0;

        for (Map.Entry<String, ArrayList<Integer>> entry : collisionConfusionResult.entrySet()) {

            ArrayList<Integer> arr;
            arr = entry.getValue();
            // check for 0 collision and confusion
            if (!arr.isEmpty()) {
                collisionSum = collisionSum + arr.get(0);
                confusionSum = confusionSum + arr.get(1);
            }
        }
        return ((collisionSum >= configuration.getMinCollision()) && (confusionSum >= configuration.getMinConfusion()));

    }

    /**
     * get policy notification string from oof result.
     *
     */
    public String getNotificationString(String pnfName, List<CellPciPair> cellPciPairs, String requestId,
            Long alarmStartTime) {
        ArrayList<Configurations> configurations = new ArrayList<>();
        for (CellPciPair cellPciPair : cellPciPairs) {
            String cellId = cellPciPair.getCellId();
            int pci = cellPciPair.getPhysicalCellId();
            Configurations configuration = new Configurations(new Data(new FapService(cellId,
                    new X0005b9Lte(pci, pnfName), new CellConfig(new Lte(new Ran(new Common(cellId), null))))));
            configurations.add(configuration);
        }

        Payload payload = new Payload(configurations);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        String payloadString = "";
        try {
            payloadString = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.debug("JSON processing exception: {}", e);
        }

        String closedLoopControlName = "ControlLoop-vPCI-fb41f388-a5f2-11e8-98d0-529269fb1459";
        try {
            closedLoopControlName = (String) configPolicy.getConfig().get("PCI_MODCONFIG_POLICY_NAME");
        } catch (NullPointerException e) {
            log.error("Config policy not found, Using default");
        }

        PolicyNotification policyNotification = new PolicyNotification(closedLoopControlName, requestId, alarmStartTime,
                pnfName);

        policyNotification.setClosedLoopControlName(closedLoopControlName);
        policyNotification.setPayload(payloadString);

        String notification = "";
        try {
            notification = mapper.writeValueAsString(policyNotification);
        } catch (JsonProcessingException e1) {
            log.debug("JSON processing exception: {}", e1);
        }
        return notification;
    }

    /**
     * Sends Dmaap notification to Policy.
     *
     * @throws ConfigDbNotFoundException
     *             when config db is unreachable
     */
    public void sendToPolicy(AsyncResponseBody async) throws ConfigDbNotFoundException {

        if (log.isDebugEnabled()) {
            log.debug(async.toString());
        }

        Solutions solutions;
        solutions = async.getSolutions();

        if (!solutions.getPciSolutions().isEmpty()) {
            Map<String, List<CellPciPair>> pnfs = pnfUtils.getPnfs(solutions);

            for (Map.Entry<String, List<CellPciPair>> entry : pnfs.entrySet()) {
                String pnfName = entry.getKey();
                List<CellPciPair> cellPciPairs = entry.getValue();

                String notification = getNotificationString(pnfName, cellPciPairs, UUID.randomUUID().toString(),
                        System.currentTimeMillis());
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
        if (!solutions.getAnrSolutions().isEmpty()) {

            List<Configurations> configurations = new ArrayList<>();
            for (AnrSolutions anrSolution : solutions.getAnrSolutions()) {
                String cellId = anrSolution.getCellId();
                List<LteCell> lteCellList = new ArrayList<>();
                for (String removeableNeighbor : anrSolution.getRemoveableNeighbors()) {
                    LteCell lteCell = new LteCell();
                    lteCell.setBlacklisted("true");
                    lteCell.setPlmnId(solutions.getNetworkId());
                    lteCell.setCid(removeableNeighbor);
                    int pci = SdnrRestClient.getPci(cellId);
                    String pnfName = SdnrRestClient.getPnfName(cellId);
                    lteCell.setPhyCellId(pci);
                    lteCell.setPnfName(pnfName);
                    lteCellList.add(lteCell);
                }
                Configurations configuration = new Configurations(
                        new Data(new FapService(cellId, null, new CellConfig(new Lte(new Ran(new Common(cellId),
                                new NeighborListInUse(null, lteCellList, String.valueOf(lteCellList.size()))))))));
                configurations.add(configuration);
            }
            Payload payload = new Payload(configurations);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
            String anrUpdateString = null;
            try {
                anrUpdateString = mapper.writeValueAsString(payload);
            } catch (JsonProcessingException e) {
                log.error("Exception in writing anrupdate string", e);
            }
            Boolean result = policyDmaapClient.sendNotificationToPolicy(anrUpdateString);
            log.debug("send notification to policy result {} ", result);
        }
    }

}
