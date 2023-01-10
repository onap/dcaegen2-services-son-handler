/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2019-2020 Wipro Limited.
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

import static org.junit.Assert.*;

import org.junit.Test;

public class PciUpdateTest {

	@Test
	public void test() {

		PciUpdate pciUpdate = new PciUpdate();
		pciUpdate.setCellId("Chn0001");
		pciUpdate.setNewPci(5000);
		pciUpdate.setOldPci(3000);
		pciUpdate.setNegativeAckCount(3);

		assertEquals("Chn0001", pciUpdate.getCellId());
		assertEquals(5000, pciUpdate.getNewPci());
		assertEquals(3000, pciUpdate.getOldPci());
		assertEquals(3, pciUpdate.getNegativeAckCount());
                PciUpdate pciUpdate1 = new PciUpdate("Chn0001",3000,5000,3);
                assertEquals("Chn0001", pciUpdate1.getCellId());
                assertEquals(5000, pciUpdate1.getNewPci());
                assertEquals(3000, pciUpdate1.getOldPci());
                assertEquals(3, pciUpdate1.getNegativeAckCount());

	}

}
