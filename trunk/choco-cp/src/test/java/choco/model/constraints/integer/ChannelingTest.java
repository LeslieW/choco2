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

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 13 oct. 2005
 * Time: 12:37:51
 */
public class ChannelingTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    CPModel m;
    CPSolver s;

    @Before
    public void before(){
        s = new CPSolver();
        m = new CPModel();
    }
    @After
    public void after(){
        s = null;
        m = null;
    }

    @Test
  public void test1() {
    IntegerVariable y01 = makeIntVar("y01", 0, 1);
    IntegerVariable x1 = makeIntVar("x1", 0, 5);
    Constraint A = boolChanneling(y01, x1, 4);
    m.addConstraint(A);
    s.read(m);
    s.solveAll();
    assertEquals(6, s.getNbSolutions());
  }

    @Test
  public void test2() {
    IntegerVariable x1 = makeIntVar("x1", 0, 5);
    IntegerVariable x2 = makeIntVar("x2", 0, 5);
    IntegerVariable y1 = makeIntVar("y1", 0, 1);
    IntegerVariable y2 = makeIntVar("y2", 0, 1);
    IntegerVariable z = makeIntVar("z", 0, 5);
    m.addConstraint(boolChanneling(y1, x1, 4));
    m.addConstraint(boolChanneling(y2, x2, 1));
    m.addConstraint(eq(plus(y1, y2), z));
    s.read(m);
    s.maximize(s.getVar(z), false);
    LOGGER.info(s.getVar(x1).getVal() + " " + s.getVar(x2).getVal() + " " + s.getVar(z).getVal());
    assertEquals(4, s.getVar(x1).getVal());
    assertEquals(1, s.getVar(x2).getVal());
    assertEquals(2, s.getVar(z).getVal());
  }

    @Test
  public void test3() {
    int n = 5;
    IntegerVariable[] x = new IntegerVariable[n];
    IntegerVariable[] y = new IntegerVariable[n];
    for (int i = 0; i < n; i++) {
      x[i] = makeIntVar("x" + i, 0, n - 1);
      y[i] = makeIntVar("y" + i, 0, n - 1);
    }
    m.addConstraint(inverseChanneling(x, y));
        s.read(m);
        s.solve();
    do {
      for (int i = 0; i < n; i++) {
        //LOGGER.info("" + x[i] + ":" + x[i].getVal() + " <=> " + y[x[i].getVal()] + ":" + y[x[i].getVal()].getVal());
        assertTrue(s.getVar(y[s.getVar(x[i]).getVal()]).getVal() == i);
      }
    } while (s.nextSolution() == Boolean.TRUE);
    assertEquals(120, s.getNbSolutions());
  }

    @Test
  public void test4() {
    int n = 5;
    int lb = 7;
    IntegerVariable[] x = new IntegerVariable[n];
    IntegerVariable[] y = new IntegerVariable[n];
    for (int i = 0; i < n; i++) {
      x[i] = makeIntVar("x" + i, lb, lb + n - 1);
      y[i] = makeIntVar("y" + i, lb, lb + n - 1);
    }
    m.addConstraint(inverseChanneling(x, y));
        s.read(m);
    s.solve();
    do {
      for (int i = 0; i < n; i++) {
        //LOGGER.info("" + x[i] + ":" + x[i].getVal() + " <=> " + y[x[i].getVal() - lb] + ":" + y[x[i].getVal() - lb].getVal());
        assertTrue(s.getVar(y[s.getVar(x[i]).getVal() - lb]).getVal() == (i + lb));
      }
    } while (s.nextSolution() == Boolean.TRUE);
    assertEquals(120, s.getNbSolutions());
  }

}
