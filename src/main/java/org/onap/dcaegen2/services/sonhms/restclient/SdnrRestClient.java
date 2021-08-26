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

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.utils.SonHandlerRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class SdnrRestClient extends ConfigInterface{

    private static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
    private static Logger log = LoggerFactory.getLogger(SdnrRestClient.class);

    public SdnrRestClient()
    {

    }

    @Override
    public List<CellPciPair> getNbrList(String cellId) throws ConfigDbNotFoundException
    {
        Configuration configuration = Configuration.getInstance();
        String ts = new SimpleDateFormat(DATETIMEFORMAT).format(new Time(System.currentTimeMillis()));
        String requestUrl = configuration.getConfigDbService() + "/api/sdnc-config-db/v3/getNbrList" + "/" + cellId
                + "/" + ts;
        log.debug("request url: {}", requestUrl);
        String response = sendRequest(requestUrl);
        List<CellPciPair> nbrList = new ArrayList<>();
        JSONObject responseJson = new JSONObject(response);
        JSONArray nbrListObj = responseJson.getJSONArray("nbrList");
        for (int i = 0; i < nbrListObj.length(); i++) {
            JSONObject cellObj = nbrListObj.getJSONObject(i);
            if (cellObj.getBoolean("ho")) {
                CellPciPair cell = new CellPciPair(cellObj.getString("targetCellId"), cellObj.getInt("pciValue"));
                nbrList.add(cell);
            }
        }

        return nbrList;
    }

    /**
     * Method to get PCI from SDNR.
     *
     * @throws ConfigDbNotFoundException
     *             when request to configDB fails
     */

    @Override
    public int getPci(String cellId) throws ConfigDbNotFoundException {
        Configuration configuration = Configuration.getInstance();
        String ts = new SimpleDateFormat(DATETIMEFORMAT).format(new Time(System.currentTimeMillis()));
        String requestUrl = configuration.getConfigDbService() + "/api/sdnc-config-db/v3/getPCI" + "/" + cellId + "/"
                + ts;
        String response = sendRequest(requestUrl);
        JSONObject respObj = new JSONObject(response);
        return respObj.getInt("value");
    }

    /**
     * Method to get PNF name from SDNR.
     *
     * @throws ConfigDbNotFoundException
     *             when request to configDB fails
     */
    @Override
    public String getPnfName(String cellId) throws ConfigDbNotFoundException {
        Configuration configuration = Configuration.getInstance();
        String ts = new SimpleDateFormat(DATETIMEFORMAT).format(new Time(System.currentTimeMillis()));
        String requestUrl = configuration.getConfigDbService() + "/api/sdnc-config-db/v3/getPnfId" + "/" + cellId + "/"
                + ts;
        String response = sendRequest(requestUrl);
        JSONObject responseObject = new JSONObject(response);
        return responseObject.getString("value");
    }

    /**
     * Method to get CellData name from SDNR.
     *
     * @throws ConfigDbNotFoundException
     *             when request to configDB fails
     */

    @Override
    public JSONObject getCellData(String cellId) throws ConfigDbNotFoundException {
        Configuration configuration = Configuration.getInstance();
        String requestUrl = configuration.getConfigDbService() + "/api/sdnc-config-db/v3/getCell" + "/" + cellId;
        String response = sendRequest(requestUrl);
        JSONObject responseObject = new JSONObject(response);
        return responseObject;
    }

    /**
     * Method to send request.
     */
    private static String sendRequest(String url) throws ConfigDbNotFoundException {
        ResponseEntity<String> response = SonHandlerRestTemplate.sendGetRequest(url,
                new ParameterizedTypeReference<String>() {
                });
        if (response == null) {
            throw new ConfigDbNotFoundException("Cannot reach Config DB");
        }
        return response.getBody();
    }

}
