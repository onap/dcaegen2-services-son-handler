

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
 *******************************************************************************/package org.onap.dcaegen2.services.sonhms.utils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({ BeanUtil.class })
@SpringBootTest(classes = SonHandlerRestTemplate.class)
public class SonHandlerRestTemplateTest {

	@Mock
	RestTemplate restTemplatemock;
	
	@InjectMocks
	SonHandlerRestTemplate sonHandlerRestTemplate; 
	
	@SuppressWarnings({ "static-access", "unchecked", "rawtypes" })
	@Test
	public void sendPostRequestTest() { 

       PowerMockito.mockStatic(BeanUtil.class);
       PowerMockito.when(BeanUtil.getBean(RestTemplate.class)).thenReturn(restTemplatemock);
       ParameterizedTypeReference<String> responseType = null;
       HttpHeaders headers = new HttpHeaders();
       headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
       headers.setContentType(MediaType.APPLICATION_JSON);
       String requestUrl = "Url"; String requestBody = null;  
       HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
       when(restTemplatemock.exchange(requestUrl, HttpMethod.POST,requestEntity,responseType)).thenReturn(new ResponseEntity(HttpStatus.OK)); 
       ResponseEntity<String> resp = sonHandlerRestTemplate.sendPostRequest(requestUrl,requestBody,responseType);
       assertEquals(resp.getStatusCode(), HttpStatus.OK);  

	}
	
	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	@Test
	public void sendPostRequestTest2() { 

       ParameterizedTypeReference<String> responseType = null;
       HttpHeaders headers = new HttpHeaders();
       headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
       headers.setContentType(MediaType.APPLICATION_JSON);
       String requestUrl = "Url"; String requestBody = null;  
       HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
       when(restTemplatemock.exchange(requestUrl, HttpMethod.POST,requestEntity,responseType)).thenReturn(new ResponseEntity(HttpStatus.NOT_FOUND));        
       ResponseEntity<String> resp = sonHandlerRestTemplate.sendPostRequest(requestUrl,requestBody,responseType);
       assertEquals(resp.getStatusCode(), HttpStatus.NOT_FOUND);  

	}  
	
	@SuppressWarnings({ "static-access", "unchecked", "rawtypes" })
	@Test
	public void sendGetRequest() { 
		
		    HttpHeaders headers = new HttpHeaders();
	        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	        HttpEntity<Void> requestEntity = new HttpEntity<>(headers); 
	        ParameterizedTypeReference<String> responseType = null;
	        String requestUrl = "Url";  
	        PowerMockito.mockStatic(BeanUtil.class);
	        PowerMockito.when(BeanUtil.getBean(RestTemplate.class)).thenReturn(restTemplatemock);
	        when(restTemplatemock.exchange(requestUrl, HttpMethod.GET,requestEntity,responseType)).thenReturn(new ResponseEntity(HttpStatus.OK));
	        ResponseEntity<String> resp = sonHandlerRestTemplate.sendGetRequest(requestUrl,responseType);	     
	        assertEquals(resp.getStatusCode(), HttpStatus.OK); 
		
	}

	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	@Test
	public void sendGetRequest2() {     
		
		    HttpHeaders headers = new HttpHeaders();
	        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.add("Authorization", "Basic SW5mcmFQb3J0YWxDbGllbnQ6cGFzc3dvcmQxJA==");
	        String requestBody = "RequestBody"; 
	        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
	        ParameterizedTypeReference<String> responseType = null;
	        String requestUrl = "Url";  
	        PowerMockito.mockStatic(BeanUtil.class);
	        PowerMockito.when(BeanUtil.getBean(RestTemplate.class)).thenReturn(restTemplatemock);
	        when(restTemplatemock.exchange(requestUrl, HttpMethod.POST,requestEntity,responseType)).thenReturn(new ResponseEntity(HttpStatus.OK));
	        ResponseEntity<String> resp = sonHandlerRestTemplate.sendGetRequest(requestUrl,requestBody,responseType);
	        assertEquals(resp.getStatusCode(), HttpStatus.OK); 
	        
	}
	  
	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	@Test
	public void sendPostRequestToOof() { 
		
	    HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic cGNpX3Rlc3Q6cGNpX3Rlc3Rwd2Q=");  
        String requestUrl = null; String requestBody = null;  
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers); 
        ParameterizedTypeReference<String> responseType = null;
	    PowerMockito.mockStatic(BeanUtil.class);
	    PowerMockito.when(BeanUtil.getBean(RestTemplate.class)).thenReturn(restTemplatemock);
	    when(restTemplatemock.exchange(requestUrl, HttpMethod.POST,requestEntity,responseType)).thenReturn(new ResponseEntity(HttpStatus.NOT_FOUND));
	    ResponseEntity<String> resp = sonHandlerRestTemplate.sendPostRequestToOof(requestUrl,requestBody,responseType);
	    assertEquals(resp.getStatusCode(), HttpStatus.NOT_FOUND); 
          
	}
}
