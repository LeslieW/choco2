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

import static choco.Choco.geq;
import static choco.Choco.makeIntVar;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.Propagator;
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

/* File choco.currentElement.search.LessOrEqualXC.java, last modified by Francois 11 sept. 2003 00:46:25 */

public class GreaterOrEqualCXTest {
  private final static Logger LOGGER = ChocoLogging.getTestLogger();
  private CPModel m;
    private CPSolver s;
  private IntegerVariable x;
  private IntegerVariable y;
  private IntegerVariable z;
  private Constraint c1;
  private Constraint c2;

    @Before
  public void setUp() {
    LOGGER.finer("LessOrEqualXC Testing...");
    m = new CPModel();
        s = new CPSolver();
    x = makeIntVar("X", 1, 5);
    y = makeIntVar("Y", 1, 5);
        m.addVariables(Options.V_BOUND, x, y);
    c1 = geq(1, x);
    c2 = geq(2, y);
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
    LOGGER.finer("test1");
    try {
      m.addConstraints(c1, c2);
        s.read(m);
      s.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }
    assertTrue(s.getVar(x).isInstantiated());
    assertFalse(s.getVar(y).isInstantiated());
    assertEquals(1, s.getVar(x).getSup());
    assertEquals(2, s.getVar(y).getSup());
    LOGGER.finest("domains OK after first propagate");
    assertTrue(s.getCstr(c1).isSatisfied());
    assertEquals(((Propagator) s.getCstr(c2)).isEntailed(), Boolean.TRUE);
  }
}