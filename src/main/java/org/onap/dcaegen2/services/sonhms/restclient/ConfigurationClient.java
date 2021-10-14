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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class with method to get the Client type depending on the
 * configuration client specified.
 *
 * @see org.onap.dcaegen2.services.sonhms.Configuration
 */

public class ConfigurationClient
{
     private static Logger log = LoggerFactory.getLogger(ConfigurationClient.class);

    /**
     * Method to get the Client type.
     *
     * @param config_name client name(CPS or ConfigDB)
     * @return configuration client type
     *
     */
    public static ConfigInterface configClient(String config_name)
    {
        if (config_name == null || config_name.isEmpty()){
            log.info("Returning null from ConfigClient class");
	    return null;
	}
        if ("ConfigDB".equalsIgnoreCase(config_name)) {
	    log.info("Creating SdnrClient object");
            return new SdnrRestClient();
        }
        if ("CPS".equalsIgnoreCase(config_name)) {
	    log.info("Creating CPSClient object");
            return new CpsClient();
        }
        return null;
    }
}
