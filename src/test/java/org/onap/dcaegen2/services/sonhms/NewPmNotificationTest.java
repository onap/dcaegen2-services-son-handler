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

package org.onap.dcaegen2.services.sonhms;

import static org.junit.Assert.*;

import org.junit.Test;

public class NewPmNotificationTest {

	@Test
	public void test() {
		NewPmNotification newPmNotification = new NewPmNotification();
		newPmNotification.setNewNotif(true);
		assertTrue(newPmNotification.getNewNotif());
                NewPmNotification newPmNotification1 = new NewPmNotification(true);
                newPmNotification1.setNewNotif(true);
                assertEquals(true, newPmNotification1.getNewNotif());
                NewPmNotification newPmNotification2 = new NewPmNotification();
                newPmNotification2.setNewNotif(false);
                newPmNotification2.init();
	}

}
