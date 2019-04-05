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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import fj.data.Either;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.onap.dcaegen2.services.sonhms.dao.HandOverMetricsRepository;
import org.onap.dcaegen2.services.sonhms.model.HoDetails;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({ BeanUtil.class })
@SpringBootTest(classes = HoMetricsComponentTest.class)
public class HoMetricsComponentTest {
    
    @Mock
    HandOverMetricsRepository handOverMetricsRepositoryMock;
    
    @InjectMocks
    HoMetricsComponent hoMetricsComponent;
    
    static String hoDetailsString;
    
    @BeforeClass
    public static void setup() {
        hoDetailsString=readFromFile("/hoDetails.json");
    }
    
    @Test
    public void getHoMetricsTest() {
        PowerMockito.mockStatic(BeanUtil.class);
        PowerMockito.when(BeanUtil
                .getBean(HandOverMetricsRepository.class)).thenReturn(handOverMetricsRepositoryMock);
        when(handOverMetricsRepositoryMock.getHandOverMetrics("")).thenReturn(hoDetailsString);
        Either<List<HoDetails>, Integer> result = hoMetricsComponent.getHoMetrics("");
        assertNotNull(result.left().value());
        when(handOverMetricsRepositoryMock.getHandOverMetrics("")).thenReturn("wrongText");
        result = hoMetricsComponent.getHoMetrics("");
        int res= result.right().value();
        assertEquals(400,res);
    }
    
    private static String readFromFile(String file) { 
        String content = new String();
        try {

            InputStream is = HoMetricsComponentTest.class.getResourceAsStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            content = bufferedReader.readLine();
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                content = content.concat(temp);
            }
            content = content.trim();
            bufferedReader.close();
        } catch (Exception e) {
            content = null;
        }
        return content;
    }
}
