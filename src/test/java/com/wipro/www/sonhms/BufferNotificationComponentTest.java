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

package com.wipro.www.sonhms;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wipro.www.sonhms.dao.BufferedNotificationsRepository;
import com.wipro.www.sonhms.entity.BufferedNotifications;
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
@PrepareForTest({ BeanUtil.class })
@SpringBootTest(classes = BufferNotificationComponent.class)
public class BufferNotificationComponentTest {

    @Mock
    private BufferedNotificationsRepository bufferedNotificationsRepositoryMock;

    @InjectMocks
    private BufferNotificationComponent bufferNotificationComponent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void bufferNotificationTest() {
        BufferedNotifications bufferedNotifications = new BufferedNotifications();
        bufferedNotifications.setClusterId("clusterId");
        bufferedNotifications.setNotification("notification");
        PowerMockito.mockStatic(BeanUtil.class);
        PowerMockito.when(BeanUtil.getBean(BufferedNotificationsRepository.class))
                .thenReturn(bufferedNotificationsRepositoryMock);
        when(bufferedNotificationsRepositoryMock.save(bufferedNotifications)).thenReturn(bufferedNotifications);
        BufferedNotifications bufferedNotificationsResult = new BufferedNotifications();
        bufferNotificationComponent.bufferNotification("notification", "clusterId");
        assertEquals(bufferedNotifications, bufferedNotificationsRepositoryMock.save(bufferedNotifications));

    }
    
    @Test
    public void getBufferedNotificationTest() {
        List<String> notificationsList = new ArrayList<String>();
        notificationsList.add("NOTIF1");
        notificationsList.add("NOTIF2");
        String clusterId = "clusterId";
        PowerMockito.mockStatic(BeanUtil.class);
        PowerMockito.when(BeanUtil.getBean(BufferedNotificationsRepository.class))
                .thenReturn(bufferedNotificationsRepositoryMock);
        when(bufferedNotificationsRepositoryMock.getNotificationsFromQueue(clusterId)).thenReturn((notificationsList));

        List<String> testResult = new ArrayList<String>();
        testResult = bufferNotificationComponent.getBufferedNotification(clusterId);
        for (int i = 1; i <= testResult.size(); i++) {
            assertEquals("NOTIF" + i, testResult.get(i - 1));
        }
    }
    
    @Test
    public void getClusterIdTest() {
        String notification = "NOTIF1";
        PowerMockito.mockStatic(BeanUtil.class);
        PowerMockito.when(BeanUtil.getBean(BufferedNotificationsRepository.class))
                .thenReturn(bufferedNotificationsRepositoryMock);
        when(bufferedNotificationsRepositoryMock.getClusterIdForNotification(notification)).thenReturn(("clusterId"));
        String clusterId = bufferNotificationComponent.getClusterId(notification);
        assertEquals("clusterId", clusterId);
    }

}
