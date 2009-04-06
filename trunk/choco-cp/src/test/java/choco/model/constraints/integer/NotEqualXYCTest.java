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

/* File choco.currentElement.search.NotEqualXYCTest.java, last modified by Francois 21 sept. 2003 10:59:44 */

package choco.model.constraints.integer;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.NotEqualXYC;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

public class NotEqualXYCTest {
  private static Logger logger = Logger.getLogger("choco.currentElement");
  private CPModel m;
  private CPSolver s;
  private IntegerVariable x;
  private IntegerVariable y;
  private IntegerVariable z;
  private SConstraint c1;
  private SConstraint c2;

    @Before
  public void setUp() {
    logger.fine("NotEqualXYC Testing...");
    m = new CPModel();
        s = new CPSolver();
    x = makeIntVar("X", 1, 5);
    y = makeIntVar("Y", 1, 5);
    z = makeIntVar("Z", 1, 5);
        m.addVariables(x, y, z);
        s.read(m);
    c1 = new NotEqualXYC(s.getVar(x), s.getVar(y), -2);
    c2 = new NotEqualXYC(s.getVar(y), s.getVar(z), 1);
  }

    @After
  public void tearDown() {
    c1 = null;
    c2 = null;
    x = null;
    y = null;
    z = null;
    s = null;
        m=null;
  }

    @Test
  public void test1() {
    logger.finer("test1");
    try {
      s.post(c1);
      s.getVar(x).setSup(2);
      s.getVar(y).setVal(3);
      s.propagate();
      assertFalse(s.getVar(x).canBeInstantiatedTo(1));
      assertTrue(s.getVar(x).isInstantiatedTo(2));
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

    @Test
  public void test2() {
    logger.finer("test2");
    try {
      s.post(c1);
      s.post(c2);
      s.getVar(y).setVal(3);
      s.propagate();
      assertFalse(s.getVar(x).canBeInstantiatedTo(1));
      assertFalse(s.getVar(z).canBeInstantiatedTo(2));
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

	public static void main(String[] args) {
		CPModel mod = new CPModel();
		int n = 11;
		IntegerVariable[] vs = makeIntVarArray("yo", n, 0, n-2);
		for (int i = 0; i < vs.length; i++) {
			for (int j = i + 1; j < vs.length; j++) {
				mod.addConstraint(neq(vs[i], vs[j]));
			}
		}
		CPSolver s = new CPSolver();
		s.read(mod);
		s.setBackTrackLimit(Integer.MAX_VALUE);
		s.solve();
		s.printRuntimeSatistics();
	}

}
