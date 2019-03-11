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

package org.onap.dcaegen2.services.sonhms.child;

import org.onap.dcaegen2.services.sonhms.dao.CellInfoRepository;
import org.onap.dcaegen2.services.sonhms.entity.CellInfo;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.restclient.SdnrRestClient;
import org.onap.dcaegen2.services.sonhms.restclient.Solution;
import org.onap.dcaegen2.services.sonhms.restclient.SonSolution;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;



public class PnfUtils {

    /**
     * get pnfs.
     *
     */
    public Map<String, List<CellPciPair>> getPnfs(List<Solution> solutions) throws ConfigDbNotFoundException {

        Map<String, List<CellPciPair>> pnfs = new HashMap<>(); 

        for (Solution solution : solutions) {
            List<SonSolution> pciSolutions = solution.getPciSolutions();
            for (SonSolution pciSolution : pciSolutions) {
                String cellId = pciSolution.getCellId();
                int pci = pciSolution.getPci();

                String pnfName = "";
                CellInfoRepository cellInfoRepository = BeanUtil.getBean(CellInfoRepository.class);
                Optional<CellInfo> cellInfo = cellInfoRepository.findById(cellId);
                if (cellInfo.isPresent()) {
                    pnfName = cellInfo.get().getPnfName();
                } else {
                    pnfName = SdnrRestClient.getPnfName(cellId);
                    cellInfoRepository.save(new CellInfo(cellId, pnfName));
                }
                if (pnfs.containsKey(pnfName)) {
                    pnfs.get(pnfName).add(new CellPciPair(cellId, pci));
                } else {
                    List<CellPciPair> cellPciPairs = new ArrayList<>();
                    cellPciPairs.add(new CellPciPair(cellId, pci));
                    pnfs.put(pnfName, cellPciPairs);
                }
            }

        }
        return pnfs;
    }
}
