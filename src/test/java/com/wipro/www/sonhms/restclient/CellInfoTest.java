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

package com.wipro.www.sonhms.restclient;

import static org.junit.Assert.assertEquals;

import com.wipro.www.sonhms.restclient.CellInfo;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class CellInfoTest {
    @Test
    public void cellInfoTest() {
        List<String> cellIdLists = new ArrayList<>();
        cellIdLists.add("cell1");

        CellInfo cellInfo = new CellInfo();
        cellInfo.setNetworkId("NTWK001");
        cellInfo.setCellIdList(cellIdLists);
        assertEquals("NTWK001", cellInfo.getNetworkId());
        assertEquals(cellIdLists, cellInfo.getCellIdList());

    }
}
