/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2022 Wipro Limited.
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

import com.fasterxml.jackson.annotation.JsonProperty;

public class Input {

    @JsonProperty(value = "near-rt-ric-url")
    private String url;
    
    @JsonProperty("body")
    private ANRPayload anrPayload;

    public Input() {

    }

    /**
     * Parameterized constructor.
     */
    public Input(String url, ANRPayload anrPayload) {
        super();
        this.url = url;
        this.anrPayload = anrPayload;
    }


    public String getUrl() {
        return url;
    }

  
    public void setUrl(String url) {
        this.url = url;
    }

    public ANRPayload getAnrPayload(){
        return anrPayload;
    }

    public void setAnrPayload(ANRPayload anrPayload){
        this.anrPayload = anrPayload;
    }

  @Override
  public String toString() {
      return "Input [url=" + url + ", anrPayload=" + anrPayload + "]";
  }
}

