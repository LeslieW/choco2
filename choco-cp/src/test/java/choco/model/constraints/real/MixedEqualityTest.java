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
package choco.model.constraints.real;

import static choco.Choco.makeIntVar;
import static choco.Choco.makeRealVar;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.real.MixedEqXY;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.real.RealIntervalConstant;
import junit.framework.TestCase;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 18 juin 2004
 */
public class MixedEqualityTest extends TestCase {
  CPModel m;
    CPSolver s;
  RealVariable v1;
  IntegerVariable v2;

  public void setUp() {
    m = new CPModel();
    s = new CPSolver();
    v1 = makeRealVar("v1", 0.0, 8.0);
    v2 = makeIntVar("v2", 2, 10);
      m.addVariables(v1, v2);
      s.read(m);
    s.post(new MixedEqXY(s.getVar(v1), s.getVar(v2)));
  }

  public void tearDown() {
    m = null;
      s = null;
    v1 = null;
    v2 = null;
  }

  public void testInt2Real() {
    try {
      s.propagate();
      assertEquals(2.0, s.getVar(v1).getInf(), 1e-10);
      s.getVar(v2).setSup(6);
      s.propagate();
      assertEquals(6.0, s.getVar(v1).getSup(), 1e-10);
    } catch (ContradictionException e) {
      assertTrue("The model is consistent !", false);
    }
  }

  public void testReal2Int() {
    try {
      s.propagate();
      assertEquals(8, s.getVar(v2).getSup());
      s.getVar(v1).intersect(new RealIntervalConstant(4.0, Double.POSITIVE_INFINITY));
      s.propagate();
      assertEquals(4, s.getVar(v2).getInf());
    } catch (ContradictionException e) {
      assertTrue("The model is consistent !", false);
    }
  }
}
