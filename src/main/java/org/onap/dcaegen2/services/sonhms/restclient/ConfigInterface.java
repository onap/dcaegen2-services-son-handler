/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2021 Wipro Limited.
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

import org.json.JSONObject;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.exceptions.CpsNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;

import java.util.List;

/**
 * An abstract base class which contains the methods for CpsClient and SdnrRestClient classes
 *
 * @see CpsClient,SdnrRestClient
 */

public abstract class ConfigInterface
{
    /**
     * Abstract method to get neighbour list from SDNR or CPS based on the client
     * mentioned in configuration.
     *
     * @throws ConfigDbNotFoundException,CpsNotFoundException when request to configDB or CPS fails
     * @see org.onap.dcaegen2.services.sonhms.Configuration
     */
    public abstract List<CellPciPair> getNbrList(String cellId) throws ConfigDbNotFoundException, CpsNotFoundException;

    /**
     * Abstract method to get PCI from SDNR or CPS based on the client mentioned.
     *
     * @throws ConfigDbNotFoundException,CpsNotFoundException when request to configDB or CPS fails
     * @see org.onap.dcaegen2.services.sonhms.Configuration
     */
    public abstract int getPci(String cellId) throws ConfigDbNotFoundException, CpsNotFoundException;

    /**
     * Abstract method to get PNF name from SDNR or CPS based on the client mentioned.
     *
     * @throws ConfigDbNotFoundException,CpsNotFoundException
     *             when request to configDB or CPS fails
     * @see org.onap.dcaegen2.services.sonhms.Configuration
     */
    public abstract String getPnfName(String cellId) throws ConfigDbNotFoundException, CpsNotFoundException;

    /**
     * Abstract method to get CellData name from SDNR or CPS based on the client mentioned.
     *
     * @throws ConfigDbNotFoundException,CpsNotFoundException
     *             when request to configDB or CPS fails
     * @see org.onap.dcaegen2.services.sonhms.Configuration
     */
    public abstract JSONObject getCellData(String cellId) throws ConfigDbNotFoundException, CpsNotFoundException;
}