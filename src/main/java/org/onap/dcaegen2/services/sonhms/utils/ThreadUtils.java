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

package org.onap.dcaegen2.services.sonhms.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import org.onap.dcaegen2.services.sonhms.EventHandler;
import org.onap.dcaegen2.services.sonhms.HoMetricsComponent;
import org.onap.dcaegen2.services.sonhms.child.ChildThread;
import org.onap.dcaegen2.services.sonhms.child.Graph;
import org.onap.dcaegen2.services.sonhms.model.CellPciPair;
import org.onap.dcaegen2.services.sonhms.model.ThreadId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ThreadUtils {
    
    private static Logger log = LoggerFactory.getLogger(ThreadUtils.class);
    
    /**
     * Create thread.
     */
    public Boolean createNewThread(List<Graph> newClusters, BlockingQueue<List<String>> childStatusQueue,
            ExecutorService pool, EventHandler eventHandler,String cellId) {

		if (newClusters.isEmpty()) {

			BlockingQueue<Map<CellPciPair, ArrayList<CellPciPair>>> queue = new LinkedBlockingQueue<>();
			ThreadId threadId = new ThreadId();
			threadId.setChildThreadId(0);
			ChildThread child = new ChildThread(childStatusQueue, new Graph(), queue, threadId,
					new HoMetricsComponent());
			log.info("Creating new child thread");
			pool.execute(child);
			waitForThreadId(threadId);
			EventHandler.addChildThreadMap(threadId.getChildThreadId(), child);
			eventHandler.addChildStatus(threadId.getChildThreadId(), "processingNotifications");
		}

        for (Graph cluster : newClusters) {

            BlockingQueue<Map<CellPciPair, ArrayList<CellPciPair>>> queue = new LinkedBlockingQueue<>();
            ThreadId threadId = new ThreadId();
            threadId.setChildThreadId(0);
            ChildThread child = new ChildThread(childStatusQueue, cluster, queue, threadId, new HoMetricsComponent());
            
            log.info("Creating new child thread");
            pool.execute(child);
            waitForThreadId(threadId);
            UUID clusterId = UUID.randomUUID();

            ClusterUtils clusterUtils = new ClusterUtils();
            clusterUtils.saveCluster(cluster, clusterId, threadId.getChildThreadId());
            EventHandler.addChildThreadMap(threadId.getChildThreadId(), child);
            eventHandler.addChildStatus(threadId.getChildThreadId(), "processingNotifications");
        }
        return true;
        
    }
    
    private void waitForThreadId(ThreadId threadId) {

        ThreadId thread = threadId;
        try {
            synchronized (thread) {
                while (thread.getChildThreadId() == 0) {
                    thread.wait();
                }
            }
        } catch (InterruptedException e) {

            log.error("ChildThread queue error {}", e); 
            Thread.currentThread().interrupt();
        }
    }

}