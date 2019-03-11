/*******************************************************************************
 * ============LICENSE_START=======================================================
 * son-handler
 *  ================================================================================
 *  Copyright (C) 2019 Wipro Limited.
 *  ==============================================================================
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   ============LICENSE_END=========================================================
 ******************************************************************************/

package org.onap.dcaegen2.services.sonhms.child;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.onap.dcaegen2.services.sonhms.dao.CellInfoRepository;
import org.onap.dcaegen2.services.sonhms.entity.CellInfo;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.restclient.SdnrRestClient;
import org.onap.dcaegen2.services.sonhms.restclient.Solution;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.onap.dcaegen2.services.sonhms.utils.ClusterUtilsTest;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({BeanUtil.class, SdnrRestClient.class })
@SpringBootTest(classes = PnfUtils.class)
public class TestPnfUtils {

    @Mock
    private CellInfoRepository cellInfoRepositoryMock;
    
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TestPnfUtils.class);
    private static List<Solution> solutions = new ArrayList<>();
    private static Optional<CellInfo> cellInfo;
    private static Optional<CellInfo> cellInfoNull;

    
    @InjectMocks
    PnfUtils pnfUtils;
    
     @BeforeClass
     public static void setup() {
         
         
         String solutionsString=readFromFile("/solutions.json");
         ObjectMapper mapper = new ObjectMapper();
            
            try {
                solutions=mapper.readValue(solutionsString,new TypeReference<List<Solution>>(){});
            } catch (IOException e) {
                log.debug("Exception in StateOof Test "+e);
                e.printStackTrace();
            }
         
     }
     @Before
     public void setupTest() {
            cellInfo = Optional.of(new CellInfo("EXP001","ncserver1"));
            cellInfoNull = Optional.ofNullable(null);
            pnfUtils = new PnfUtils();
            MockitoAnnotations.initMocks(this);
        } 
     @Test
     public void getPnfsTest() {
         Map<String, List<CellPciPair>> pnfs = new HashMap<>();
         List<CellPciPair> cellpciPairList1=new ArrayList<>();
         cellpciPairList1.add(new CellPciPair("EXP001",101));
         List<CellPciPair> cellpciPairList2=new ArrayList<>();
         cellpciPairList2.add(new CellPciPair("EXP002",102));
         String pnfName="ncserver2";
         String cellId="EXP002";
         PowerMockito.mockStatic(BeanUtil.class);
         PowerMockito.mockStatic(SdnrRestClient.class);

         PowerMockito.when(BeanUtil.getBean(CellInfoRepository.class))
                 .thenReturn(cellInfoRepositoryMock);
         
        Mockito.when(cellInfoRepositoryMock.findById("EXP001"))
         .thenReturn(cellInfo);
        Mockito.when(cellInfoRepositoryMock.findById(cellId))
        .thenReturn(cellInfoNull);
        try {
            PowerMockito.when(SdnrRestClient.getPnfName(cellId))
            .thenReturn(pnfName);
            PowerMockito.when(cellInfoRepositoryMock.save(new CellInfo(cellId, pnfName))).thenReturn(new CellInfo());
        } catch (ConfigDbNotFoundException e) {
            e.printStackTrace();
        }
        pnfs.put(pnfName, cellpciPairList2);
        pnfs.put("ncserver1", cellpciPairList1);
        System.out.println(solutions);
        try {
            assertEquals(pnfs,pnfUtils.getPnfs(solutions));
        } catch (ConfigDbNotFoundException e) {
            log.debug("exception in stateOof test {}", e);
            e.printStackTrace();
        }
     }
     private static String readFromFile(String file) {
            String content  = new String();
            try {
                
                InputStream is = ClusterUtilsTest.class.getResourceAsStream(file);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                content = bufferedReader.readLine();
                String temp;
                while((temp = bufferedReader.readLine()) != null) {
                    content = content.concat(temp);
                }
                content = content.trim();
                bufferedReader.close();
            }
            catch(Exception e) {
                content  = null;
            }
            return content;
        }
}