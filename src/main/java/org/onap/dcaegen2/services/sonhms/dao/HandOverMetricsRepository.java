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

package org.onap.dcaegen2.services.sonhms.dao;

import org.onap.dcaegen2.services.sonhms.entity.HandOverMetrics;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface HandOverMetricsRepository extends CrudRepository<HandOverMetrics, String> {

    @Query(nativeQuery = true, value = "SELECT ho_details FROM handover_metrics WHERE src_cell_id=?1")
    public String getHandOverMetrics(String srcCellId);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE handover_metrics SET ho_details=?1 where src_cell_id=?2")
    public void updateHoMetrics(String hoDetails, String srcCellId);
}
