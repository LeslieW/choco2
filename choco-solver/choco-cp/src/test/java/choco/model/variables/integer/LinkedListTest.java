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
package choco.model.variables.integer;

import choco.Choco;
import static choco.Choco.makeIntVar;
import static choco.Choco.neq;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 20 nov. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class LinkedListTest {

    @Test
     public void test_patakm1() {
        ChocoLogging.setVerbosity(Verbosity.VERBOSE);
        Model m = new CPModel();
        IntegerVariable[] arr = new IntegerVariable[1];
        int[] values = {-20000000, 20000000};
        for (int i = 0; i < arr.length; i++) {
            arr[i] = makeIntVar("", values, "cp:link");
            m.addConstraint(neq(arr[i], 1));
        }

        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();
        Assert.assertEquals(2, s.getSolutionCount());
    }

    @Test
     public void test_patakm2() {
        String option = "cp:link";
        for(int i = 0; i < 1000; i++){
            Random r = new Random(i);
            Model m = new CPModel();
            int lb = r.nextInt(100) * (r.nextBoolean()?1:-1);
            int ub = lb+ r.nextInt(100);
            boolean isArray = r.nextBoolean();
            IntegerVariable link = (isArray?Choco.makeIntVar("v", new int[]{lb, ub}, option):Choco.makeIntVar("v", lb, ub, option));
            m.addVariable(link);
            Solver s = new CPSolver();
            s.read(m);
            s.solveAll();
            Assert.assertEquals("["+lb+","+ (isArray?"...":"")+ub+"]", isArray?(ub-lb==0?1:2):(ub-lb+1), s.getNbSolutions());
        }
    }
}
