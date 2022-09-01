/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2019-2022 Wipro Limited.
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

import fj.data.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.onap.dcaegen2.services.sonhms.child.ChildThreadUtils;
import org.onap.dcaegen2.services.sonhms.child.PnfUtils;
import org.onap.dcaegen2.services.sonhms.dao.HandOverMetricsRepository;
import org.onap.dcaegen2.services.sonhms.dmaap.PolicyDmaapClient;
import org.onap.dcaegen2.services.sonhms.entity.HandOverMetrics;
import org.onap.dcaegen2.services.sonhms.model.AdditionalMeasurements;
import org.onap.dcaegen2.services.sonhms.model.ANRPayload;
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
import org.onap.dcaegen2.services.sonhms.model.Neighbours;
import org.onap.dcaegen2.services.sonhms.model.Payload;
import org.onap.dcaegen2.services.sonhms.model.PmNotification;
import org.onap.dcaegen2.services.sonhms.model.PolicyData;
import org.onap.dcaegen2.services.sonhms.model.Ran;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.onap.dcaegen2.services.sonhms.utils.DmaapUtils;
import org.onap.dcaegen2.services.sonhms.restclient.ConfigurationClient;
import org.onap.dcaegen2.services.sonhms.restclient.CpsClient;
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
    public Boolean handlePmNotifications(PmNotification pmNotification, int badThreshold, int poorThreshold,
            int badCountThreshold) {
        Boolean result;
        Boolean newEntryFlag = false;
        try {
            List<HoDetails> hoDetailsList = new ArrayList<>();
            List<Neighbours> neighbourList = new ArrayList<>();
            String srcCellId = pmNotification.getEvent().getCommonEventHeader().getSourceName();
            /*
             * check whether entry already exists if yes : read the hometrics and update it
             * with latest info else store the current data
             */
            HoMetricsComponent hoMetricsComponent = new HoMetricsComponent();
            Either<List<HoDetails>, Integer> hoMetrics = hoMetricsComponent.getHoMetrics(srcCellId);
            Map<String, Integer> dstCellIdPcPair = new HashMap<>();
            Map<String, Integer> dstCellIdBcPair = new HashMap<>();
            if (hoMetrics.isLeft()) {
                List<HoDetails> oldHoDetailsList = hoMetrics.left().value();
                for (HoDetails hodetail : oldHoDetailsList) {
                    dstCellIdBcPair.put(hodetail.getDstCellId(), hodetail.getBadCount());
                    dstCellIdPcPair.put(hodetail.getDstCellId(), hodetail.getPoorCount());
                }

            } else if (hoMetrics.right().value() == 404) {
                newEntryFlag = true;
                log.info("no history of srcCell found");
            }
            for (AdditionalMeasurements additionalMeasurements : pmNotification.getEvent().getMeasurementFields()
                    .getAdditionalMeasurements()) {
                int attemptsCount = Integer.parseInt(additionalMeasurements.getHashMap().get("InterEnbOutAtt_X2HO"));
                int successCount = Integer.parseInt(additionalMeasurements.getHashMap().get("InterEnbOutSucc_X2HO"));
                int successRate = (int)((float) successCount / attemptsCount) * 100;

                Neighbours neighbourCell = new Neighbours();
                neighbourCell.setHoKpi(successRate);
                neighbourCell.setCellId(additionalMeasurements.getName());
                neighbourCell.setPlmnId(additionalMeasurements.getHashMap().get("networkId"));
                neighbourCell.setPnfName(pmNotification.getEvent().getCommonEventHeader().getReportingEntityName());
                neighbourList.add(neighbourCell);
            }
            if (!neighbourList.isEmpty()) {
                log.info("triggering policy to remove bad neighbors");
                Flag policyTriggerFlag = BeanUtil.getBean(Flag.class);

                while (policyTriggerFlag.getHolder().equals("CHILD")) {
                    Thread.sleep(100);
                }

                policyTriggerFlag.setHolder("PM");
                result = sendAnrUpdateToPolicy(pmNotification, neighbourList);
                log.info("Sent ANR update to policy {}", result);
                policyTriggerFlag.setHolder("NONE");
            }
            if (!hoDetailsList.isEmpty() && newEntryFlag) {
                result = saveToHandOverMetrics(hoDetailsList, srcCellId);
                log.debug("save HO metrics result {} ", result);

            } else if (!hoDetailsList.isEmpty()) {
                String hoDetailsString = null;
                ObjectMapper mapper = new ObjectMapper();
                try {
                    hoDetailsString = mapper.writeValueAsString(hoDetailsList);
                } catch (Exception e) {
                    log.error("Error in writing handover metrics json ", e);
                    return false;
                }
                hoMetricsComponent.update(hoDetailsString, srcCellId);
            }

        } catch (Exception e) {
            log.error("Error in handlePmNotifications ", e);
            return false;
        }
        return true;

    }

    private Boolean sendAnrUpdateToPolicy(PmNotification pmNotification, List<Neighbours> neighbourList) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.setSerializationInclusion(Include.NON_NULL);
            String cellId = pmNotification.getEvent().getCommonEventHeader().getSourceName();
            String pnfName = pmNotification.getEvent().getCommonEventHeader().getReportingEntityName();
            String plmnId =  pmNotification.getEvent().getMeasurementFields()
               .getAdditionalMeasurements().get(0).getHashMap().get("networkId");
            String ric_id = CpsClient.getRicId(cellId);
            ANRPayload payload = new ANRPayload("CreatePolicy",1,1,ric_id,
                    (new PolicyData(pnfName,plmnId,cellId,neighbourList)));
            log.info("payload : {}", payload);
            String anrUpdateString = mapper.writeValueAsString(payload);
            ChildThreadUtils childUtils = new ChildThreadUtils(ConfigPolicy.getInstance(), new PnfUtils(),
                    new PolicyDmaapClient(new DmaapUtils(), Configuration.getInstance()), new HoMetricsComponent());
            String requestId = UUID.randomUUID().toString();
            String notification = childUtils.getNotificationString(
                    pmNotification.getEvent().getCommonEventHeader().getReportingEntityName(), requestId,
                    anrUpdateString, System.currentTimeMillis(), "ModifyA1Policy");
            log.info("Policy Notification: {}", notification);
            Boolean result = policyDmaapClient.sendNotificationToPolicy(notification);
            log.info("send notification to policy result {} ", result);

        } catch (Exception e) {
            log.error("Exception in sending Anr update to policy ", e);
            return false;
        }
        return true;
    }

    private Boolean saveToHandOverMetrics(List<HoDetails> hoDetailsList, String srcCellId) {
        ObjectMapper mapper = new ObjectMapper();
        String hoDetailsString = null;
        try {
            hoDetailsString = mapper.writeValueAsString(hoDetailsList);
        } catch (Exception e) {
            log.error("Error in writing handover metrics json ", e);
            return false;
        }
        HandOverMetrics handOverMetrics = new HandOverMetrics();
        handOverMetrics.setHoDetails(hoDetailsString);
        handOverMetrics.setSrcCellId(srcCellId);
        HandOverMetricsRepository handOverMetricsRepository = BeanUtil.getBean(HandOverMetricsRepository.class);
        handOverMetricsRepository.save(handOverMetrics);
        return true;
    }
}
