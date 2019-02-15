/*******************************************************************************
 * ============LICENSE_START=======================================================
 * pcims
 *  ================================================================================
 *  Copyright (C) 2018 Wipro Limited.
 *  ==============================================================================
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   ============LICENSE_END=========================================================
 ******************************************************************************/

package com.wipro.www.sonhms;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MainThreadComponent {

    private static Logger log = LoggerFactory.getLogger(MainThreadComponent.class);

    private ExecutorService pool;

    public ExecutorService getPool() {
        return pool;
    }

    /**
     * main thread initialization.
     */
    public void init(NewNotification newNotification) {
        log.debug("initializing main thread");
        log.debug("initializing executors");
        Configuration configuration = Configuration.getInstance();
        int maximumClusters = configuration.getMaximumClusters();
        log.debug("pool creating");
        pool = Executors.newFixedThreadPool(maximumClusters);
        log.debug("pool created");
        Thread thread = new Thread(new MainThread(newNotification));
        thread.start();
    }
}
