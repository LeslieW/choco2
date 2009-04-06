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

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 12 août 2008
 * Time: 11:31:25
 */
public class EqTest {

    CPModel m;
    CPSolver s;

    @Before
    public void before(){
        m = new CPModel();
        s = new CPSolver();
    }

    @After
    public void after(){
        m = null;
        s = null;
    }

    @Test
    public void test1(){
        int c = 1;
        IntegerVariable v = makeIntVar("v", 0, 2);
        m.addConstraint(eq(v, c));
        s.read(m);
        s.solve();
        Assert.assertTrue("no solution", s.getNbSolutions()!=0);   
    }

    @Test
    public void test2(){
        int c = 1;
        IntegerVariable v = makeIntVar("v", 0, 2);
        IntegerExpressionVariable w = minus(v, 1);
        m.addConstraint(eq(w, c));
        s.read(m);
        s.solve();
        Assert.assertTrue("no solution", s.getNbSolutions()!=0);
    }

    @Test
    public void test3(){
        IntegerVariable v1 = makeIntVar("v1", 0, 2);
        IntegerVariable v2 = makeIntVar("v2", 0, 2);
        IntegerExpressionVariable w1 = plus(v1, 1);
        IntegerExpressionVariable w2 = minus(v2, 1);
        m.addConstraint(eq(w1, w2));
        s.read(m);
        s.solve();
        Assert.assertTrue("no solution", s.getNbSolutions()!=0);
    }

    @Test
    public void test4(){
        IntegerVariable v1 = makeIntVar("v1", 0, 2);
        IntegerVariable v2 = makeIntVar("v2", 0, 2);
        m.addConstraint(eq(v1, v2));
        s.read(m);
        s.solve();
        Assert.assertTrue("no solution", s.getNbSolutions()!=0);
    }

    @Test
    public void test5(){
        IntegerVariable v = Choco.makeIntVar("v", -2, 0);
        Constraint c = eq(v, plus(v,0));
        m.addConstraint(c);
        s.read(m);
        Assert.assertEquals("wrong type",CPSolver.TRUE, s.getCstr(c));
        s.solveAll();
        Assert.assertEquals("nb of solution", 3, s.getNbSolutions());

    }


    @Test
    public void test6(){
        IntegerVariable v = Choco.makeIntVar("v", -2, 0);
        Constraint c = eq(v, plus(v,1));
        m.addConstraint(c);
        s.read(m);
        Assert.assertEquals("wrong type",CPSolver.FALSE, s.getCstr(c));
        s.solveAll();
        Assert.assertEquals("nb of solution", 0, s.getNbSolutions());

    }
}
