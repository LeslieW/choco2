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
package choco.model.constraints.global;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 8 janv. 2008
 * Time: 18:22:15
 * To change this template use File | Settings | File Templates.
 */
public class BoundGccTest {
    @Test
  public void testIsSatisfied() {
    Model m = new CPModel();
    IntegerVariable v1 = makeIntVar("v1", 1, 1);
    IntegerVariable v2 = makeIntVar("v2", 1, 1);
    IntegerVariable v3 = makeIntVar("v3", 2, 2);
    IntegerVariable v4 = makeIntVar("v4", 2, 2);
    Constraint c1 = globalCardinality(new IntegerVariable[]{v1, v2, v3, v4}, 1, 2, new int[]{1, 1}, new int[]{2, 2});
    Constraint c2 = globalCardinality(new IntegerVariable[]{v1, v2, v3, v4}, 1, 2, new int[]{1, 1}, new int[]{1, 3});
    System.out.println(c1.pretty());
    System.out.println(c2.pretty());
    m.addConstraints("cp:bc", c1, c2);
    CPSolver s = new CPSolver();
    s.read(m);
    SConstraint c = s.getCstr(c1);
    assertTrue(c.isSatisfied());
    assertFalse(s.getCstr(c2).isSatisfied());
  }

    @Test
  public void testIsSatisfiedVar() {
    Model m = new CPModel();
    IntegerVariable v1 = makeIntVar("v1", 1, 1);
    IntegerVariable v2 = makeIntVar("v2", 1, 1);
    IntegerVariable v3 = makeIntVar("v3", 2, 2);
    IntegerVariable v4 = makeIntVar("v4", 2, 2);
    IntegerVariable x = makeIntVar("x", 2, 2);
    IntegerVariable y = makeIntVar("y", 1, 1);
    Constraint c1 = globalCardinality(new IntegerVariable[]{v1, v2, v3}, 1, 2, new IntegerVariable[]{x, y});
    Constraint c2 = globalCardinality(new IntegerVariable[]{v1, v3, v4}, 1, 2, new IntegerVariable[]{x, y});
    System.out.println(c1.pretty());
    System.out.println(c2.pretty());
        m.addConstraints(c1, c2);
        CPSolver s = new CPSolver();
        s.read(m);
    assertTrue(s.getCstr(c1).isSatisfied());
    assertFalse(s.getCstr(c2).isSatisfied());
  }


    @Test
    public void test2(){
        int MAX = 6;
        for(int seed = 0; seed < 30; seed++){
            Random r = new Random();
            gcc(r.nextInt(MAX)+1, r.nextInt(MAX)+1);
        }
    }

    @Test
    public void test2bis(){
        gcc(3, 4);
    }

    private static void gcc(int nbVariable, int nbValue){
        System.out.println("dim:"+nbVariable+" nbVal:"+nbValue);
        Model m = new CPModel();
        Solver s = new CPSolver();

        IntegerVariable[] vars = makeIntVarArray("vars", nbVariable, 1, nbValue);
        IntegerVariable[] card = makeIntVarArray("card", nbValue, 0, nbVariable);

        m.addConstraint(Choco.globalCardinality(vars, 1, nbValue, card));
        s.read(m);
        s.solveAll();
        //todo : pas d'assert ?
    }



    @Test
    public void test3(){
        Model m = new CPModel();
        Solver s = new CPSolver();

        IntegerVariable[] vars = makeIntVarArray("vars", 6, 0, 3);
        IntegerVariable[] card = makeIntVarArray("card", 4, 0, 6);

        m.addConstraint(Choco.globalCardinality(vars, 0, 3, card));

        m.addConstraint(eq(vars[0], 0));
        m.addConstraint(eq(vars[1], 1));
        m.addConstraint(eq(vars[2], 3));
        m.addConstraint(eq(vars[3], 2));
        m.addConstraint(eq(vars[4], 0));
        m.addConstraint(eq(vars[5], 0));

        s.read(m);
        s.solve();
        Assert.assertTrue(s.getNbSolutions()>0);
    }

    @Test
    public void test4(){
        Model m = new CPModel();
        Solver s = new CPSolver();

        IntegerVariable[] vars = makeIntVarArray("vars", 2, 0, 1);
        IntegerVariable[] card = makeIntVarArray("card", 2, 0, 2);

        m.addConstraint(Choco.globalCardinality(vars, 0, 1, card));

        m.addConstraint(eq(vars[0], 0));
        m.addConstraint(eq(vars[1], 1));

        s.read(m);
        s.solve();
        Assert.assertTrue(s.getNbSolutions()>0);
    }

}
