/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2019-2020 Wipro Limited.
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

public abstract class ConfigInterface
{
	public abstract List<CellPciPair> getNbrList(String cellId) throws ConfigDbNotFoundException, CpsNotFoundException;
	public abstract int getPci(String cellId) throws ConfigDbNotFoundException, CpsNotFoundException;
	public abstract String getPnfName(String cellId) throws ConfigDbNotFoundException, CpsNotFoundException;
	public abstract JSONObject getCellData(String cellId) throws ConfigDbNotFoundException, CpsNotFoundException;

}
