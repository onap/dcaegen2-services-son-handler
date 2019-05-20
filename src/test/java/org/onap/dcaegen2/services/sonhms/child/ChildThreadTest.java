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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import fj.data.Either;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.HoMetricsComponent;
import org.onap.dcaegen2.services.sonhms.entity.HandOverMetrics;
import org.onap.dcaegen2.services.sonhms.model.HoDetails;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = ChildThreadTest.class)
public class ChildThreadTest {

    @Mock
    HoMetricsComponent hoMetricsComponent;
    
    @InjectMocks
    ChildThread childThread;
    
    @Test
    public void checkAnrTriggerTest() {
        //PowerMockito.mockStatic(Configuration.class);
        Configuration config = Configuration.getInstance() ;
        config.setPoorThreshold(70);
        config.setPoorCountThreshold(3);
        //PowerMockito.when(Configuration.getInstance()).thenReturn(config);
        Either<List<HandOverMetrics>, Integer> response = null;
        List<HoDetails> hoDetailsList = new ArrayList<>(); 
        List<HandOverMetrics> hoMetrics = new ArrayList<>();
        HoDetails hoDetail = new HoDetails();
        hoDetail.setDstCellId("dstCell1");
        hoDetail.setSuccessRate(60);
        hoDetail.setPoorCount(4);
        hoDetailsList.add(hoDetail);
        HandOverMetrics hoMetric = new HandOverMetrics();
        hoMetric.setSrcCellId("cell1");
        ObjectMapper mapper = new ObjectMapper();
        String hoDetailsString = null;
        try {
            hoDetailsString = mapper.writeValueAsString(hoDetailsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        hoMetric.setHoDetails(hoDetailsString);
        hoMetrics.add(hoMetric);
        response = Either.left(hoMetrics);
        when(hoMetricsComponent.getAll()).thenReturn(response);
        assertTrue(childThread.checkAnrTrigger().isLeft());
    }

}
