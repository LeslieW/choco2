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
package choco.model.constraints.integer;

import static choco.Choco.makeIntVar;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.GreaterOrEqualXYC;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.Choco;
import org.junit.After;
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

/* File choco.currentElement.search.GreaterOrEqualXYCTest.java, last modified by Francois 11 sept. 2003 00:36:20 */

public class GreaterOrEqualXYCTest {
    private Logger logger = Logger.getLogger("choco.currentElement");
    private CPModel m;
    private CPSolver s;
    private IntegerVariable x;
    private IntegerVariable y;
    private IntegerVariable z;
    private SConstraint c1;
    private SConstraint c2;

    @Before
    public void setUp() {
        logger.fine("GreaterOrEqualXYCTest Testing...");
        m = new CPModel();
        s = new CPSolver();
        x = makeIntVar("X", 1, 5);
        y = makeIntVar("Y", 1, 5);
        z = makeIntVar("Z", 1, 5);
        m.addVariables("cp:bound",x, y, z);
        s.read(m);
        c1 = new GreaterOrEqualXYC(s.getVar(x), s.getVar(y), 1);
        c2 = new GreaterOrEqualXYC(s.getVar(y), s.getVar(z), 2);
    }

    @After
    public void tearDown() {
        c1 = null;
        c2 = null;
        x = null;
        y = null;
        z = null;
        m = null;
        s = null;
    }

    @Test
    public void test1() {
        logger.finer("test1");
        try {
            s.post(c1);
            s.post(c2);
            s.propagate();
            assertFalse(s.getVar(x).isInstantiated());
            assertFalse(s.getVar(y).isInstantiated());
            assertEquals(4, s.getVar(x).getInf());
            assertEquals(3, s.getVar(y).getInf());
            assertEquals(1, s.getVar(z).getInf());
            assertEquals(5, s.getVar(x).getSup());
            assertEquals(4, s.getVar(y).getSup());
            assertEquals(2, s.getVar(z).getSup());
            logger.finest("domains OK after first propagate");
            s.getVar(z).setVal(2);
            s.propagate();
            assertTrue(s.getVar(x).isInstantiated());
            assertTrue(s.getVar(y).isInstantiated());
            assertTrue(s.getVar(z).isInstantiated());
            assertEquals(5, s.getVar(x).getVal());
            assertEquals(4, s.getVar(y).getVal());
            assertEquals(2, s.getVar(z).getVal());
        } catch (ContradictionException e) {
            assertTrue(false);
        }
    }

     @Test
    public void test2() {
        logger.finer("test2");
        CPModel model = new CPModel();
        CPSolver solver = new CPSolver();
        IntegerVariable x = makeIntVar("x",0,20);
        IntegerVariable y = makeIntVar("y",0,20);
        model.addConstraint(Choco.gt(x,y));
        model.addConstraint(Choco.gt(y,x));
        solver.read(model);
        try {
             solver.propagate();
        } catch (ContradictionException e) {
            System.out.println("contradiction");
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }
}
