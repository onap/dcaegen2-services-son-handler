/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2019-2020 Wipro Limited.
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.onap.dcaegen2.services.sonhms.child.ChildThread;
import org.onap.dcaegen2.services.sonhms.child.Graph;
import org.onap.dcaegen2.services.sonhms.dao.FixedPciCellsRepository;
import org.onap.dcaegen2.services.sonhms.utils.BeanUtil;
import org.onap.dcaegen2.services.sonhms.utils.ClusterUtils;
import org.onap.dcaegen2.services.sonhms.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MainThreadComponent {

    private static Logger log = LoggerFactory.getLogger(MainThreadComponent.class);

    @Autowired
    private NewSdnrNotification newNotification;
    
    @Autowired
    private NewPmNotification newPmNotification;
    
    @Autowired
    private NewFmNotification newFmNotification;
    
    /**
     * main thread initialization.
     */
    @PostConstruct
    public void init() {
        log.debug("initializing main thread");
        Thread thread = new Thread(new MainThread(newNotification, newFmNotification));
        thread.start();
        Thread pmThread = new Thread(new PmThread(newPmNotification));
        pmThread.start();
    }

	@Scheduled(fixedRate = 300000, initialDelay = 60000)
	public void checkFixedPciTable() {
		log.info("Inside checkFixedPciTable method");
		FixedPciCellsRepository fixedPciCellsRepository = BeanUtil.getBean(FixedPciCellsRepository.class);
		List<String> fixedPciCellsList = fixedPciCellsRepository.getFixedPciCells();
		if (!fixedPciCellsList.isEmpty()) {
			log.info("Fixed Pci table not empty");
			Timestamp lastInvokedOofTimeStamp = ChildThread.getLastInvokedOofTimeStamp();
			Timestamp fixedPciCreatedAt = fixedPciCellsRepository.getTimeStampforFixedPci();
			Long difference = fixedPciCreatedAt.getTime() - lastInvokedOofTimeStamp.getTime();
			Configuration configuration = Configuration.getInstance();

			if (Math.abs(difference) > configuration.getPolicyFixedPciTimeInterval()) {
				log.info("Creating new child thread for sending fixedPciCells");
				List<Graph> cluster = new ArrayList<>();
				BlockingQueue<List<String>> childStatusQueue = new LinkedBlockingQueue<>();
				EventHandler eventHandler = new EventHandler(childStatusQueue,
						Executors.newFixedThreadPool(Configuration.getInstance().getMaximumClusters()), new HashMap<>(),
						new ClusterUtils(), new ThreadUtils());
				ExecutorService pool = Executors.newFixedThreadPool(5);
				ThreadUtils threadUtils = new ThreadUtils();
				boolean result = threadUtils.createNewThread(cluster, childStatusQueue, pool, eventHandler, null);
				log.info("Child Thread creation result:"+ result);
			}

		} else {
			log.info("Exiting function fixedPci table empty");
		}
	}
}
