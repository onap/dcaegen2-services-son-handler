package com.wipro.www.sonhms.restclient;

import static org.junit.Assert.assertEquals;

import com.wipro.www.sonhms.restclient.Solution;
import com.wipro.www.sonhms.restclient.SonSolution;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;



public class SolutionsTest {

    @Test
    public void solutionsTest() {

        SonSolution pciSolutions = new SonSolution();
        pciSolutions.setCellId("EXP001");
        pciSolutions.setPci(101);
        List<SonSolution> pciSolutionsList = new ArrayList<SonSolution>();
        pciSolutionsList.add(pciSolutions);
        Solution solutions = new Solution();
        solutions.setFinishTime("2018-10-01T00:40+01.00");
        solutions.setNetworkId("EXP001");
        solutions.setPciSolutions(pciSolutionsList);
        solutions.setStartTime("2018-10-01T00:30+01:00");
        assertEquals("2018-10-01T00:40+01.00", solutions.getFinishTime());
        assertEquals("EXP001", solutions.getNetworkId());
        assertEquals(pciSolutionsList, solutions.getPciSolutions());
        assertEquals("2018-10-01T00:30+01:00", solutions.getStartTime());

    }

}
