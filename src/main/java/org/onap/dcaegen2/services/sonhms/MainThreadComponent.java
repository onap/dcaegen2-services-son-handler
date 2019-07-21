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

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
}