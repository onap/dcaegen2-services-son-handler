/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2019 Wipro Limited.
 *   Copyright (C) 2022 Wipro Limited.
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

package org.onap.dcaegen2.services.sonhms.entity;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;

import org.junit.Test;

public class BufferedNotificationsTest {

    private Timestamp createdAt;

    @Test
    public void bufferedNotificationsTest() {
        BufferedNotifications bufferedNotifications=new BufferedNotifications();
        bufferedNotifications.setClusterId("clusterId");
        bufferedNotifications.setNotification("notification");
        bufferedNotifications.setCreatedAt(createdAt);
        assertEquals("clusterId", bufferedNotifications.getClusterId());
        assertEquals("notification", bufferedNotifications.getNotification());
        assertEquals(createdAt, bufferedNotifications.getCreatedAt());
        BufferedNotifications bufferedNotifications2=new BufferedNotifications("notification", createdAt,"clusterId");
        bufferedNotifications2.setClusterId("clusterId");
        bufferedNotifications2.setNotification("notification");
        assertEquals("clusterId", bufferedNotifications2.getClusterId());
        assertEquals("notification", bufferedNotifications2.getNotification());
        assertEquals(createdAt, bufferedNotifications2.getCreatedAt());
    }
}
