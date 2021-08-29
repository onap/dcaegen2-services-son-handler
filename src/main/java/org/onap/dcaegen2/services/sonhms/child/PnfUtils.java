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

package org.onap.dcaegen2.services.sonhms.child;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.onap.dcaegen2.services.sonhms.Configuration;
import org.onap.dcaegen2.services.sonhms.dao.CellInfoRepository;
import org.onap.dcaegen2.services.sonhms.entity.CellInfo;
import org.onap.dcaegen2.services.sonhms.exceptions.ConfigDbNotFoundException;
import org.onap.dcaegen2.services.sonhms.exceptions.CpsNotFoundException;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.restclient.AnrSolutions;
import org.onap.dcaegen2.services.sonhms.restclient.PciSolutions;
import org.onap.dcaegen2.services.sonhms.restclient.ConfigurationClient;
import org.onap.dcaegen2.services.sonhms.restclient.CpsClient;
import org.onap.dcaegen2.services.sonhms.restclient.SdnrRestClient;
import org.onap.dcaegen2.services.sonhms.restclient.Solutions;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.slf4j.Logger;

public class PnfUtils {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ChildThreadUtils.class);

    /**
     * get pnfs.
     *
     */
    public Map<String, List<CellPciPair>> getPnfs(Solutions solutions) throws ConfigDbNotFoundException, CpsNotFoundException {

        Map<String, List<CellPciPair>> pnfs = new HashMap<>();
        List<PciSolutions> pciSolutions = solutions.getPciSolutions();
        for (PciSolutions pciSolution : pciSolutions) {
            String cellId = pciSolution.getCellId();
            int pci = pciSolution.getPci();

            String pnfName = "";
            CellInfoRepository cellInfoRepository = BeanUtil.getBean(CellInfoRepository.class);
            Optional<CellInfo> cellInfo = cellInfoRepository.findById(cellId);
            if (cellInfo.isPresent()) {
                pnfName = cellInfo.get().getPnfName();
            } else {
                pnfName = ConfigurationClient.configClient(Configuration.getInstance().getConfigClientType()).getPnfName(cellId);
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
        return pnfs;
    }

    /**
     * get pnfs for ANR solutions.
     * 
     */
    public Map<String, List<Map<String, List<String>>>> getPnfsForAnrSolutions(List<AnrSolutions> anrSolutions)
            throws ConfigDbNotFoundException, CpsNotFoundException {

        Map<String, List<Map<String, List<String>>>> anrPnfs = new HashMap<>();

        List<String> removeableNeighbors;
        for (AnrSolutions anrSolution : anrSolutions) {
            String cellId = anrSolution.getCellId();
            String pnfName = ConfigurationClient.configClient(Configuration.getInstance().getConfigClientType()).getPnfName(cellId);
            removeableNeighbors = anrSolution.getRemoveableNeighbors();
            Map<String, List<String>> cellRemNeighborsPair = new HashMap<>();
            cellRemNeighborsPair.put(cellId, removeableNeighbors);
            if (anrPnfs.containsKey(pnfName)) {
                anrPnfs.get(pnfName).add(cellRemNeighborsPair);
            } else {
                List<Map<String, List<String>>> anrCells = new ArrayList<>();
                anrCells.add(cellRemNeighborsPair);
                anrPnfs.put(pnfName, anrCells);
            }
        }
        log.info("anr Pnfs {}", anrPnfs);
        return anrPnfs;

    }
}
