/* ************************************************
*           _       _                            *
*          |  �(..)  |                           *
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

        IntSConstraint allDiff = new AllDifferent(vars, s.getEnvironment());
        SoftIntSConstraint softC = new SoftIntSConstraint(dist, allDiff);

        s.post(softC);
        s.solveAll();
    }

    @Test
    public void test4(){
        Solver s = new CPSolver();
        int n = 10;
        IntDomainVar[] vars = new IntDomainVar[n];
        for(int i = 0; i < vars.length; i++){
            vars[i] = s.createEnumIntVar("v_"+i, 1, n);
        }
        IntDomainVar[] dists = new IntDomainVar[n/2+1];
        for(int i = 0; i < dists.length; i++){
            dists[i] = s.createEnumIntVar("d_"+i, 0,1);
        }

        int k =0;
        IntSConstraint allDiff = new AllDifferent(vars, s.getEnvironment());
        SoftIntSConstraint softC = new SoftIntSConstraint(dists[k++], allDiff);
        s.post(softC);


        for(int i = 0; i < n ; i+=2){
            IntSConstraint eq = (IntSConstraint)s.eq(vars[i], vars[i+1]);
            SoftIntSConstraint seq = new SoftIntSConstraint(dists[k++],eq);
            s.post(seq);
        }

        IntDomainVar obj = s.createEnumIntVar("obj", 0, n);
        s.post(s.eq(obj, s.sum(dists)));

        System.out.println(s.pretty());
        s.minimize(obj, true);
    }

}
