/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _        _                           *
 *         |   (..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package common;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import samples.tutorials.scheduling.PertCPM;
import choco.cp.solver.CPSolver;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 26 avr. 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class TestPrecedences {

	
    private final static Random RANDOM = new Random();

    private void testPertCPM(int horizon, int nbSol) {
    	PertCPM ex = new PertCPM(horizon);
        ex.buildModel();
    	for (int i = 0; i < 5; i++) {
        	ex.buildSolver();
        	CPSolver s = (CPSolver) ex.solver;
        	s.setRandomSelectors(RANDOM.nextLong());
        	Assert.assertEquals(s.solveAll().booleanValue(), nbSol > 0);
        	Assert.assertEquals(s.getSolutionCount(), nbSol);
    	}
    }

    @Test
    public void testPert1() {
    	testPertCPM(17, 0);
    }

    @Test
    public void testPert2() {
    	testPertCPM(18, 154);

    }

    @Test
    public void testPert3() {
    	testPertCPM(19, 1764);
    }


}
