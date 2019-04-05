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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestInfo {
    String transactionId = null;
    String requestId = null;
    String callbackUrl = null;
    String sourceId = null;
    String requestType = null;
    int numSolutions;
    List<String> optimizers = null;
    protected Map<String, String> callbackHeader = new HashMap<>();

    
    public Map<String, String> getCallbackHeader() {
        return callbackHeader;
    }

    public void setCallbackHeader(Map<String, String> callbackHeader) {
        this.callbackHeader = callbackHeader;
    }

    public void setOptimizers(List<String> optimizers) {
        this.optimizers = optimizers;
    }

    public List<String> getOptimizers() {
        return optimizers;
    }

    int timeout;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public int getNumSolutions() {
        return numSolutions;
    }

    public void setNumSolutions(int numSolutions) {
        this.numSolutions = numSolutions;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

}
