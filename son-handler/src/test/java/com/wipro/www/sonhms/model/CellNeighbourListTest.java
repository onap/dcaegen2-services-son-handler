package com.wipro.www.sonhms.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CellNeighbourListTest {

    @Test
    public void cellNeighbourListTest() {
        CellNeighbourList cellNeighbourList = new CellNeighbourList();
        cellNeighbourList.setCellId("cellId");
        cellNeighbourList.setNeighbours("neighbour");
        cellNeighbourList.setPhysicalCellId(1);
        assertEquals("cellId",cellNeighbourList.getCellId() );
        assertEquals("neighbour",cellNeighbourList.getNeighbours() );
        assertEquals(1,cellNeighbourList.getPhysicalCellId() );

    }
            
}
