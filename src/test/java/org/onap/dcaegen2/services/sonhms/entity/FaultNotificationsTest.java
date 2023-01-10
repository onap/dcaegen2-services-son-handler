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

public class FaultNotificationsTest {
    
    private Timestamp createdAt;

    @Test
    public void faultNotififcationsTest() {
        FaultNotifications faultNotifications = new FaultNotifications();
        faultNotifications.setNotification("notification");
        faultNotifications.setCreatedAt(createdAt);
        assertEquals("notification", faultNotifications.getNotification());
        assertEquals(createdAt, faultNotifications.getCreatedAt());
        FaultNotifications faultNotifications2=new FaultNotifications("notifications", createdAt);
        faultNotifications2.setNotification("notifications");
        assertEquals("notifications", faultNotifications2.getNotification());
        assertEquals(createdAt,faultNotifications2.getCreatedAt());
    }

}


