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
import org.onap.dcaegen2.services.sonhms.ConfigPolicy;
import org.onap.dcaegen2.services.sonhms.model.CellConfig;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.model.Common;
import org.onap.dcaegen2.services.sonhms.model.Configurations;
import org.onap.dcaegen2.services.sonhms.model.Data;
import org.onap.dcaegen2.services.sonhms.model.FapService;
import org.onap.dcaegen2.services.sonhms.model.Lte;
import org.onap.dcaegen2.services.sonhms.model.Payload;
import org.onap.dcaegen2.services.sonhms.model.PolicyNotification;
import org.onap.dcaegen2.services.sonhms.model.Ran;
import org.onap.dcaegen2.services.sonhms.model.X0005b9Lte;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class ChildThreadUtils {
    
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ChildThreadUtils.class);
    ConfigPolicy configPolicy;
    
    public ChildThreadUtils(ConfigPolicy configPolicy) {
        this.configPolicy = configPolicy;
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
                    new X0005b9Lte(pci, pnfName), new CellConfig(new Lte(new Ran(new Common(cellId)))))));
            configurations.add(configuration);
        }

        Payload payload = new Payload(configurations);
        ObjectMapper mapper = new ObjectMapper();
        String payloadString = "";
        try {
            payloadString = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.debug("JSON processing exception: {}", e);
        }
        
        String closedLoopControlName = (String) configPolicy.getConfig().get("PCI_MODCONFIG_POLICY_NAME");
        PolicyNotification policyNotification = new PolicyNotification(closedLoopControlName, 
                requestId, alarmStartTime, pnfName);
        
        policyNotification.setClosedLoopControlName(closedLoopControlName);
        policyNotification.setPayload(payloadString);

        mapper.setSerializationInclusion(Include.NON_NULL);
        String notification = "";
        try {
            notification = mapper.writeValueAsString(policyNotification);
        } catch (JsonProcessingException e1) {
            log.debug("JSON processing exception: {}", e1);
        }
        return notification;
    }
    
}
