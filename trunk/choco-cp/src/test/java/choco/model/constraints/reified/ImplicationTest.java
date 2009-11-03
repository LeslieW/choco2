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
*                   N. Jussien    1999-2009      *
**************************************************/
package choco.model.constraints.reified;

import org.junit.Test;
import org.junit.Assert;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 3 nov. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class ImplicationTest {

    @Test
    public void test1(){
        IntegerVariable b = Choco.makeBooleanVar("b");
        IntegerVariable[] bs = Choco.makeBooleanVarArray("bs", 2);

        Constraint c = Choco.reifiedRightImp(b, bs[0], bs[1]);

        Model m = new CPModel();
        m.addConstraint(c);

        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();

        Assert.assertEquals("nb sol", 4, s.getSolutionCount());

    }

}
