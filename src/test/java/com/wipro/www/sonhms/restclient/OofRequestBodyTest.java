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
import com.wipro.www.sonhms.restclient.OofRequestBody;
import com.wipro.www.sonhms.restclient.RequestInfo;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;



public class OofRequestBodyTest {
    @Test
    public void oofRequestBodyTest() {

        List<String> cellIdLists = new ArrayList<>();
        cellIdLists.add("cell1");
        CellInfo cellInfo = new CellInfo();
        cellInfo.setNetworkId("NTWK001");
        cellInfo.setCellIdList(cellIdLists);
        RequestInfo requestInfo = new RequestInfo();
        List<String> optimizers = new ArrayList<String>();
        optimizers.add("PCI");
        requestInfo.setCallbackUrl("");
        requestInfo.setNumSolutions(1);
        requestInfo.setOptimizers(optimizers);
        requestInfo.setRequestId("e44a4165-3cf4-4362-89de-e2375eed97e7");
        requestInfo.setRequestType("create");
        requestInfo.setSourceId("PCIHMS");
        requestInfo.setTimeout(60);
        requestInfo.setTransactionId("3df7b0e9-26d1-4080-ba42-28e8a3139689");

        OofRequestBody oofRequestBody = new OofRequestBody();
        oofRequestBody.setCellInfo(cellInfo);
        oofRequestBody.setRequestInfo(requestInfo);
        assertEquals(requestInfo, oofRequestBody.getRequestInfo());
        assertEquals(cellInfo, oofRequestBody.getCellInfo());
    }
}
