/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.model.constraints.real;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.solver.Solver;
import org.junit.Assert;
import org.junit.Test;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 10 juin 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class EqTest {

    @Test
    public void test1(){
        RealVariable x = new RealVariable("x", -.1, .1);
        Constraint c1= geq(x, 0);
        Constraint c2= leq(x, 0);

        Model m1 = new CPModel();
        m1.addConstraint(c1);
        m1.setPrecision(0.01);

        Model m2 = new CPModel();
        m2.addConstraint(c2);
        m2.setPrecision(0.01);


        Solver s1 = new CPSolver();
        s1.read(m1);
        s1.solveAll();

        Solver s2 = new CPSolver();
        s2.read(m2);
        s2.solveAll();

        Assert.assertEquals("not same number of solutions", s1.getNbSolutions(), s2.getNbSolutions());
    }


    @Test
    public void test2(){
        RealVariable x = new RealVariable("x", -.1, .1);
        Constraint c1= geq(0, x);
        Constraint c2= leq(0, x);

        Model m1 = new CPModel();
        m1.addConstraint(c1);
        m1.setPrecision(0.01);

        Model m2 = new CPModel();
        m2.addConstraint(c2);
        m2.setPrecision(0.01);


        Solver s1 = new CPSolver();
        s1.read(m1);
        s1.solveAll();

        Solver s2 = new CPSolver();
        s2.read(m2);
        s2.solveAll();

        Assert.assertEquals("not same number of solutions", s1.getNbSolutions(), s2.getNbSolutions());
    }


    @Test
    public void test3(){
        RealVariable x = new RealVariable("x", -.1, .1);
        RealVariable y = new RealVariable("y", -.1, .1);
        Constraint c1= geq(x, y);
        Constraint c2= leq(x, y);

        Model m1 = new CPModel();
        m1.addConstraint(c1);
        m1.setPrecision(0.01);

        Model m2 = new CPModel();
        m2.addConstraint(c2);
        m2.setPrecision(0.01);


        Solver s1 = new CPSolver();
        s1.read(m1);
        s1.solveAll();

        Solver s2 = new CPSolver();
        s2.read(m2);
        s2.solveAll();

        Assert.assertEquals("not same number of solutions", s1.getNbSolutions(), s2.getNbSolutions());
    }

    @Test
    public void test4(){
        RealVariable x = new RealVariable("x", -.1, .1);
        RealVariable y = new RealVariable("y", -.1, .1);
        RealVariable z = new RealVariable("z", -.1, .1);
        Constraint c1= geq(x, plus(y, z));
        Constraint c2= leq(x, plus(y, z));

        Model m1 = new CPModel();
        m1.addConstraint(c1);
        m1.setPrecision(0.01);

        Model m2 = new CPModel();
        m2.addConstraint(c2);
        m2.setPrecision(0.01);


        Solver s1 = new CPSolver();
        s1.read(m1);
        s1.solveAll();

        Solver s2 = new CPSolver();
        s2.read(m2);
        s2.solveAll();

        Assert.assertEquals("not same number of solutions", s1.getNbSolutions(), s2.getNbSolutions());
    }

    @Test
    public void test5(){
        RealVariable x = new RealVariable("x", -.1, .1);
        RealVariable y = new RealVariable("y", -.1, .1);
        RealVariable z = new RealVariable("z", -.1, .1);
        Constraint c1= geq(x, minus(y, z));
        Constraint c2= leq(x, minus(y, z));

        Model m1 = new CPModel();
        m1.addConstraint(c1);
        m1.setPrecision(0.01);

        Model m2 = new CPModel();
        m2.addConstraint(c2);
        m2.setPrecision(0.01);


        Solver s1 = new CPSolver();
        s1.read(m1);
        s1.solveAll();

        Solver s2 = new CPSolver();
        s2.read(m2);
        s2.solveAll();

        Assert.assertEquals("not same number of solutions", s1.getNbSolutions(), s2.getNbSolutions());
    }
}
