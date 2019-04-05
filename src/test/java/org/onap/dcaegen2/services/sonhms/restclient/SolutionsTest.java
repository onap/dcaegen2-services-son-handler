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



public class SolutionsTest {

    @Test
    public void solutionsTest() {

        Solutions solutions = new Solutions();
        List<AnrSolutions> anrSolutionsList = new ArrayList<>();
        List<PciSolutions> pciSolutionsList = new ArrayList<>();
        
        PciSolutions pciSolutions = new PciSolutions();
        pciSolutions.setCellId("EXP001");
        pciSolutions.setPci(101);
        pciSolutionsList.add(pciSolutions);
        AnrSolutions anrSolutions = new AnrSolutions();
        anrSolutions.setCellId("EXP001");
        List<String> removeableNeighbors = new ArrayList<>();
        removeableNeighbors.add("cell001");
        anrSolutions.setRemoveableNeighbors(removeableNeighbors);
        anrSolutionsList.add(anrSolutions);
        solutions.setNetworkId("EXP001");
        solutions.setPciSolutions(pciSolutionsList);
        solutions.setAnrSolutions(anrSolutionsList);
        assertEquals("EXP001", solutions.getNetworkId());
        assertEquals(pciSolutionsList, solutions.getPciSolutions());
        assertEquals(anrSolutionsList, solutions.getAnrSolutions());

    }

}
