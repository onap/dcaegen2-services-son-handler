/*******************************************************************************
 * ============LICENSE_START=======================================================
 * pcims
 *  ================================================================================
 *  Copyright (C) 2018 Wipro Limited.
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

package org.onap.dcaegen2.services.sonhms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.onap.dcaegen2.services.sonhms.child.Graph;
import org.onap.dcaegen2.services.sonhms.entity.ClusterDetails;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.FapServiceList;
import org.onap.dcaegen2.services.sonhms.model.Notification;
import org.onap.dcaegen2.services.sonhms.utils.ClusterUtils;
import org.onap.dcaegen2.services.sonhms.utils.ClusterUtilsTest;
import org.onap.dcaegen2.services.sonhms.utils.ThreadUtils;

import fj.data.Either;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = EventHandler.class)
public class EventHandlerTest {

    @Mock 
    ClusterUtils clusterutilsMock;
    
    @Mock
    ExecutorService pool;
    
    @Mock
    ThreadUtils threadUtilsMock;
    
    private static Notification notification;
    private static List<ClusterDetails> clusterDetails = new ArrayList<>();
    
    @InjectMocks
    EventHandler eventHandler;
    
    @Before
    public void setup() {
        
        notification = new Notification();
        String notificationString = readFromFile("/notification3.json");
        String clusterInfo1 = readFromFile("/clusterInfo1.json");
        String clusterInfo2 = readFromFile("/clusterInfo2.json");
        String clusterInfo3 = readFromFile("/clusterInfo3.json");
        String clusterInfo4 = readFromFile("/clusterInfo4.json");
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            notification = mapper.readValue(notificationString, Notification.class);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(notification.toString());
        
        clusterDetails.add(new ClusterDetails("1", clusterInfo1, 35));
        clusterDetails.add(new ClusterDetails("2", clusterInfo2, 36));
        clusterDetails.add(new ClusterDetails("3", clusterInfo3, 37));
        clusterDetails.add(new ClusterDetails("4", clusterInfo4, 38));
  
    }
    
    @Test
    public void handleSdnrNotificationTest() {
        
        String clusterInfo7 = readFromFile("/clusterInfo7.json");
        Graph cluster = new Graph(clusterInfo7);
        NotificationToClusterMapping mapping = new NotificationToClusterMapping();
        Map<FapServiceList, String> cellsinCluster = new HashMap<>();
        List<FapServiceList> newCells = new ArrayList<>();
        newCells.add(notification.getPayload().getRadioAccess().getFapServiceList().get(0));
        mapping.setCellsinCluster(cellsinCluster);
        mapping.setNewCells(newCells);
        Either<Graph, Integer> existingCluster = Either.right(404);
        
        
        Mockito.when(clusterutilsMock.getAllClusters()).thenReturn(clusterDetails);
        Mockito.when(clusterutilsMock.getClustersForNotification(notification, clusterDetails)).thenReturn(mapping);
        Mockito.when(clusterutilsMock.getClusterForCell(Mockito.any(), Mockito.any())).thenReturn(existingCluster);
        Mockito.when(threadUtilsMock.createNewThread(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        
        try {
            Mockito.when(clusterutilsMock.createCluster(Mockito.any())).thenReturn(cluster);
        } catch (ConfigDbNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        
        Assert.assertEquals(true, eventHandler.handleSdnrNotification(notification));
        
        
        
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
