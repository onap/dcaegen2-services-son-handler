/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2019-2021 Wipro Limited.
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

package org.onap.dcaegen2.services.sonhms.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fj.data.Either;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.dcaegen2.services.sonhms.NotificationToClusterMapping;
import org.onap.dcaegen2.services.sonhms.child.Graph;
import org.onap.dcaegen2.services.sonhms.dao.ClusterDetailsRepository;
import org.onap.dcaegen2.services.sonhms.entity.ClusterDetails;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.model.FapServiceList;
import org.onap.dcaegen2.services.sonhms.model.Notification;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.exceptions.CpsNotFoundException;
import org.onap.dcaegen2.services.sonhms.restclient.ConfigurationClient;
import org.onap.dcaegen2.services.sonhms.restclient.SdnrRestClient;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({ SdnrRestClient.class, BeanUtil.class, ConfigurationClient.class })
@SpringBootTest(classes = ClusterUtils.class)
public class ClusterUtilsTest {

    @Mock
    private ClusterDetailsRepository clusterDetailsRepositoryMock;

    @InjectMocks
    ClusterUtils clusterUtils;

    private static Notification notification1;
    private static Notification notification2;
    private static List<ClusterDetails> clusterDetailsForGetClusterDetailsFromClusterIdTest;
    private static Graph cluster;
    private static List<ClusterDetails> clusterDetails = new ArrayList<>();

    @BeforeClass
    public static void setup() {

        Configuration config = Configuration.getInstance();
        config.setConfigClientType("ConfigDB");
        notification1 = new Notification();
        notification2 = new Notification();
        clusterDetailsForGetClusterDetailsFromClusterIdTest = new ArrayList<ClusterDetails>();

        String notificationString1 = readFromFile("/notification1.json");
        String notificationString2 = readFromFile("/notification2.json");
        String clusterDetailsListString = readFromFile("/ClusterDetailsTest.json");

        String clusterInfo1 = readFromFile("/clusterInfo1.json");
        String clusterInfo2 = readFromFile("/clusterInfo2.json");
        String clusterInfo3 = readFromFile("/clusterInfo3.json");
        String clusterInfo4 = readFromFile("/clusterInfo4.json");
        String clusterInfo = readFromFile("/clusterInfo5.json");
        cluster = new Graph(clusterInfo);

        clusterDetails.add(new ClusterDetails("1", clusterInfo1, 35));
        clusterDetails.add(new ClusterDetails("2", clusterInfo2, 36));
        clusterDetails.add(new ClusterDetails("3", clusterInfo3, 37));
        clusterDetails.add(new ClusterDetails("4", clusterInfo4, 38));

        ObjectMapper mapper = new ObjectMapper();

        try {
            notification1 = mapper.readValue(notificationString1, Notification.class);
            notification2 = mapper.readValue(notificationString2, Notification.class);
            clusterDetailsForGetClusterDetailsFromClusterIdTest = mapper.readValue(clusterDetailsListString,
                    new TypeReference<List<ClusterDetails>>() {
                    });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Before
    public void setupTest() {
        clusterUtils = new ClusterUtils();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getClustersForNotificationTest() {

        NotificationToClusterMapping expected = new NotificationToClusterMapping();
        Map<FapServiceList, String> cellsinCluster = new HashMap<>();
        cellsinCluster.put(notification1.getPayload().getRadioAccess().getFapServiceList().get(0), "2");
        expected.setCellsinCluster(cellsinCluster);
        expected.setNewCells(new ArrayList<FapServiceList>());

        NotificationToClusterMapping result = clusterUtils.getClustersForNotification(notification1, clusterDetails);
        assertEquals(expected, result);

        expected = new NotificationToClusterMapping();
        List<FapServiceList> newCells = new ArrayList<>();
        newCells.add(notification2.getPayload().getRadioAccess().getFapServiceList().get(0));
        expected.setCellsinCluster(new HashMap<>());
        expected.setNewCells(newCells);

        result = clusterUtils.getClustersForNotification(notification2, clusterDetails);
        assertEquals(expected, result);
    }

    @Test
    public void createClusterTest() throws Exception {

        Map<CellPciPair, ArrayList<CellPciPair>> clusterMap = new HashMap<CellPciPair, ArrayList<CellPciPair>>();

        List<CellPciPair> firstNbrList = new ArrayList<>();
        List<CellPciPair> nbrList = new ArrayList<>();

        firstNbrList.add(new CellPciPair("48", 0));
        nbrList.add(new CellPciPair("44", 3));

        PowerMockito.mockStatic(SdnrRestClient.class);
        PowerMockito.mockStatic(ConfigurationClient.class);

        SdnrRestClient sdnr = PowerMockito.spy(new SdnrRestClient());
        Configuration config = Configuration.getInstance();

        PowerMockito.whenNew(SdnrRestClient.class).withAnyArguments().thenReturn(sdnr);
        PowerMockito.when(ConfigurationClient.configClient(config.getConfigClientType()))
                .thenReturn(sdnr);
        PowerMockito.doReturn(nbrList).when(sdnr, "getNbrList", Mockito.anyString());

        clusterMap.put(new CellPciPair("45", 310), (ArrayList<CellPciPair>) firstNbrList);

        assertEquals(cluster, clusterUtils.createCluster(clusterMap));

    }

    @Test
    public void getClusterDetailsFromClusterIdTest() {
        ClusterDetails responseValue = null;
        Integer responseVal = null;
        Integer expectedValue = 404;
        Either<ClusterDetails, Integer> response = clusterUtils.getClusterDetailsFromClusterId("0",
                clusterDetailsForGetClusterDetailsFromClusterIdTest);
        assertTrue(response.isLeft());
        if (response.isLeft()) {
            responseValue = response.left().value();
        }
        assertEquals(clusterDetailsForGetClusterDetailsFromClusterIdTest.get(0), responseValue);
        response = clusterUtils.getClusterDetailsFromClusterId("1",
                clusterDetailsForGetClusterDetailsFromClusterIdTest);
        assertTrue(response.isLeft());
        if (response.isLeft()) {
            responseValue = response.left().value();
        }
        assertEquals(clusterDetailsForGetClusterDetailsFromClusterIdTest.get(1), responseValue);
        response = clusterUtils.getClusterDetailsFromClusterId("9",
                clusterDetailsForGetClusterDetailsFromClusterIdTest);
        assertTrue(response.isRight());
        if (response.isRight()) {
            responseVal = response.right().value();
        }
        assertEquals(expectedValue, responseVal);

    }

    @Test
    public void saveClusterTest() {
        ClusterDetails details = new ClusterDetails();
        details.setClusterId("123e4567-e89b-12d3-a456-426655440000");
        details.setClusterInfo("cellPciNeighbourString");
        details.setChildThreadId(978668);
        PowerMockito.mockStatic(BeanUtil.class);
        PowerMockito.when(BeanUtil.getBean(ClusterDetailsRepository.class)).thenReturn(clusterDetailsRepositoryMock);
        Mockito.when(clusterDetailsRepositoryMock.save(details)).thenReturn(details);
        Long threadId = (long) 978668;
        clusterUtils.saveCluster(cluster, UUID.fromString("123e4567-e89b-12d3-a456-426655440000"), threadId);
        assertEquals(details, clusterDetailsRepositoryMock.save(details));

    }

    @Test
    public void getClusterForCellTest() {
        FapServiceList fapServiceList = notification1.getPayload().getRadioAccess().getFapServiceList().get(0);
        String clusterInfo1 = readFromFile("/clusterInfo1.json");
        String clusterInfo2 = readFromFile("/clusterInfo2.json");
        Graph graph1 = new Graph(clusterInfo1);
        Graph graph2 = new Graph(clusterInfo2);
        List<Graph> newClusters = new ArrayList<Graph>();
        newClusters.add(graph1);
        newClusters.add(graph2);
        Either<Graph, Integer> result = clusterUtils.getClusterForCell(fapServiceList, newClusters);
        assertTrue(result.isLeft());

        newClusters = new ArrayList<>();
        newClusters.add(graph1);
        result = clusterUtils.getClusterForCell(fapServiceList, newClusters);
        assertTrue(result.isRight());
        int resultRight = result.right().value();
        assertEquals(404, resultRight);

        List<Graph> emptyList = new ArrayList<Graph>();

        result = clusterUtils.getClusterForCell(fapServiceList, emptyList);
        assertTrue(result.isRight());
        resultRight = result.right().value();
        assertEquals(404, resultRight);

    }

    @Test
    public void modifyClusterTest() {

        String clusterInfo = readFromFile("/clusterInfo2.json");
        String clusterInfo2 = readFromFile("/clusterInfo6.json");

        Graph cluster = new Graph(clusterInfo);
        Graph expected = new Graph(clusterInfo2);
        Map<CellPciPair, ArrayList<CellPciPair>> clusterMap = new HashMap<CellPciPair, ArrayList<CellPciPair>>();

        ArrayList<CellPciPair> firstNbrList = new ArrayList<>();
        firstNbrList.add(new CellPciPair("48",0));
        clusterMap.put(new CellPciPair("45",310),firstNbrList);
        assertEquals(expected, clusterUtils.modifyCluster(cluster,clusterMap));
    }

    private static String readFromFile(String file) {
        String content = new String();
        try {

            InputStream is = ClusterUtilsTest.class.getResourceAsStream(file);
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
