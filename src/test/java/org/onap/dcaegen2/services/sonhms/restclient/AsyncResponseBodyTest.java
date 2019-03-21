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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;



public class AsyncResponseBodyTest {

    @Test
    public void asyncResponseBodyTest() {
        AsyncResponseBody asyncResponseBody = new AsyncResponseBody();
        asyncResponseBody.setRequestId("e44a4165-3cf4-4362-89de-e2375eed97e7");
        asyncResponseBody.setRequestStatus("completed");
        SonSolution pciSolutions = new SonSolution();
        pciSolutions.setCellId("EXP001");
        pciSolutions.setPci(101);
        List<SonSolution> pciSolutionsList = new ArrayList<SonSolution>();
        pciSolutionsList.add(pciSolutions);
        Solution solutions = new Solution();
        solutions.setFinishTime("2018-10-01T00:40+01.00");
        solutions.setNetworkId("EXP001");
        solutions.setPciSolutions(pciSolutionsList);
        solutions.setStartTime("2018-10-01T00:30+01:00");
        ArrayList<Solution> solutionsList = new ArrayList<Solution>();
        solutionsList.add(solutions);
        asyncResponseBody.setSolutions(solutionsList);
        asyncResponseBody.setStatusMessage("success");
        asyncResponseBody.setTransactionId("3df7b0e9-26d1-4080-ba42-28e8a3139689");
    }
}
