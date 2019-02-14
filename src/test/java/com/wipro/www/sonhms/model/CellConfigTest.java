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

package com.wipro.www.sonhms.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CellConfigTest {

    @Test
    public void cellConfigTest() {
               
        Common common = new Common();
        common.setCellIdentity("cellIdentity");
        Ran ran = new Ran();
        ran.setCommon(common);
        Lte lte = new Lte();
        lte.setRan(ran);
        CellConfig cellConfig = new CellConfig();
        cellConfig.setLte(lte);
        assertEquals(lte, cellConfig.getLte());
        assertEquals(ran, lte.getRan());
        assertEquals(common, ran.getCommon());
    }
}
