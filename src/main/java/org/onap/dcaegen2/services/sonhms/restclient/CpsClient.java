/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2021-2022 Wipro Limited.
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
 *   ============LICENSE_END=========================================================
 *
 *******************************************************************************/

package org.onap.dcaegen2.services.sonhms.restclient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.exceptions.CpsNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.utils.SonHandlerRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

/**
 *  A subclass which contains the methods
 *  to get required information from the CPS Client
 *
 */

public class CpsClient extends ConfigInterface {

    private static Logger log = LoggerFactory.getLogger(CpsClient.class);

    public CpsClient() {

    }

    /**
     * Method to get neighbour list from CPS.
     *
     * @throws CpsNotFoundException when request to CPS fails
     */
    @Override
    public List<CellPciPair> getNbrList(String cellId) throws CpsNotFoundException {

        Configuration configuration = Configuration.getInstance();
        String requestUrl = configuration.getCpsServiceUrl() + "/" + configuration.getGetNbrListUrl();
        JSONObject inputparam = new JSONObject();
        JSONObject reqbody = new JSONObject();
        inputparam.put("cellId", cellId);
        reqbody.put("inputParameters", inputparam);
        log.debug("request url: {}", requestUrl);
        String response = sendRequest(requestUrl, reqbody);
        log.info("Response from CPS is : " + response);
        List<CellPciPair> nbrList = new ArrayList<>();
        JSONObject responseJson = new JSONObject(response);
        JSONArray nbrListObj = responseJson.getJSONArray("NRCellRelation");
        for (int i = 0; i < nbrListObj.length(); i++) {
            JSONObject cellObj = nbrListObj.getJSONObject(i);
            JSONObject obj = cellObj.getJSONObject("attributes");
            if (obj.getBoolean("isHOAllowed")) {
                CellPciPair cell = new CellPciPair(obj.getString("nRTCI"), obj.getInt("nRPCI"));
                nbrList.add(cell);
            }
        }

        return nbrList;
    }

    /**
     * Method to get PCI from CPS.
     *
     * @throws CpsNotFoundException when request to CPS fails
     */
    @Override
    public int getPci(String cellId) throws CpsNotFoundException {

        Configuration configuration = Configuration.getInstance();
        int responseObject = 0;
        String requestUrl = configuration.getCpsServiceUrl() + "/" + configuration.getGetPciUrl();
        JSONObject inputparam = new JSONObject();
        JSONObject reqbody = new JSONObject();
        inputparam.put("cellId", cellId);
        reqbody.put("inputParameters", inputparam);
        String response = sendRequest(requestUrl, reqbody);
        log.info("Response from CPS is : " + response);
        JSONArray requestArray = new JSONArray(response);
        for (int i=0;i<requestArray.length();i++) {
            int pciValue = requestArray.getJSONObject(i).getInt("nRPCI");
            responseObject = pciValue;
            log.info("The nRPCI value is : " + responseObject);
        }
        return responseObject;
    }

    /**
     * Method to get PNF name from CPS.
     *
     * @throws CpsNotFoundException when request to CPS fails
     */
    @Override
    public String getPnfName(String cellId) throws CpsNotFoundException {
        Configuration configuration = Configuration.getInstance();
        String responseObject = "";
        String requestUrl = configuration.getCpsServiceUrl() + "/" + configuration.getGetPnfUrl();
        JSONObject inputparam = new JSONObject();
        JSONObject reqbody = new JSONObject();
        inputparam.put("cellId", cellId);
        reqbody.put("inputParameters", inputparam);
        String response = sendRequest(requestUrl, reqbody);
        log.info("Response from CPS is : " + response);
        JSONArray requestArray = new JSONArray(response);
        for (int i=0;i<requestArray.length();i++) {
            String pnfName = requestArray.getJSONObject(i).optString("idGNBDUFunction");
            responseObject = pnfName;
        }
        return responseObject;
    }

    /*
     * Method to get NearRTRIC ID from CPS.
     *
     * @throws CpsNotFoundException when request to CPS fails
     */

    public static String getRicId(String cellId) throws CpsNotFoundException {
        Configuration configuration = Configuration.getInstance();
        String responseObject = "";
        String requestUrl = configuration.getCpsServiceUrl() + "/" + configuration.getGetRicIdUrl();
        JSONObject inputparam = new JSONObject();
        JSONObject reqbody = new JSONObject();
        inputparam.put("cellId", cellId);
        reqbody.put("inputParameters", inputparam);
        String response = sendRequest(requestUrl, reqbody);
        log.info("Response from CPS is : " + response);
        JSONArray requestArray = new JSONArray(response);
        for (int i=0;i<requestArray.length();i++) {
            String ricId = requestArray.getJSONObject(i).optString("idNearRTRIC");
            responseObject = ricId;
        }
        return responseObject;
    }

    /**
     * Method to get CellData name from CPS.
     *
     * @throws CpsNotFoundException when request to CPS fails
     */
    @Override
    public JSONObject getCellData(String cellId) throws CpsNotFoundException {

        Configuration configuration = Configuration.getInstance();
        String requestUrl = configuration.getCpsServiceUrl() + "/" + configuration.getGetCellDataUrl();
        JSONObject inputparam = new JSONObject();
        JSONObject reqbody = new JSONObject();
        inputparam.put("cellId", cellId);
        reqbody.put("inputParameters", inputparam);
        String response = sendRequest(requestUrl, reqbody);
        log.info("Response from CPS is : " + response);
        JSONObject responseObject = new JSONObject(response);
        return responseObject;
    }

    private static String sendRequest(String url, JSONObject reqbody) throws CpsNotFoundException {
        ResponseEntity<String> response = SonHandlerRestTemplate.sendPostRequest(url, reqbody.toString(),
                new ParameterizedTypeReference<String>() {
                });
        if (response == null) {
            throw new CpsNotFoundException("Cannot reach CPS");
        }
        return response.getBody();
    }

}
