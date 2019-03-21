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

package org.onap.dcaegen2.services.sonhms.restclient;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class RequestInfoTest {
    @Test
    public void requestInfoTest() {
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
        assertEquals(1, requestInfo.getNumSolutions());
        assertEquals(optimizers, requestInfo.getOptimizers());
        assertEquals("create", requestInfo.getRequestType());
        assertEquals("PCIHMS", requestInfo.getSourceId());
        assertEquals("3df7b0e9-26d1-4080-ba42-28e8a3139689", requestInfo.getTransactionId());
        assertEquals("e44a4165-3cf4-4362-89de-e2375eed97e7", requestInfo.getRequestId());
        assertEquals(60, requestInfo.getTimeout());
        assertEquals("", requestInfo.getCallbackUrl());
    }

}
