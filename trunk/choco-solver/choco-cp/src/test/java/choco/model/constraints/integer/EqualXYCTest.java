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
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.EqualXYC;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;
/**
 * a class implementing tests for backtrackable search
 */
public class EqualXYCTest{
  private Logger logger = ChocoLogging.getTestLogger();
  private CPModel m;
  private CPSolver s;
  private IntegerVariable x;
  private IntegerVariable y;
  private IntegerVariable z;
  private SConstraint c1;
  private SConstraint c2;

    @Before
  public void setUp() {
    logger.fine("EqualXYC Testing...");
    m = new CPModel();
    s = new CPSolver();
    x = makeIntVar("X", 1, 5);
    y = makeIntVar("Y", 1, 5);
    z = makeIntVar("Z", 1, 5);
        m.addVariables(CPOptions.V_BOUND, x, y, z);
    m.addVariables(x, y, z);
    s.read(m);
    c1 = new EqualXYC(s.getVar(x), s.getVar(y), 2);
    c2 = new EqualXYC(s.getVar(y), s.getVar(z), 1);
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
    s.post(c1);
    s.post(c2);

    try {
      s.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }
    logger.finest("X : " + s.getVar(x).getInf() + " - > " + s.getVar(x).getSup());
    logger.finest("Y : " + s.getVar(y).getInf() + " - > " + s.getVar(y).getSup());
    logger.finest("Z : " + s.getVar(z).getInf() + " - > " + s.getVar(z).getSup());
    assertEquals(4, s.getVar(x).getInf());
    assertEquals(5, s.getVar(x).getSup());
    assertEquals(2, s.getVar(y).getInf());
    assertEquals(3, s.getVar(y).getSup());
    assertEquals(1, s.getVar(z).getInf());
    assertEquals(2, s.getVar(z).getSup());
    logger.finest("domains OK after first propagate");

    try {
      s.getVar(z).setInf(2);
      s.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }

    assertTrue(s.getVar(x).isInstantiated());
    assertTrue(s.getVar(y).isInstantiated());
    assertTrue(s.getVar(z).isInstantiated());
    assertEquals(5, s.getVar(x).getVal());
    assertEquals(3, s.getVar(y).getVal());
    assertEquals(2, s.getVar(z).getVal());
  }

}