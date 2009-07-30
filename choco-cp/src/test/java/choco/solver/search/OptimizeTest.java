/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
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
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.solver.search;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;
// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

import junit.framework.Assert;

public class OptimizeTest {
	private final static Logger LOGGER = ChocoLogging.getTestLogger();
    private Model m;
    private Solver s;
    private IntegerVariable v1, v2, v3, obj;

    @Before
    public void setUp() {
        LOGGER.fine("StoredInt Testing...");
        m = new CPModel();
        s = new CPSolver();
        obj = makeIntVar("objectif", -10, 1000);
        m.addVariable("cp:bound", obj);
        v1 = makeIntVar("v1", 1, 10);
        v2 = makeIntVar("v2", -3, 10);
        v3 = makeIntVar("v3", 1, 10);
        m.addConstraint(eq(sum(v1, v2, v3), obj));
        s.read(m);
    }

    @After
    public void tearDown() {
        v1 = null;
        v2 = null;
        v3 = null;
        obj = null;
        m = null;
        s = null;
    }

    /**
     * testing b&b search
     */
    @Test
    public void test1() {
        LOGGER.finer("test1");
        assertEquals(Boolean.TRUE, s.maximize(s.getVar(obj), false));
        assertTrue(s.getNbSolutions() == 32);
        assertEquals(s.getOptimumValue().intValue(), 30);
    }

    /**
     * testing search with restarts
     */
    @Test
    public void test2() {
        LOGGER.finer("test2");
        assertEquals(Boolean.TRUE, s.maximize(s.getVar(obj), true));
        assertTrue(s.getNbSolutions() == 32);
        assertEquals(s.getOptimumValue().intValue(), 30);
    }
    
    
  
    @Test
    public void test3() {
        LOGGER.finer("test3");
        s.setNodeLimit(2);
        assertNull("feasible", s.maximize(s.getVar(obj), false));
        assertEquals("solution count", 0, s.getSolutionCount());
        assertNull("objective value", s.getObjectiveValue());
    }
    

}