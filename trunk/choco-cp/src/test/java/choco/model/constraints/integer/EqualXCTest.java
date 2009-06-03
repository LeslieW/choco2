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

import static choco.Choco.eq;
import static choco.Choco.makeIntVar;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * a class implementing tests for backtrackable search
 */
public class EqualXCTest {
	private final static Logger LOGGER = ChocoLogging.getTestLogger();
  private CPModel m;
  private CPSolver s;
  private IntegerVariable x;
  private IntegerVariable y;
  private IntegerVariable z;
  private Constraint c1;
  private Constraint c2;
  private Constraint c3;

    @Before
  public void setUp() {
    LOGGER.fine("EqualXC Testing...");
    m = new CPModel();
        s = new CPSolver();
    x = makeIntVar("X", 1, 5);
    y = makeIntVar("Y", 1, 5);
    m.addVariables("cp:bound", x, y);
    c1 = eq(x, 1);
    c2 = eq(y, 2);
    c3 = eq(y, 3);
  }

    @After
  public void tearDown() {
    c1 = null;
    c2 = null;
    c3 = null;
    x = null;
    y = null;
    z = null;
    m = null;
    s= null;
  }

    @Test
  public void test1() {
    LOGGER.finer("test1");
    try {
      m.addConstraints(c1, c2);
        s.read(m);
      s.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }
    assertTrue(s.getVar(x).isInstantiated());
    assertTrue(s.getVar(y).isInstantiated());
    assertEquals(1, s.getVar(x).getVal());
    assertEquals(2, s.getVar(y).getVal());
    LOGGER.finest("domains OK after first propagate");
        s.addConstraint(c3);
    assertFalse(s.getCstr(c3).isSatisfied());
  }
}
