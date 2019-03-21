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

package org.onap.dcaegen2.services.sonhms;

import java.util.List;
import java.util.Map;

public class Configuration {

    private static Configuration instance = null;
    private String pgHost;
    private int pgPort;
    private String pgUsername;
    private String pgPassword;
    private List<String> dmaapServers;
    private String configDbService;
    private String oofService;
    private String cg;
    private String cid;
    private int pollingInterval;
    private int pollingTimeout;
    private int minCollision;
    private int minConfusion;
    private String sourceId;
    private String callbackUrl;
    private List<String> optimizers;
    private int numSolutions;
    private int bufferTime;
    private int maximumClusters; 
    private String aafUsername;
    private String aafPassword;
    private Map<String,Object> streamsSubscribes;
    private Map<String,Object> streamsPublishes;
    
    public boolean isSecured() {
		if(aafUsername.equals("")||aafUsername==null){
			return false;
		}
		else 
			return true;
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

    public List<String> getOptimizers() {
        return optimizers;
    }

    public void setOptimizers(List<String> optimizers) {
        this.optimizers = optimizers;
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



	@Override
	public String toString() {
		return "Configuration [pgHost=" + pgHost + ", pgPort=" + pgPort + ", pgUsername=" + pgUsername + ", pgPassword="
				+ pgPassword + ", dmaapServers=" + dmaapServers + ", configDbService=" + configDbService + ", oofService="
				+ oofService + ", cg=" + cg + ", cid=" + cid + ", pollingInterval=" + pollingInterval
				+ ", pollingTimeout=" + pollingTimeout + ", minCollision=" + minCollision + ", minConfusion="
				+ minConfusion + ", sourceId=" + sourceId + ", callbackUrl=" + callbackUrl + ", optimizers="
				+ optimizers + ", numSolutions=" + numSolutions + ", bufferTime=" + bufferTime + ", maximumClusters="
				+ maximumClusters + ", aafUsername=" + aafUsername + ", aafPassword=" + aafPassword
				+ ", streamsSubscribes=" + streamsSubscribes + ", streamsPublishes=" + streamsPublishes + "]";
	}

	


   
}
