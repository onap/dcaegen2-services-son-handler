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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fj.data.Either;

import java.util.ArrayList;
import java.util.List;

import org.onap.dcaegen2.services.sonhms.dao.HandOverMetricsRepository;
import org.onap.dcaegen2.services.sonhms.model.HoDetails;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HoMetricsComponent {

    private static Logger log = LoggerFactory.getLogger(HoMetricsComponent.class);

    /**
     * Get HO metrics.
     */

    public Either<List<HoDetails>, Integer> getHoMetrics(String srcCellId) {
        HandOverMetricsRepository handOverMetricsRepository = BeanUtil.getBean(HandOverMetricsRepository.class);
        String hoDetailsString = handOverMetricsRepository.getHandOverMetrics(srcCellId);
        if (hoDetailsString != null) {
            ObjectMapper mapper = new ObjectMapper();
            List<HoDetails> hoDetails = new ArrayList<>();
            try {
                hoDetails = mapper.readValue(hoDetailsString, new TypeReference<ArrayList<HoDetails>>() {
                });
                return Either.left(hoDetails);
            } catch (Exception e) {
                log.error("Exception in parsing HO metrics", hoDetailsString, e);
                return Either.right(400);
            }
        } else
            return Either.right(404);
    }
}
