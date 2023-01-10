/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  son-handler
 *  ================================================================================
 *   Copyright (C) 2019 Wipro Limited.
 *   Copyright (C) 2022 Wipro Limited.
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TopicTest {
    
    @Test
    public void topicTest() {
        Topic topic=new Topic();
        topic.setConsumer("consumer");
        topic.setName("name");
        topic.setProducer("producer");
        assertEquals("consumer", topic.getConsumer());
        assertEquals("name", topic.getName());
        assertEquals("producer", topic.getProducer());
        String name="name";
        String producer="producer";
        String consumer="consumer";
        assertEquals("topic [name=" + name + ", producer=" + producer + ", consumer=" + consumer + "]",topic.toString());
        Topic topic2=new Topic(name, producer, consumer);
        assertEquals("consumer", topic2.getConsumer());
        assertEquals("name", topic2.getName());
        assertEquals("producer", topic2.getProducer());
    }
}
