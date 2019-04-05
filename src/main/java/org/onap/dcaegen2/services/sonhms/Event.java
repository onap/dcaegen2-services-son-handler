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

public class Event {
	private CommonEventHeader commonEventHeader;
	private FaultFields faultFields;
	public CommonEventHeader getCommonEventHeader() {
		return commonEventHeader;
	}
	public void setCommonEventHeader(CommonEventHeader commonEventHeader) {
		this.commonEventHeader = commonEventHeader;
	}
	public FaultFields getFaultFields() {
		return faultFields;
	}
	public void setFaultFields(FaultFields faultFields) {
		this.faultFields = faultFields;
	}
    @Override
    public String toString() {
        return "Event [commonEventHeader=" + commonEventHeader + ", faultFields=" + faultFields + "]";
    }
	
	
}
