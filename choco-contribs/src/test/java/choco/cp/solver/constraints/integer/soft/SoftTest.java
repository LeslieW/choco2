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
package choco.cp.solver.constraints.integer.soft;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.matching.AllDifferent;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import org.junit.Test;

import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 9 nov. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class SoftTest {

    static Logger LOGGER = ChocoLogging.getTestLogger();

    @Test
    public void test1(){
        Solver s = new CPSolver();
        IntDomainVar v1 = s.createEnumIntVar("v1", 1, 5);
        IntDomainVar v2 = s.createEnumIntVar("v2", 1, 5);
        IntDomainVar dist = s.createBooleanVar("dist");

        IntSConstraint eq = (IntSConstraint)s.eq(v1, v2);
        SoftIntSConstraint softC = new SoftIntSConstraint(dist, eq);

        s.post(softC);

        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        s.solveAll();
    }

    @Test
    public void test2(){
        Solver s = new CPSolver();
        IntDomainVar v1 = s.createEnumIntVar("v1", 1, 5);
        IntDomainVar v2 = s.createEnumIntVar("v2", 1, 5);
        IntDomainVar dist = s.createBooleanVar("dist");

        IntSConstraint neq = (IntSConstraint)s.neq(v1, v2);
        SoftIntSConstraint softC = new SoftIntSConstraint(dist, neq);

        s.post(softC);

        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        s.solveAll();
    }

    @Test
    public void test3(){
        Solver s = new CPSolver();
        int n = 3;
        IntDomainVar[] vars = new IntDomainVar[n];
        for(int i = 0; i < vars.length; i++){
            vars[i] = s.createEnumIntVar("v1", 1, n);
        }
        IntDomainVar dist = s.createBooleanVar("dist");

        IntSConstraint allDiff = new AllDifferent(vars);
        SoftIntSConstraint softC = new SoftIntSConstraint(dist, allDiff);

        s.post(softC);

        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        s.solveAll();
    }

}
