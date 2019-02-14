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
import static org.junit.Assert.assertTrue;

import com.wipro.www.sonhms.SdnrNotificationHandlingState;
import com.wipro.www.sonhms.SonContext;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;



public class SonContextTest {

    private BlockingQueue<List<String>> childStatusUpdate;

    @Test
    public void sonContextTest() {
        SonContext sonContext = new SonContext();
        sonContext.setChildThreadId(1);
        sonContext.setNotifToBeProcessed(true);
        sonContext.setSdnrNotification("notification");
        sonContext.setChildStatusUpdate(childStatusUpdate);
        NewNotification newNotification = new NewNotification(true);
        sonContext.setNewNotification(newNotification);
        SdnrNotificationHandlingState pciState = new SdnrNotificationHandlingState();
        sonContext.setPciState(pciState);
        assertEquals(1, sonContext.getChildThreadId());
        assertTrue(sonContext.isNotifToBeProcessed());
        assertEquals("notification", sonContext.getSdnrNotification());
        assertEquals(pciState, sonContext.getPciState());
        assertEquals(childStatusUpdate, sonContext.getChildStatusUpdate());
        assertEquals(newNotification, sonContext.getNewNotification());

    }
}
