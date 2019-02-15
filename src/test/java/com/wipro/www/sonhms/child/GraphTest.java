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

package com.wipro.www.sonhms.child;

import static org.junit.Assert.assertNotEquals;

import com.wipro.www.sonhms.child.Graph;
import com.wipro.www.sonhms.model.CellPciPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;



public class GraphTest {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GraphTest.class);

    @Test
    public void graphTest() {

        CellPciPair cpPair1 = new CellPciPair();
        cpPair1.setCellId("25");
        cpPair1.setPhysicalCellId(32);

        CellPciPair cpPair2 = new CellPciPair();
        cpPair2.setCellId("29");
        cpPair2.setPhysicalCellId(209);

        Graph graph = new Graph();

        graph.addEdge(cpPair1, cpPair2);

        Map<CellPciPair, ArrayList<CellPciPair>> map = new HashMap<>();

        log.debug("graph {}", graph.getCellPciNeighbourMap());
        System.out.println("graph" + graph.getCellPciNeighbourMap());
        assertNotEquals(map, graph.getCellPciNeighbourMap());

    }

}
