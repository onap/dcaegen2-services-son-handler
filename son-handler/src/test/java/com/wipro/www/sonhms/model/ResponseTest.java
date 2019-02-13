package com.wipro.www.sonhms.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ResponseTest {

    @Test
    public void respomseTest() {
        Response response = new Response();
        response.setCellId("cellId");
        response.setPci(1);
        assertEquals("cellId", response.getCellId());
        assertEquals(1, response.getPci());
    }
            
}
