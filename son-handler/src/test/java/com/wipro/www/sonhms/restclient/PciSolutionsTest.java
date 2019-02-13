package com.wipro.www.sonhms.restclient;

import static org.junit.Assert.assertEquals;

import com.wipro.www.sonhms.restclient.SonSolution;

import org.junit.Test;


public class PciSolutionsTest {
    @Test
    public void pciSolutionsTest() {
        SonSolution pciSolutions = new SonSolution();
        pciSolutions.setCellId("EXP001");
        pciSolutions.setPci(101);
        assertEquals("EXP001", pciSolutions.getCellId());
        assertEquals(101, pciSolutions.getPci());
    }

}
