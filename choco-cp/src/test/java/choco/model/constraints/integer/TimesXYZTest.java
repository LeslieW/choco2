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
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

public class TimesXYZTest {
    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    private CPModel m;
    private CPSolver s;
  private IntegerVariable x, y, z;

    @Before
  public void setUp() {
    LOGGER.fine("choco.currentElement.bool.TimesXYZTest Testing...");
    m = new CPModel();
    s = new CPSolver();
  }

    @After
  public void tearDown() {
    y = null;
    x = null;
    z = null;
    m = null;
    s = new CPSolver();
  }

    @Test
  public void test1() {
    LOGGER.finer("test1");
    x = makeIntVar("x", -7, 12);
    y = makeIntVar("y", 3, 5);
    z = makeIntVar("z", 22, 59);
    m.addConstraint(times(x, y, z));
        s.read(m);
    try {
      s.propagate();
      assertEquals(s.getVar(x).getInf(), 5);
      s.getVar(y).setVal(3);
      s.propagate();
      assertEquals(s.getVar(x).getInf(), 8);
      assertEquals(s.getVar(z).getSup(), 36);
      assertEquals(s.getVar(z).getInf(), 24);
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

    @Test
  public void test2() {
    for (int i = 0; i < 10; i++) {
      m = new CPModel();
      s = new CPSolver();
      LOGGER.finer("test2");
      x = makeIntVar("x", 1, 2);
      y = makeIntVar("y", 3, 5);
      z = makeIntVar("z", 3, 10);
      m.addConstraint(times(x, y, z));
      s.setVarIntSelector(new RandomIntVarSelector(s, i));
      s.setValIntSelector(new RandomIntValSelector(i + 1));
      s.read(m);
      s.solve();
      do {
//        /LOGGER.info("" + s.getVar(x).getVal() + "*" + s.getVar(y).getVal() + "=" + s.getVar(z).getVal());
      } while (s.nextSolution() == Boolean.TRUE);
      LOGGER.info("Nb solution : " + s.getNbSolutions());
      assertEquals( s.getNbSolutions(), 6);
    }
  }

    @Test
  public void test2b() throws ContradictionException {
    for (int i = 0; i < 10; i++) {
      m = new CPModel();
      s = new CPSolver();
      LOGGER.finer("test2");
      x = makeIntVar("x", 1, 2);
      y = makeIntVar("y", 3, 5);
      z = makeIntVar("z", 3, 10);
        m.addVariables("cp:bound", x, y, z);
      m.addConstraint(times(x, y, z));
      s.setVarIntSelector(new RandomIntVarSelector(s, i));
      s.setValIntSelector(new RandomIntValSelector(i + 1));
        s.read(m);
      s.solve();
      do {
        //LOGGER.info("" + x.getVal() + "*" + y.getVal() + "=" +
        //    z.getVal());
        assertEquals(s.getVar(x).getVal() * s.getVar(y).getVal(), s.getVar(z).getVal());
      } while (s.nextSolution() == Boolean.TRUE);
      //LOGGER.info("Nb solution : " + s.getNbSolutions());
      assertEquals(s.getNbSolutions(), 6);
    }
  }

    @Test
  public void test3() {
    for (int i = 0; i < 10; i++) {
      LOGGER.info("test3-" + i);
      try {
          m=new CPModel();
          s= new CPSolver();

        IntegerVariable x = makeIntVar("x", -10, 10);
        IntegerVariable y = makeIntVar("y", -10, 10);
        IntegerVariable z = makeIntVar("z", -20, 20);
        //IntVar oneFourFour = pb.makeConstantIntVar(144);

        m.addConstraint(times(x, y, z));
        //m.addConstraint(pb.neq(z, oneFourFour));

        s.setVarIntSelector(new RandomIntVarSelector(s, i));
        s.setValIntSelector(new RandomIntValSelector(i + 1));
          s.read(m);
        s.solve();
        do {
          //LOGGER.info("" + x.getVal() + "*" + y.getVal() + "=" + z.getVal());
          assertEquals(s.getVar(x).getVal() * s.getVar(y).getVal(), s.getVar(z).getVal());
        } while (s.nextSolution() == Boolean.TRUE);
        assertEquals(225, s.getNbSolutions()); // with 10,10,20
        //assertEquals(3993, s.getNbSolutions()); // with 100,100,200
        //LOGGER.info("Nb solution : " + s.getNbSolutions());
      } catch (Exception e) {
        e.printStackTrace();
        assertTrue(false);
      }
    }
  }

    @Test
    public void test3b() {
    for (int i = 0; i < 10; i++) {
      LOGGER.info("test3-" + i);
      try {
         m=new CPModel();
          s= new CPSolver();
        IntegerVariable x = makeIntVar("x", -10, 10);
        IntegerVariable y = makeIntVar("y", -10, 10);
        IntegerVariable z = makeIntVar("z", -20, 20);
        IntegerConstantVariable oneFourFour = constant("c", 14);
        //IntVar oneFourFour = pb.makeConstantIntVar(144);

        m.addConstraint(times(x, y, z));
        m.addConstraint(neq(z, oneFourFour));

        s.setVarIntSelector(new RandomIntVarSelector(s, i));
        s.setValIntSelector(new RandomIntValSelector(i + 1));
        s.read(m);
        s.solve();
        do {
          //LOGGER.info("" + x.getVal() + "*" + y.getVal() + "=" + z.getVal());
          assertEquals(s.getVar(x).getVal() * s.getVar(y).getVal(), s.getVar(z).getVal());
        } while (s.nextSolution() == Boolean.TRUE);
        assertEquals(221, s.getNbSolutions()); // with 10,10,20,14
        //assertEquals(3967, s.getNbSolutions()); // with 100,100,200,144
        //LOGGER.info("Nb solution : " + s.getNbSolutions());
      } catch (Exception e) {
        e.printStackTrace();
        assertTrue(false);
      }
    }
  }

    @Test
  public void test3c() {
    for (int i = 0; i < 10; i++) {
      LOGGER.info("test3-" + i);
      try {
        m=new CPModel();
          s= new CPSolver();

        IntegerVariable x = makeIntVar("x", -10, 10);
        IntegerVariable y = makeIntVar("y", -10, 10);
        IntegerVariable z = makeIntVar("z", -20, 20);
          m.addVariables("cp:bound", x, y, z);

        m.addConstraint(times(x, y, z));

        s.setVarIntSelector(new RandomIntVarSelector(s, i));
        s.setValIntSelector(new RandomIntValSelector(i + 1));
          s.read(m);
        s.solve();
        do {
          //LOGGER.info("" + x.getVal() + "*" + y.getVal() + "=" + z.getVal());
          assertEquals(s.getVar(x).getVal() * s.getVar(y).getVal(), s.getVar(z).getVal());
        } while (s.nextSolution() == Boolean.TRUE);
        assertEquals(225, s.getNbSolutions()); // with 10,10,20
        //assertEquals(3993, s.getNbSolutions()); // with 100,100,200
        //LOGGER.info("Nb solution : " + s.getNbSolutions());
      } catch (Exception e) {
        e.printStackTrace();
        assertTrue(false);
      }
    }
  }

}
