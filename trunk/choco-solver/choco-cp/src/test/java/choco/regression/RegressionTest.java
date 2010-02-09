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

package choco.regression;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.PropagationEngine;
import choco.kernel.solver.propagation.queue.EventQueue;
import choco.kernel.solver.variables.integer.IntDomainVar;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * @author grochart
 */
public class RegressionTest {
    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

  // 03/01/2006: number of constraints was not correctly maintained !
  // bug reported by R�mi Coletta
    @Test
  public void testNbConstraints() {
    LOGGER.info("Regression currentElement: 03/01/2006 - Remi Coletta");

    CPModel m = new CPModel();
    Solver s = new CPSolver();
    IntegerVariable[] vart = new IntegerVariable[2];
    vart[0] = makeIntVar("x1", 0, 3);
    vart[1] = makeIntVar("x2", 0, 3);
    m.addVariables(vart);
    s.read(m);

    IntDomainVar[] vars = s.getVar(vart) ;
    // On place la contrainte (x1 == x2)
    assertEquals(0, s.getNbIntConstraints());
    s.worldPush();
    SConstraint c1 = s.eq(vars[0], vars[1]);
    s.post(c1);
    assertEquals(1, s.getNbIntConstraints());
    s.solve(true);
    int nbSol1 = s.getNbSolutions();
    assertEquals(4, nbSol1);
    // On supprime maintenant la contrainte...
    s.worldPopUntil(0);
    assertEquals(0, s.getNbIntConstraints());

    // On place la seconde contrainte : (x1 != x2)
    s.worldPush();
    SConstraint c2 = s.neq(vars[0], vars[1]);
    s.post(c2);
    assertEquals(1, s.getNbIntConstraints());
    s.solve(true);
    int nbSol2 = s.getNbSolutions();
    assertEquals(12, nbSol2);
  }

    @Test
  public void testIsFeasible() {
    LOGGER.info("Regression currentElement: 27/01/2006");

    Model m = new CPModel();
    Solver s = new CPSolver();
    IntegerVariable[] vars = new IntegerVariable[4];
    vars[0] = makeIntVar("x1", 0, 2);
    vars[1] = makeIntVar("x2", 0, 2);
    vars[2] = makeIntVar("x3", 0, 2);
    vars[3] = makeIntVar("x4", 0, 2);
    for (int i = 0; i < 4; i++) {
      for (int j = i + 1; j < 4; j++)
        m.addConstraint(neq(vars[i], vars[j]));
    }
        s.read(m);
    s.solve();
    // On place la contrainte (x1 == x2)
    assertEquals(true, s.isFeasible() != null);
    assertEquals(false, s.isFeasible());

  }


    @Test
    public void run(){
        for(int i = 0; i < 10; i++){
            LOGGER.info("i:"+i);
            this.constraintsIterator();
        }
    }

  public void constraintsIterator() {
        LOGGER.info("Regression currentElement: 27/01/2006 - iterator");
    Model m = new CPModel();
    Solver s = new CPSolver();
    IntegerVariable x = makeIntVar("X", 1, 5);
    IntegerVariable y = makeIntVar("Y", 1, 5);
        m.addVariables("cp:bound", x, y);
    Constraint c1 = eq(x, 1);
    Constraint c2 = eq(x, y);
    m.addConstraints(c1,c2);
        s.read(m);
    assertEquals(s.getVar(x).getNbConstraints(), 2);
    assertEquals(s.getVar(y).getNbConstraints(), 1);

    Iterator constraints = s.getVar(x).getConstraintsIterator();
      assertTrue(constraints.hasNext());
      assertEquals(constraints.next(), s.getCstr(c1));
      assertTrue(constraints.hasNext());
      assertEquals(constraints.next(), s.getCstr(c2));
      assertFalse(constraints.hasNext());

    constraints = s.getVar(y).getConstraintsIterator();
    assertTrue(constraints.hasNext());
    assertEquals(constraints.next(), s.getCstr(c2));
    assertFalse(constraints.hasNext());
  }

    /*public void testMult() {
      LOGGER.info("Regression currentElement: 27/01/2006");

      Model pb = new Model();
      IntegerVariable v = pb.makeIntVar("x1", 0, 2);
      Constraint c = pb.eq(pb.mult(3,v),1);
      pb.post(c);
      pb.solve();
      this.assertEquals(true, pb.isFeasible() != null);
      this.assertEquals(false, pb.isFeasible().booleanValue());
    } */


    @Test
    public void testCleanState() {
        String[] type = new String[]{"cp:enum", "cp:bound", "cp:btree", "cp:link"};

        for (String aType : type) {

            Model m = new CPModel();
            IntegerVariable v = makeIntVar("v", 1, 2, aType);
            m.addConstraint(geq(v, 1));
            Solver s = new CPSolver();
            s.read(m);
            try {
                //s.propagate();
                PropagationEngine pe = s.getPropagationEngine();
                boolean someEvents = true;
                while (someEvents) {
                    EventQueue q = pe.getNextActiveEventQueue();
                    if (q != null) {
                        q.propagateSomeEvents();
                    } else {
                        someEvents = false;
                    }
                }
                assertTrue(pe.checkCleanState());
            } catch (ContradictionException e) {
            }
        }
    }

}
