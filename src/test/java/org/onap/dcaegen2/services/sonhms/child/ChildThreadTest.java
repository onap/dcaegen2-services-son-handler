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

import fj.data.Either;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.HoMetricsComponent;
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
        ArrayList<String> cellidList = new ArrayList<>();
        cellidList.add("cell1");
        //PowerMockito.mockStatic(Configuration.class);
        Configuration config = Configuration.getInstance() ;
        config.setPoorThreshold(70);
        //PowerMockito.when(Configuration.getInstance()).thenReturn(config);
        Either<List<HoDetails>, Integer> response = null;
        HoDetails hoDetail = new HoDetails();
        hoDetail.setDstCellId("dstCell1");
        hoDetail.setSuccessRate(60);
        List<HoDetails> hoDetailsList = new ArrayList<>();
        hoDetailsList.add(hoDetail);
        response = Either.left(hoDetailsList);
        when(hoMetricsComponent.getHoMetrics(Mockito.anyString())).thenReturn(response);
        assertTrue(childThread.checkAnrTrigger(cellidList).isLeft());
    }

}
