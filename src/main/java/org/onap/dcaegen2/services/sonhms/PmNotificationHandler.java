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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.onap.dcaegen2.services.sonhms.child.ChildThreadUtils;
import org.onap.dcaegen2.services.sonhms.child.PnfUtils;
import org.onap.dcaegen2.services.sonhms.dao.HandOverMetricsRepository;
import org.onap.dcaegen2.services.sonhms.dmaap.PolicyDmaapClient;
import org.onap.dcaegen2.services.sonhms.entity.HandOverMetrics;
import org.onap.dcaegen2.services.sonhms.model.AdditionalMeasurements;
import org.onap.dcaegen2.services.sonhms.model.CellConfig;
import org.onap.dcaegen2.services.sonhms.model.Common;
import org.onap.dcaegen2.services.sonhms.model.Configurations;
import org.onap.dcaegen2.services.sonhms.model.Data;
import org.onap.dcaegen2.services.sonhms.model.FapService;
import org.onap.dcaegen2.services.sonhms.model.Flag;
import org.onap.dcaegen2.services.sonhms.model.HoDetails;
import org.onap.dcaegen2.services.sonhms.model.Lte;
import org.onap.dcaegen2.services.sonhms.model.LteCell;
import org.onap.dcaegen2.services.sonhms.model.NeighborListInUse;
import org.onap.dcaegen2.services.sonhms.model.PMNotification;
import org.onap.dcaegen2.services.sonhms.model.Payload;
import org.onap.dcaegen2.services.sonhms.model.Ran;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PmNotificationHandler {

    private static Logger log = LoggerFactory.getLogger(PmNotificationHandler.class);
    PolicyDmaapClient policyDmaapClient;

    public PmNotificationHandler() {

    }

    public PmNotificationHandler(PolicyDmaapClient policyDmaapClient) {
        this.policyDmaapClient = policyDmaapClient;
    }

    /**
     * handle PM notifications.
     */
    public Boolean handlePmNotifications(PMNotification pmNotification, int badThreshold) {
        HandOverMetricsRepository handOverMetricsRepository = BeanUtil.getBean(HandOverMetricsRepository.class);
        Boolean result;
        try {
            List<HoDetails> hoDetailsList = new ArrayList<>();
            List<LteCell> lteCellList = new ArrayList<>();
            String srcCellId = pmNotification.getEvent().getCommonEventHeader().getSourceName();
            for (AdditionalMeasurements additionalMeasurements : pmNotification.getEvent().getMeasurementFields()
                    .getAdditionalMeasurements()) {
                int attemptsCount = Integer.parseInt(additionalMeasurements.getHashMap().get("InterEnbOutAtt_X2HO"));
                int successCount = Integer.parseInt(additionalMeasurements.getHashMap().get("InterEnbOutSucc_X2HO"));
                float successRate = ((float) successCount / attemptsCount) * 100;
                if (successRate >= badThreshold) {
                    HoDetails hoDetails = new HoDetails();
                    hoDetails.setDstCellId(additionalMeasurements.getName());
                    hoDetails.setAttemptsCount(attemptsCount);
                    hoDetails.setSuccessCount(successCount);
                    hoDetails.setSuccessRate(successRate);
                    hoDetailsList.add(hoDetails);
                    log.info("not bad  neighbor {}", additionalMeasurements.getName());
                } else {
                    log.info(" bad  neighbor {}", additionalMeasurements.getName());
                    LteCell lteCell = new LteCell();
                    lteCell.setBlacklisted("true");
                    lteCell.setCid(additionalMeasurements.getName());
                    lteCell.setPlmnId(additionalMeasurements.getHashMap().get("networkId"));
                    lteCell.setPnfName(pmNotification.getEvent().getCommonEventHeader().getReportingEntityName());
                    lteCellList.add(lteCell);
                }
            }
            if (!lteCellList.isEmpty()) {
                log.info("triggering policy to remove bad neighbors");
                Flag policyTriggerFlag = BeanUtil.getBean(Flag.class);

                while (policyTriggerFlag.getHolder().equals("CHILD")) {
                    Thread.sleep(100);
                }

                policyTriggerFlag.setHolder("PM");
                result = sendAnrUpdateToPolicy(pmNotification, lteCellList);
                log.info("Sent ANR update to policy {}", result);
                policyTriggerFlag.setHolder("NONE");

                String hoDetailsString = handOverMetricsRepository.getHandOverMetrics(srcCellId);
                if (hoDetailsString != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    String newHoDetailsString = null;
                    try {
                        newHoDetailsString = mapper.writeValueAsString(hoDetailsList);
                    } catch (Exception e) {
                        log.error("Error in writing handover metrics json ", e);
                        return false;
                    }
                    handOverMetricsRepository.updateHoMetrics(newHoDetailsString, srcCellId);
                }
            }
            if (!hoDetailsList.isEmpty()) {
                result = saveToHandOverMetrics(hoDetailsList, srcCellId);
                log.debug("save HO metrics result {} ", result);

            }

        } catch (Exception e) {
            log.error("Error in handlePmNotifications ", e);
            return false;
        }
        return true;

    }

    private Boolean sendAnrUpdateToPolicy(PMNotification pmNotification, List<LteCell> lteCellList) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.setSerializationInclusion(Include.NON_NULL);
            ArrayList<Configurations> configurations = new ArrayList<>();
            String cellId = pmNotification.getEvent().getCommonEventHeader().getSourceName();
            Configurations configuration = new Configurations(
                    new Data(new FapService(cellId, null,
                            new CellConfig(new Lte(new Ran(new Common(cellId),
                                    new NeighborListInUse(null, lteCellList, String.valueOf(lteCellList.size()))))))),
                    null);
            configurations.add(configuration);
            Payload payload = new Payload(configurations);
            log.info("payload : {}", payload);
            String anrUpdateString = mapper.writeValueAsString(payload);
            ChildThreadUtils childUtils = new ChildThreadUtils(ConfigPolicy.getInstance(), new PnfUtils(),
                    new PolicyDmaapClient());
            String requestId=UUID.randomUUID().toString();
            String notification = childUtils.getNotificationString(
                    pmNotification.getEvent().getCommonEventHeader().getReportingEntityName(),
                    requestId, anrUpdateString, System.currentTimeMillis(), "ModifyConfigANR");
            log.info("Policy Notification: {}", notification);
            Boolean result = policyDmaapClient.sendNotificationToPolicy(notification);
            log.info("send notification to policy result {} ", result);
            // getting policy response
            policyDmaapClient.handlePolicyResponse(requestId);
            log.info("handled policy response");


        } catch (Exception e) {
            log.error("Exception in sending Anr update to policy ", e);
            return false;
        }
        return true;
    }

    private Boolean saveToHandOverMetrics(List<HoDetails> hoDetailsList, String srcCellId) {
        HandOverMetricsRepository handOverMetricsRepository = BeanUtil.getBean(HandOverMetricsRepository.class);
        ObjectMapper mapper = new ObjectMapper();
        String hoDetailsString = null;
        try {
            hoDetailsString = mapper.writeValueAsString(hoDetailsList);
        } catch (Exception e) {
            log.error("Error in writing handover metrics json ", e);
            return false;
        }

        if (handOverMetricsRepository.getHandOverMetrics(srcCellId) == null) {
            HandOverMetrics handOverMetrics = new HandOverMetrics();
            handOverMetrics.setHoDetails(hoDetailsString);
            handOverMetrics.setSrcCellId(srcCellId);
            handOverMetricsRepository.save(handOverMetrics);
        } else {
            handOverMetricsRepository.updateHoMetrics(hoDetailsString, srcCellId);
        }
        return true;
    }
}
