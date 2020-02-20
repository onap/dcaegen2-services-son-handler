/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2020 Wipro Limited.
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

package org.onap.dcaegen2.services.sonhms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PCI_UPDATE")
public class PciUpdate {

	@Id
	@Column(name = "cell_id", columnDefinition = "text")
	private String cellId;

	@Column(name = "old_pci", columnDefinition = "bigint")
	private long oldPci;
	
	@Column(name = "new_pci", columnDefinition = "bigint")
	private long newPci;
	
	@Column(name = "negative_ack_count", columnDefinition = "int")
	private int negativeAckCount;
	
	public PciUpdate() {

	}

	public PciUpdate(String cellId, long oldPci, long newPci, int negativeAckCount) {
		this.cellId = cellId;
		this.oldPci = oldPci;
		this.newPci = newPci;
		this.negativeAckCount = negativeAckCount;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public long getOldPci() {
		return oldPci;
	}

	public void setOldPci(long oldPci) {
		this.oldPci = oldPci;
	}

	public long getNewPci() {
		return newPci;
	}

	public void setNewPci(long newPci) {
		this.newPci = newPci;
	}

	public int getNegativeAckCount() {
		return negativeAckCount;
	}

	public void setNegativeAckCount(int negativeAckCount) {
		this.negativeAckCount = negativeAckCount;
	}	
}
