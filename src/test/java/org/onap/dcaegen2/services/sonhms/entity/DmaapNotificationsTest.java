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

public class DmaapNotificationsTest {
    
    private Timestamp createdAt;

    @Test
    public void dmaapNotififcationsTest() {
        DmaapNotifications dmaapNotifications = new DmaapNotifications();
        dmaapNotifications.setNotification("notification");
        dmaapNotifications.setCreatedAt(createdAt);
        assertEquals("notification", dmaapNotifications.getNotification());
        assertEquals(createdAt, dmaapNotifications.getCreatedAt());
        DmaapNotifications dmaapNotifications2=new DmaapNotifications("notifications", createdAt);
        dmaapNotifications2.setNotification("notifications");
        assertEquals("notifications",dmaapNotifications2.getNotification());
        assertEquals(createdAt, dmaapNotifications2.getCreatedAt());
    }

}
