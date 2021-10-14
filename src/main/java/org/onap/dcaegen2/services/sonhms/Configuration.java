/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2019-2021 Wipro Limited.
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

package org.onap.dcaegen2.services.sonhms;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.onap.dcaegen2.services.sonhms.restclient.ConfigInterface;
import org.onap.dcaegen2.services.sonhms.restclient.ConfigurationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Configuration {

    private static Logger log = LoggerFactory.getLogger(Configuration.class);

    private static Configuration instance = null;
    private String pgHost;
    private int pgPort;
    private String pgUsername;
    private String pgPassword;
    private List<String> dmaapServers;
    private String configDbService;
    private String cpsServiceUrl;
    private String getCellDataUrl;
    private String getNbrListUrl;
    private String getPciUrl;
    private String getPnfUrl;
    private String oofService;
    private String oofEndpoint;
    private String cg;
    private String cid;
    private int pollingInterval;
    private int pollingTimeout;
    private int minCollision;
    private int minConfusion;
    private String sourceId;
    private String callbackUrl;
    private String pciOptimizer;
    private String pciAnrOptimizer;
    private int numSolutions;
    private int bufferTime;
    private int maximumClusters;
    private String aafUsername;
    private String aafPassword;
    private Map<String, Object> streamsSubscribes;
    private Map<String, Object> streamsPublishes;
    private int badThreshold;
    private int poorThreshold;
    private int poorCountThreshold;
    private int badCountThreshold;
    private int oofTriggerCountTimer;
    private int oofTriggerCountThreshold;
    private int policyRespTimer;
    private int policyNegativeAckThreshold;
    private long policyFixedPciTimeInterval;
    private String nfNamingCode;
    private String ConfigClientType;
    private String CpsUsername;
    private String CpsPassword;

    public String getCpsUsername()
    {
        return CpsUsername;
    }
    public void setCpsUsername(String CpsUsername)
    {
        this.CpsUsername = CpsUsername;
    }
    public String getCpsPassword()
    {
        return CpsPassword;
    }
    public void setCpsPassword(String CpsPassword)
    {
        this.CpsPassword = CpsPassword;
    }

    public String getConfigClientType()
    {
        return ConfigClientType;
    }
    public void setConfigClientType(String ConfigClientType)
    {
        this.ConfigClientType = ConfigClientType;
    }

    public int getPoorCountThreshold() {
        return poorCountThreshold;
    }

    public void setPoorCountThreshold(int poorCountThreshold) {
        this.poorCountThreshold = poorCountThreshold;
    }

    public int getBadCountThreshold() {
        return badCountThreshold;
    }

    public void setBadCountThreshold(int badCountThreshold) {
        this.badCountThreshold = badCountThreshold;
    }

    public int getOofTriggerCountTimer() {
        return oofTriggerCountTimer;
    }

    public void setOofTriggerCountTimer(int oofTriggerCountTimer) {
        this.oofTriggerCountTimer = oofTriggerCountTimer;
    }

    public int getOofTriggerCountThreshold() {
        return oofTriggerCountThreshold;
    }

    public void setOofTriggerCountThreshold(int oofTriggerCountThreshold) {
        this.oofTriggerCountThreshold = oofTriggerCountThreshold;
    }

    public int getPolicyRespTimer() {
        return policyRespTimer;
    }

    public void setPolicyRespTimer(int policyRespTimer) {
        this.policyRespTimer = policyRespTimer;
    }

    public int getBadThreshold() {
        return badThreshold;
    }

    public void setBadThreshold(int badThreshold) {
        this.badThreshold = badThreshold;
    }

    public int getPoorThreshold() {
        return poorThreshold;
    }

    public void setPoorThreshold(int poorThreshold) {
        this.poorThreshold = poorThreshold;
    }

    /**
     * Check if topic is secure.
     */
    public boolean isSecured() {
        return (aafUsername != null);

    }

    public String getAafUsername() {
        return aafUsername;
    }

    public void setAafUsername(String aafUsername) {
        this.aafUsername = aafUsername;
    }

    public String getAafPassword() {
        return aafPassword;
    }

    public void setAafPassword(String aafPassword) {
        this.aafPassword = aafPassword;
    }

    public Map<String, Object> getStreamsSubscribes() {
        return streamsSubscribes;
    }

    public void setStreamsSubscribes(Map<String, Object> streamsSubscribes) {
        this.streamsSubscribes = streamsSubscribes;
    }

    public Map<String, Object> getStreamsPublishes() {
        return streamsPublishes;
    }

    public void setStreamsPublishes(Map<String, Object> streamsPublishes) {
        this.streamsPublishes = streamsPublishes;
    }

    public int getMaximumClusters() {
        return maximumClusters;
    }

    public void setMaximumClusters(int maximumClusters) {
        this.maximumClusters = maximumClusters;
    }

    protected Configuration() {

    }

    /**
     * Get instance of class.
     */
    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public String getCg() {
        return cg;
    }

    public void setCg(String cg) {
        this.cg = cg;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public int getPollingTimeout() {
        return pollingTimeout;
    }

    public void setPollingTimeout(int pollingTimeout) {
        this.pollingTimeout = pollingTimeout;
    }

    public int getMinCollision() {
        return minCollision;
    }

    public void setMinCollision(int minCollision) {
        this.minCollision = minCollision;
    }

    public int getMinConfusion() {
        return minConfusion;
    }

    public void setMinConfusion(int minConfusion) {
        this.minConfusion = minConfusion;
    }

    public String getOofService() {
        return oofService;
    }

    public void setOofService(String oofService) {
        this.oofService = oofService;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public int getNumSolutions() {
        return numSolutions;
    }

    public void setNumSolutions(int numSolutions) {
        this.numSolutions = numSolutions;
    }

    public int getBufferTime() {
        return bufferTime;
    }

    public void setBufferTime(int bufferTime) {
        this.bufferTime = bufferTime;
    }

    public String getPgHost() {
        return pgHost;
    }

    public void setPgHost(String pgHost) {
        this.pgHost = pgHost;
    }

    public int getPgPort() {
        return pgPort;
    }

    public void setPgPort(int pgPort) {
        this.pgPort = pgPort;
    }

    public String getPgUsername() {
        return pgUsername;
    }

    public void setPgUsername(String pgUsername) {
        this.pgUsername = pgUsername;
    }

    public String getPgPassword() {
        return pgPassword;
    }

    public void setPgPassword(String pgPassword) {
        this.pgPassword = pgPassword;
    }

    public List<String> getDmaapServers() {
        return dmaapServers;
    }

    public void setDmaapServers(List<String> dmaapServers) {
        this.dmaapServers = dmaapServers;
    }

    public String getConfigDbService() {
        return configDbService;
    }

    public void setConfigDbService(String configDbService) {
        this.configDbService = configDbService;
    }

    public String getPciOptimizer() {
        return pciOptimizer;
    }

    public void setPciOptimizer(String pciOptimizer) {
        this.pciOptimizer = pciOptimizer;
    }

    public String getPciAnrOptimizer() {
        return pciAnrOptimizer;
    }

    public void setPciAnrOptimizer(String pciAnrOptimizer) {
        this.pciAnrOptimizer = pciAnrOptimizer;
    }

    public String getOofEndpoint() {
        return oofEndpoint;
    }

    public void setOofEndpoint(String oofEndpoint) {
        this.oofEndpoint = oofEndpoint;
    }

    public int getPolicyNegativeAckThreshold() {
		return policyNegativeAckThreshold;
	}

	public void setPolicyNegativeAckThreshold(int policyNegativeAckThreshold) {
		this.policyNegativeAckThreshold = policyNegativeAckThreshold;
	}

	public long getPolicyFixedPciTimeInterval() {
		return policyFixedPciTimeInterval;
	}

	public void setPolicyFixedPciTimeInterval(long policyFixedPciTimeInterval) {
		this.policyFixedPciTimeInterval = policyFixedPciTimeInterval;
	}
	
	public String getNfNamingCode() {
		return nfNamingCode;
	}

	public void setNfNamingCode(String nfNamingCode) {
		this.nfNamingCode = nfNamingCode;
	}

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        Configuration.log = log;
    }

    public String getCpsServiceUrl() {
        return cpsServiceUrl;
    }

    public void setCpsServiceUrl(String cpsServiceUrl) {
        this.cpsServiceUrl = cpsServiceUrl;
    }

    public String getGetCellDataUrl() {
        return getCellDataUrl;
    }

    public void setGetCellDataUrl(String getCellDataUrl) {
        this.getCellDataUrl = getCellDataUrl;
    }

    public String getGetNbrListUrl() {
        return getNbrListUrl;
    }

    public void setGetNbrListUrl(String getNbrListUrl) {
        this.getNbrListUrl = getNbrListUrl;
    }

    public String getGetPciUrl() {
        return getPciUrl;
    }

    public void setGetPciUrl(String getPciUrl) {
        this.getPciUrl = getPciUrl;
    }

    public String getGetPnfUrl() {
        return getPnfUrl;
    }

    public void setGetPnfUrl(String getPnfUrl) {
        this.getPnfUrl = getPnfUrl;
    }

    public static void setInstance(Configuration instance) {
        Configuration.instance = instance;
    }

    public ConfigInterface getConfigurationClient()
    {
        ConfigInterface conf = ConfigurationClient.configClient(Configuration.getInstance().getConfigClientType());
        log.info("ConfigurationClient obj is : " + conf);
        return conf;
    }

    @Override
    public String toString() {
        return "Configuration [pgHost=" + pgHost + ", pgPort=" + pgPort + ", pgUsername=" + pgUsername + ", pgPassword="
                + pgPassword + ", dmaapServers=" + dmaapServers + ", configDbService=" + configDbService
                + ", cpsServiceUrl=" + cpsServiceUrl + ", CpsUsername=" + CpsUsername + ",CpsPassword=" + CpsPassword + ",ConfigClientType=" + ConfigClientType + ", getCellDataUrl=" + getCellDataUrl + ", getNbrListUrl="
                + getNbrListUrl + ", getPciUrl=" + getPciUrl + ", getPnfUrl=" + getPnfUrl + ", oofService=" + oofService + ", oofEndpoint=" + oofEndpoint + ", cg=" + cg + ", cid=" + cid
                + ", pollingInterval=" + pollingInterval + ", pollingTimeout=" + pollingTimeout + ", minCollision="
                + minCollision + ", minConfusion=" + minConfusion + ", sourceId=" + sourceId + ", callbackUrl="
                + callbackUrl + ", pciOptimizer=" + pciOptimizer + ", pciAnrOptimizer=" + pciAnrOptimizer
                + ", numSolutions=" + numSolutions + ", bufferTime=" + bufferTime + ", maximumClusters="
                + maximumClusters + ", aafUsername=" + aafUsername + ", aafPassword=" + aafPassword
                + ", streamsSubscribes=" + streamsSubscribes + ", streamsPublishes=" + streamsPublishes
                + ", badThreshold=" + badThreshold + ", poorThreshold=" + poorThreshold + ", poorCountThreshold="
                + poorCountThreshold + ", badCountThreshold=" + badCountThreshold + ", oofTriggerCountTimer="
                + oofTriggerCountTimer + ", oofTriggerCountThreshold=" + oofTriggerCountThreshold + ", policyRespTimer="
                + policyRespTimer + ", policyNegativeAckThreshold=" + policyNegativeAckThreshold + ", policyFixedPciTimeInterval="+ policyFixedPciTimeInterval + ", nfNamingCode="+nfNamingCode+"]";
    }

    /**
     * updates application configuration.
     */
    public void updateConfigurationFromJsonObject(JsonObject jsonObject) {

        log.info("Updating configuration from CBS");

        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();


        JsonObject subscribes = jsonObject.getAsJsonObject("streams_subscribes");
        streamsSubscribes = new Gson().fromJson(subscribes, mapType);

        JsonObject publishes = jsonObject.getAsJsonObject("streams_publishes");
        streamsPublishes = new Gson().fromJson(publishes, mapType);

        CpsUsername = jsonObject.get("cps.username").getAsString();
        CpsPassword = jsonObject.get("cps.password").getAsString();
        pgPort = jsonObject.get("postgres.port").getAsInt();
        pollingInterval = jsonObject.get("sonhandler.pollingInterval").getAsInt();
        pgPassword = jsonObject.get("postgres.password").getAsString();
        numSolutions = jsonObject.get("sonhandler.numSolutions").getAsInt();
        minConfusion = jsonObject.get("sonhandler.minConfusion").getAsInt();
        maximumClusters = jsonObject.get("sonhandler.maximumClusters").getAsInt();
        minCollision = jsonObject.get("sonhandler.minCollision").getAsInt();
        sourceId = jsonObject.get("sonhandler.sourceId").getAsString();
        pgUsername = jsonObject.get("postgres.username").getAsString();
        pgHost = jsonObject.get("postgres.host").getAsString();

        JsonArray servers = jsonObject.getAsJsonArray("sonhandler.dmaap.server");
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        dmaapServers = new Gson().fromJson(servers, listType);

        cg = jsonObject.get("sonhandler.cg").getAsString();
        bufferTime = jsonObject.get("sonhandler.bufferTime").getAsInt();
        cid = jsonObject.get("sonhandler.cid").getAsString();
        configDbService = jsonObject.get("sonhandler.configDb.service").getAsString();
        String namespace = jsonObject.get("sonhandler.namespace").getAsString();
        callbackUrl = "http://" + System.getenv("HOSTNAME") + "." + namespace + ":8080/callbackUrl";

        pciOptimizer = jsonObject.get("sonhandler.pciOptimizer").getAsString();
        pciAnrOptimizer = jsonObject.get("sonhandler.pciAnrOptimizer").getAsString();

        oofService = jsonObject.get("sonhandler.oof.service").getAsString();
        oofEndpoint = jsonObject.get("sonhandler.oof.endpoint").getAsString();
        pollingTimeout = jsonObject.get("sonhandler.pollingTimeout").getAsInt();

        badThreshold = jsonObject.get("sonhandler.badThreshold").getAsInt();
        poorThreshold = jsonObject.get("sonhandler.poorThreshold").getAsInt();

        poorCountThreshold = jsonObject.get("sonhandler.poorCountThreshold").getAsInt();
        badCountThreshold = jsonObject.get("sonhandler.badCountThreshold").getAsInt();
        oofTriggerCountTimer = jsonObject.get("sonhandler.oofTriggerCountTimer").getAsInt();
        oofTriggerCountThreshold = jsonObject.get("sonhandler.oofTriggerCountThreshold").getAsInt();
        policyRespTimer = jsonObject.get("sonhandler.policyRespTimer").getAsInt();
        policyNegativeAckThreshold = jsonObject.get("sonhandler.policyNegativeAckThreshold").getAsInt();
        policyFixedPciTimeInterval = jsonObject.get("sonhandler.policyFixedPciTimeInterval").getAsLong();
        nfNamingCode = jsonObject.get("sonhandler.nfNamingCode").getAsString();
        cpsServiceUrl = jsonObject.get("cps.service.url").getAsString();
        getCellDataUrl = jsonObject.get("cps.get.celldata").getAsString();
        getPnfUrl = jsonObject.get("cps.get.pnf.url").getAsString();
        getPciUrl = jsonObject.get("cps.get.pci.url").getAsString();
        ConfigClientType = jsonObject.get("sonhandler.clientType").getAsString();

        log.info("configuration from CBS {}", this);

    }



}
