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
package choco.model.variables.integer;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.variables.integer.IntervalBTreeDomain;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 18 juin 2008
 * Time: 11:36:26
 * To change this template use File | Settings | File Templates.
 */
public class BinaryTreeTest {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Model m;
  private Solver s;
  private IntegerVariable x, y;
  IntervalBTreeDomain yDom;

    @Before
  public void setUp() {
    logger.fine("BitSetIntDomain Testing...");
    m = new CPModel();
    x = Choco.makeIntVar("X", 1, 100);
    y = Choco.makeIntVar("Y", 1, 15);
        m.addVariables("cp:btree", x, y);
    m.addVariables(x, y);
    s = new CPSolver();
    s.read(m);
    yDom = (IntervalBTreeDomain) s.getVar(y).getDomain();
  }

    @After
  public void tearDown() {
    yDom = null;
    x = null;
    y = null;
    m = null;
    s = null;
  }

  /**
   * testing read and write on bounds with backtracking
   */
  @Test
  public void test1() {
    logger.finer("test1");

    assertEquals(1, yDom.getInf());
    assertEquals(15, yDom.getSup());
    assertEquals(15, yDom.getSize());
    logger.finest("First step passed");

    s.worldPush();
    yDom.updateInf(2);
    yDom.updateInf(3);
    assertEquals(3, yDom.getInf());
    assertEquals(15, yDom.getSup());
    assertEquals(13, yDom.getSize());
    logger.finest("Second step passed");

    s.worldPush();
    yDom.updateSup(13);
    yDom.updateInf(4);
    assertEquals(4, yDom.getInf());
    assertEquals(13, yDom.getSup());
    assertEquals(10, yDom.getSize());
    logger.finest("Third step passed");

    s.worldPop();
    assertEquals(3, yDom.getInf());
    assertEquals(15, yDom.getSup());
    assertEquals(13, yDom.getSize());

    s.worldPop();
    assertEquals(1, yDom.getInf());
    assertEquals(15, yDom.getSup());
    assertEquals(15, yDom.getSize());
  }



  /**
   * testing freeze and release for the delta domain
   */
  @Test
  public void test2() {
    logger.finer("test2");

    yDom.freezeDeltaDomain();
    assertTrue(yDom.releaseDeltaDomain());

    yDom.updateInf(2);
    yDom.updateSup(12);
    yDom.freezeDeltaDomain();
    yDom.updateInf(3);
    assertFalse(yDom.releaseDeltaDomain());

    yDom.freezeDeltaDomain();
    assertTrue(yDom.releaseDeltaDomain());
  }

    @Test
    public void test3(){
        for (int i = 1; i < 15; i++) {
            s.worldPush();
            try {
                s.getVar(y).remVal(i);
            } catch (ContradictionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            Assert.assertFalse("y remove "+i, yDom.contains(i));
            Assert.assertEquals("y size inf "+i, yDom.getSize(), 15-i);

        }
        s.worldPush();
        try {
            s.getVar(y).remVal(15);
            Assert.fail();
        } catch (ContradictionException e) {
            s.worldPop();
        }
        for (int i = 15; i > 0; i--) {
            Assert.assertTrue("y add "+i, yDom.contains(i));
            Assert.assertTrue("y size inf "+i, yDom.getSize()==16-i);
            s.worldPop();
        }
        for (int i = 15; i > 0; i--) {
            s.worldPush();
            try {
                s.getVar(y).remVal(15);
            } catch (ContradictionException e) {
                Assert.fail();
            }
        }
        for (int i = 15; i > 0; i--) {
            s.worldPop();
        }

    }

    @Test
    public void test4(){
        y = Choco.makeIntVar("Y", 1, 8);
        m.addVariable("cp:btree", y);
        m.addVariables(x, y);
        s = new CPSolver();
        s.read(m);
        yDom = (IntervalBTreeDomain) s.getVar(y).getDomain();

        s.worldPush();
        try {
            s.getVar(y).remVal(1);
        } catch (ContradictionException e) {
            fail();
        }
        try {
            s.getVar(y).remVal(7);
        } catch (ContradictionException e) {
            fail();
        }
        try {
            s.getVar(y).remVal(2);
        } catch (ContradictionException e) {
            fail();
        }

        s.worldPush();
        try {
            s.getVar(y).remVal(3);
            s.getVar(y).instantiate(8, 0);
        } catch (ContradictionException e) {
            fail();
        }

        s.worldPop();
        assertTrue("yDom not contains 4",yDom.contains(4));
        assertTrue("yDom not contains 5",yDom.contains(5));
        assertTrue("yDom not contains 6",yDom.contains(6));

        s.worldPop();
        assertTrue("yDom not contains 3",yDom.contains(1));
        assertTrue("yDom not contains 4",yDom.contains(7));
        assertTrue("yDom not contains 5",yDom.contains(2));
        assertEquals("yDom bad size", yDom.getSize(), 8);
    }

    @Test
    public void test5(){
        y = Choco.makeIntVar("Y", 0, 12);
        m.addVariable("cp:btree", y);
        m.addVariable(y);
        s = new CPSolver();
        s.read(m);
        yDom = (IntervalBTreeDomain) s.getVar(y).getDomain();

        s.worldPush();
        try{
            s.getVar(y).remVal(7);
            s.getVar(y).remVal(5);

            s.getVar(y).remVal(3);
            s.getVar(y).remVal(1);

            s.getVar(y).remVal(11);
            s.getVar(y).remVal(9);

        }catch(ContradictionException e){
            fail();
        }
        assertTrue("yDom not contains 4",yDom.contains(4));
        assertFalse("yDom contains 7",yDom.contains(7));
        assertTrue("yDom not contains 8",yDom.contains(8));
        assertTrue("yDom not contains 0",yDom.contains(0));
        assertTrue("yDom not contains 12",yDom.contains(12));
        assertTrue("yDom not contains 10",yDom.contains(2));
        assertTrue("yDom not contains 6",yDom.contains(6));
    }
}
