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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import fj.data.Either;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.onap.dcaegen2.services.sonhms.dao.DmaapNotificationsRepository;
import org.onap.dcaegen2.services.sonhms.dao.PerformanceNotificationsRepository;
import org.onap.dcaegen2.services.sonhms.model.Notification;
import org.onap.dcaegen2.services.sonhms.model.PmNotification;
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
@SpringBootTest(classes = DmaapNotificationsComponentTest.class)

public class DmaapNotificationsComponentTest {

	@Mock
	DmaapNotificationsRepository dmaapNotificationsRepositoryMock;
	
	@Mock
	PerformanceNotificationsRepository performanceNotificationsRepositoryMock;

	@InjectMocks
	DmaapNotificationsComponent component;

	static String notificationString;
	static String pmNotificationString;


	@BeforeClass
	public static void setupTest() {

		notificationString = readFromFile("/notification1.json");
		pmNotificationString=readFromFile("/pmNotification.json");

	}

	@Test
	public void getDmaapNotificationsTestforLeft() {
		PowerMockito.mockStatic(BeanUtil.class);
		PowerMockito.when(BeanUtil.getBean(DmaapNotificationsRepository.class))
				.thenReturn(dmaapNotificationsRepositoryMock);
		when(dmaapNotificationsRepositoryMock.getNotificationFromQueue()).thenReturn(notificationString);


		Either<Notification, Integer> result = component.getSdnrNotifications();
		//assertTrue(result.isLeft());
		assertNotNull(result.left().value());
		
		when(dmaapNotificationsRepositoryMock.getNotificationFromQueue()).thenReturn("notification");

        result = component.getSdnrNotifications();
        int resultRight = result.right().value();
        assertEquals(400, resultRight);
		
	}
	
	@Test
	public void getPmNotificationsTest() {
	    PowerMockito.mockStatic(BeanUtil.class);
        PowerMockito.when(BeanUtil.getBean(PerformanceNotificationsRepository.class))
                .thenReturn(performanceNotificationsRepositoryMock);
        when(performanceNotificationsRepositoryMock.getPerformanceNotificationFromQueue()).thenReturn(pmNotificationString);
        
        Either<PmNotification,Integer> result = component.getPmNotifications();
        assertTrue(result.isLeft());
        assertNotNull(result.left().value());
        
        when(performanceNotificationsRepositoryMock.getPerformanceNotificationFromQueue()).thenReturn("pmNotification");
        result = component.getPmNotifications();
        int res= result.right().value();
        assertEquals(400,res);
        
	}

	private static String readFromFile(String file) { 
		String content = new String();
		try {

			InputStream is = DmaapNotificationsComponentTest.class.getResourceAsStream(file);
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
