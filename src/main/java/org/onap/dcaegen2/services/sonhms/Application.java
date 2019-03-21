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

import javax.sql.DataSource;

import org.onap.dcaegen2.services.sonhms.controller.ConfigFetchFromCbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication  
public class Application {


    private static Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * Main method where the pci context is initially set.
     */
    public static void main(String[] args) {
        
        ConfigFetchFromCbs configFetchFromCbs = new ConfigFetchFromCbs();
        configFetchFromCbs.getAppConfig();
        try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			log.debug("InterruptedException : {}",e);
		}
        log.info("after 10s sleep");
        log.info("Starting spring boot application");
        SpringApplication.run(Application.class);

    }

    /**
     * DataSource bean.
     */
    @Bean
    public DataSource dataSource() {
        Configuration configuration = Configuration.getInstance();
        
        String url = "jdbc:postgresql://" + configuration.getPgHost() + ":" + configuration.getPgPort() + "/sonhms";
        
        return DataSourceBuilder
                .create()
                .url(url)
                .username(configuration.getPgUsername())
                .password(configuration.getPgPassword())
                .build();
    }

}
