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

import static choco.Choco.makeIntVar;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.cp.solver.variables.integer.IntervalIntDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;
// *********************************************
// *                   J-CHOCO                 *
// *   Copyright (c) F. Laburthe, 1999-2003    *
// *********************************************
// * Event-base contraint programming Engine   *
// *********************************************

// CVS Information
// File:               $RCSfile: IntervalIntDomainTest.java,v $
// Version:            $Revision: 1.4 $
// Last Modification:  $Date: 2007/07/16 15:17:33 $
// Last Contributor:   $Author: menana $

public class IntervalIntDomainTest{
	private final static Logger LOGGER = ChocoLogging.getTestLogger();
  private Model m;
  private Solver s;
  private IntegerVariable x, y;
  IntervalIntDomain yDom;

    @Before
  public void setUp() {
    LOGGER.fine("BitSetIntDomain Testing...");
    m = new CPModel();
    x = makeIntVar("X", 1, 100);
    y = makeIntVar("Y", 1, 15);
        m.addVariables(Options.V_BOUND, x, y);
    s = new CPSolver();
    s.read(m);
    yDom = (IntervalIntDomain) s.getVar(y).getDomain();
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
    LOGGER.finer("test1");

    assertEquals(1, yDom.getInf());
    assertEquals(15, yDom.getSup());
    assertEquals(15, yDom.getSize());
    LOGGER.finest("First step passed");

    s.worldPush();
    yDom.updateInf(2);
    yDom.updateInf(3);
    assertEquals(3, yDom.getInf());
    assertEquals(15, yDom.getSup());
    assertEquals(13, yDom.getSize());
    LOGGER.finest("Second step passed");

    s.worldPush();
    yDom.updateSup(13);
    yDom.updateInf(4);
    assertEquals(4, yDom.getInf());
    assertEquals(13, yDom.getSup());
    assertEquals(10, yDom.getSize());
    LOGGER.finest("Third step passed");

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
    LOGGER.finer("test2");

      s.getVar(y).getEvent().addPropagatedEvents(IntVarEvent.BOUNDS_MASK);
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

      /**
   * testing freeze and release for the delta domain
   */
  @Test
  public void test3() {
    LOGGER.finer("test2");

    yDom.freezeDeltaDomain();
    assertTrue(yDom.releaseDeltaDomain());

    yDom.updateInf(2);
    yDom.updateSup(12);
    yDom.freezeDeltaDomain();
    yDom.updateInf(3);
    assertTrue(yDom.releaseDeltaDomain());

    yDom.freezeDeltaDomain();
    assertTrue(yDom.releaseDeltaDomain());
  }
}
