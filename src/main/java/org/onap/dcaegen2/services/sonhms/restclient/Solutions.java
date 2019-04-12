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

package org.onap.dcaegen2.services.sonhms.restclient;

import java.util.ArrayList;
import java.util.List;

public class Solutions {

    String networkId = null;
    List<PciSolutions> pciSolutions = new ArrayList<>();
    List<AnrSolutions> anrSolutions = new ArrayList<>();

    public Solutions() {

    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public List<PciSolutions> getPciSolutions() {
        return pciSolutions;
    }

    /**
     * Sets PciSolutions.
     */
    public void setPciSolutions(List<PciSolutions> pciSolutions) {

        this.pciSolutions = pciSolutions;

    }

    public List<AnrSolutions> getAnrSolutions() {
        return anrSolutions;
    }

    public void setAnrSolutions(List<AnrSolutions> anrSolutions) {
        this.anrSolutions = anrSolutions;
    }

    @Override
    public String toString() {
        return "Solutions [networkId=" + networkId + ", pciSolutions=" + pciSolutions + ", anrSolutions=" + anrSolutions
                + "]";
    }

}
