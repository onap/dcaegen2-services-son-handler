package com.wipro.www.sonhms;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.wipro.www.sonhms.dao.ClusterDetailsRepository;
import com.wipro.www.sonhms.entity.ClusterDetails;
import com.wipro.www.sonhms.utils.BeanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.ProjectedPayload;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class) 
@PrepareForTest({BeanUtil.class})
@SpringBootTest(classes = ClusterDetailsComponent.class)
public class ClusterDetailsComponentTest {

    @Mock
    private ClusterDetailsRepository clusterDetailsRepositorymock;
    

    @InjectMocks
    private ClusterDetailsComponent clusterDetailsComponent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getClusterDetailsTest() {
        ClusterDetails clusterDetails = new ClusterDetails("clusterId", "clusterInfo", 1);
        List<ClusterDetails> list = new ArrayList<ClusterDetails>();
        list.add(clusterDetails);

        PowerMockito.mockStatic(BeanUtil.class);
        PowerMockito.when(BeanUtil.getBean(ClusterDetailsRepository.class)).thenReturn(clusterDetailsRepositorymock);
        System.out.println(clusterDetailsRepositorymock);
        when(clusterDetailsRepositorymock.getAllClusterDetails()).thenReturn((list));

        List<ClusterDetails> resultList = new ArrayList<ClusterDetails>();
        resultList = clusterDetailsComponent.getClusterDetails();
        for (ClusterDetails each : resultList) {
            assertEquals("clusterId", each.getClusterId());
            assertEquals("clusterInfo", each.getClusterInfo());
            assertEquals(1, each.getChildThreadId());

        }
        
    }

    @Test
    public void getChildThreadTest() {
        String clusterId = "clusterId";

        PowerMockito.mockStatic(BeanUtil.class);
        PowerMockito.when(BeanUtil.getBean(ClusterDetailsRepository.class)).thenReturn(clusterDetailsRepositorymock);
        when(clusterDetailsRepositorymock.getChildThreadForCluster(clusterId)).thenReturn((long)1);
        long childThreadId = clusterDetailsComponent.getChildThread(clusterId);
        assertEquals(1, childThreadId);

    }

    @Test
    public void getClusterIdTest() {
        long childThreadId = 1;

        PowerMockito.mockStatic(BeanUtil.class);
        PowerMockito.when(BeanUtil.getBean(ClusterDetailsRepository.class)).thenReturn(clusterDetailsRepositorymock);
        when(clusterDetailsRepositorymock.getClusterIdForChildThread(childThreadId))
                .thenReturn("clusterId");
        String clusterId = clusterDetailsComponent.getClusterId(childThreadId);
        assertEquals("clusterId", clusterId);
    }
}
