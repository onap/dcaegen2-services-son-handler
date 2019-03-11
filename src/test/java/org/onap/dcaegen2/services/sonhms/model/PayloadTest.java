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

package org.onap.dcaegen2.services.sonhms.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.onap.dcaegen2.services.sonhms.model.CellConfig;
import org.onap.dcaegen2.services.sonhms.model.Common;
import org.onap.dcaegen2.services.sonhms.model.Configurations;
import org.onap.dcaegen2.services.sonhms.model.Data;
import org.onap.dcaegen2.services.sonhms.model.FapService;
import org.onap.dcaegen2.services.sonhms.model.Lte;
import org.onap.dcaegen2.services.sonhms.model.Payload;
import org.onap.dcaegen2.services.sonhms.model.Ran;
import org.onap.dcaegen2.services.sonhms.model.X0005b9Lte;



public class PayloadTest {

    @Test
    public void payloadTest() {
        Common common = new Common("cell1");

        Ran ran = new Ran(common);

        Lte lte = new Lte(ran);

        CellConfig cellConfig = new CellConfig(lte);

        X0005b9Lte x0005b9Lte = new X0005b9Lte(0, "pnf2");

        FapService fapService = new FapService("cell6", x0005b9Lte, cellConfig);

        Data data = new Data(fapService);

        Configurations config = new Configurations(data);
        ArrayList<Configurations> al = new ArrayList<>();
        al.add(config);

        Payload payload = new Payload(al);

        assertEquals("pnf2", payload.getConfiguration().get(0).getData().getFapservice().getX0005b9Lte().getPnfName());

        assertEquals("cell6", payload.getConfiguration().get(0).getData().getFapservice().getAlias());

    }

}
