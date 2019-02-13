package com.wipro.www.sonhms.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CellConfigTest {

    @Test
    public void cellConfigTest() {
               
        Common common = new Common();
        common.setCellIdentity("cellIdentity");
        Ran ran = new Ran();
        ran.setCommon(common);
        Lte lte = new Lte();
        lte.setRan(ran);
        CellConfig cellConfig = new CellConfig();
        cellConfig.setLte(lte);
        assertEquals(lte, cellConfig.getLte());
        assertEquals(ran, lte.getRan());
        assertEquals(common, ran.getCommon());
    }
}
