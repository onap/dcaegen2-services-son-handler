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

package org.onap.dcaegen2.services.sonhms.dao;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.onap.dcaegen2.services.sonhms.entity.FixedPciCells;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
@Transactional
public interface FixedPciCellsRepository extends CrudRepository<FixedPciCells, String> {

	@SuppressWarnings("unchecked")
	public FixedPciCells save(FixedPciCells persisted);

	@Query(nativeQuery = true, value = "SELECT cell_id FROM fixed_pci_cells")
	public List<String> getFixedPciCells();

	@Query(nativeQuery = true,
			value = "SELECT created_at FROM fixed_pci_cells WHERE "
			        + "created_at=(SELECT created_at FROM fixed_pci_cells " 
			        + "ORDER BY created_at DESC FOR UPDATE SKIP LOCKED LIMIT 1);")
	public Timestamp getTimeStampforFixedPci();

}
