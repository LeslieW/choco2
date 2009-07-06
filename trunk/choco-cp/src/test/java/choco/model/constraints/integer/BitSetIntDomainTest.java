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
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.search.BitSetIntDomainTest.java, last modified by Francois 21 sept. 2003 10:59:44 */

package choco.model.constraints.integer;

import static choco.Choco.makeIntVar;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.variables.integer.AbstractIntDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomain;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * a class implementing tests for backtrackable search
 */
public class BitSetIntDomainTest {
    private Logger logger = ChocoLogging.getTestLogger();
    private CPModel m;
    private IntegerVariable x, y;
    private CPSolver s;
    AbstractIntDomain yDom;

    @Before
    public void setUp() {
        logger.fine("BitSetIntDomain Testing...");
        m = new CPModel();
        x = makeIntVar("X", 1, 100);
        m.addVariable("cp:bound", x);
        y = makeIntVar("Y", 1, 15);
        m.addVariables(x, y);
        s = new CPSolver();
        s.read(m);
        yDom = (AbstractIntDomain) s.getVar(y).getDomain();
    }

    @After
    public void tearDown() {
        yDom = null;
        y = null;
        x = null;
        m = null;
        s = null;
    }

    @Test
    public void test1() {
        logger.finer("test1");
        try {
            assertEquals(1, yDom.getInf());
            assertEquals(15, yDom.getSup());
            assertEquals(15, yDom.getSize());
            logger.finest("First step passed");

            s.getEnvironment().worldPush();
            yDom.removeVal(2, -1);
            assertEquals(1, yDom.getInf());
            assertEquals(15, yDom.getSup());
            assertEquals(14, yDom.getSize());
            logger.finest("Second step passed");

            yDom.removeVal(1, -1);
            assertEquals(3, yDom.getInf());
            assertEquals(15, yDom.getSup());
            assertEquals(13, yDom.getSize());
            logger.finest("Third step passed");


            s.worldPop();
            assertEquals(1, yDom.getInf());
            assertEquals(15, yDom.getSup());
            assertEquals(15, yDom.getSize());
            logger.finest("Fourth step passed");

        } catch (ContradictionException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        logger.finer("test2");
        try {

            yDom.removeVal(10,-1);
            yDom.removeVal(12,-1);
            yDom.removeVal(14,-1);
            yDom.removeVal(13,-1);
            yDom.updateSup(14);
            assertEquals(1, yDom.getInf());
            assertEquals(11, yDom.getSup());
            assertEquals(10, yDom.getSize());
            logger.finest("First step passed");

            yDom.updateInf(8);
            assertEquals(8, yDom.getInf());
            assertEquals(11, yDom.getSup());
            assertEquals(3, yDom.getSize());
            logger.finest("Second step passed");

            yDom.removeVal(11, -1);
            assertEquals(8, yDom.getInf());
            assertEquals(9, yDom.getSup());
            assertEquals(2, yDom.getSize());
            logger.finest("Third step passed");
        } catch (ContradictionException e) {
            assertTrue(false);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * testing delta domain management
     */
    @Test
    public void test3() {
        logger.finer("test3");
        Set expectedSet357 = new TreeSet();
        expectedSet357.add(new Integer(3));
        expectedSet357.add(new Integer(5));
        expectedSet357.add(new Integer(7));
        Set expectedSet9 = new TreeSet();
        expectedSet9.add(new Integer(9));

        {
            yDom.freezeDeltaDomain();
            DisposableIntIterator it = yDom.getDeltaIterator();
            assertFalse(it.hasNext());
            assertTrue(yDom.releaseDeltaDomain());
        }
        yDom.remove(3);
        yDom.remove(5);
        yDom.remove(7);
        Set tmp357 = new TreeSet();
        yDom.freezeDeltaDomain();
        yDom.remove(9);
        for (DisposableIntIterator it = yDom.getDeltaIterator(); it.hasNext();) {
            int val = it.next();
            tmp357.add(new Integer(val));
        }
        assertEquals(expectedSet357, tmp357);
        assertFalse(yDom.releaseDeltaDomain());
        yDom.freezeDeltaDomain();
        Set tmp9 = new TreeSet();
        for (DisposableIntIterator it = yDom.getDeltaIterator(); it.hasNext();) {
            int val = it.next();
            tmp9.add(new Integer(val));
        }
        assertEquals(expectedSet9, tmp9);
        assertTrue(yDom.releaseDeltaDomain());
    }

    /**
     * currentElement the restrict method
     */
    @Test
    public void test4() {
        logger.finer("test2");
        try {
        yDom.removeVal(10,-1);
        yDom.removeVal(12,-1);
        yDom.removeVal(14,-1);
        yDom.removeVal(13,-1);
        yDom.updateSup(14);
        yDom.instantiate(7,-1);
        assertEquals(7, yDom.getInf());
        assertEquals(7, yDom.getSup());
        assertEquals(1, yDom.getSize());
        DisposableIntIterator it = yDom.getIterator();
        assertTrue(it.hasNext());
        assertEquals(7, it.next());
        assertFalse(it.hasNext());
        it.dispose();
        } catch (ContradictionException e) {
            assertTrue(false);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Test
    public void testRandomValue() {
        yDom.remove(5);
        yDom.remove(10);
        yDom.remove(12);
        for (int i = 0; i < 100; i++) {
            int val = yDom.getRandomValue();
            assertTrue(yDom.contains(val));
        }

        IntDomain xDom = s.getVar(x).getDomain();
        xDom.updateInf(5);
        xDom.updateSup(10);
        for (int i = 0; i < 100; i++) {
            int val = xDom.getRandomValue();
            assertTrue(xDom.contains(val));
        }
    }
}