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

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name ="FIXED_PCI_CELLS")
public class FixedPciCells {
	
	@Id
	@Column(name = "cell_id", columnDefinition = "text")
	private String cellId;
	
	@Column(name = "fixed_pci", columnDefinition = "bigint")
	private long fixedPci;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "timestamp")
	private Timestamp createdAt;
	
	public FixedPciCells() {
		
	}

	public FixedPciCells(String cellId, long fixedPci, Timestamp createdAt) {
		this.cellId = cellId;
		this.fixedPci = fixedPci;
		this.createdAt = createdAt;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public long getFixedPci() {
		return fixedPci;
	}

	public void setFixedPci(long fixedPci) {
		this.fixedPci = fixedPci;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	
	

	
	
}
