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
package choco.solver;

import choco.cp.solver.CPSolver;
import choco.kernel.common.util.ChocoUtil;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import org.junit.Ignore;
import org.junit.Test;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 16 déc. 2008
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class SolverTest {

    @Test
    @Ignore
    public void testCharge1(){
        int cpt = 0;
		int newcpt;
        int[] nbVar = new int[]{10, 100, 1000, 100000, 1000000};
        for(int i = 1; i < nbVar.length; i++) {
            Runtime.getRuntime().gc();
            long t = System.currentTimeMillis();
            int n = nbVar[i];
            int b = 100;
            Solver solver = new CPSolver();
            IntDomainVar[] v = new IntDomainVar[n];
            for(int k = 0; k < n; k++){
                v[k] = solver.createBoundIntVar("v_"+k, 1, b);
            }
            newcpt = (int) Runtime.getRuntime().totalMemory() - (int) Runtime.getRuntime().freeMemory();
//                if (cpt != 0) {
//                    assertTrue(newcpt <= cpt + 10000);
//                }
            cpt = newcpt;
            System.out.print("|" + ChocoUtil.pad("" + n, -9, " ") + " |");
            System.out.print("|" + ChocoUtil.pad("" + (System.currentTimeMillis() - t), -5, " ") + " |");
            System.out.println("|" + ChocoUtil.pad("" + cpt, -10, " ") + " |");
        }
    }


    @Test
    @Ignore
    public void testCharge2(){
        int cpt = 0;
		int newcpt;
        int[] nbCstr = new int[]{10, 100, 1000, 100000, 1000000};
        for(int i = 1; i < nbCstr.length; i++) {
            Runtime.getRuntime().gc();
            long t = System.currentTimeMillis();
            int n = nbCstr[i];
            int b = 10;
            Solver solver = new CPSolver();
            IntDomainVar v  = solver.createBoundIntVar("v", 1 ,10);
            for(int k = 0; k < n; k++){
                solver.post(solver.eq(v, 5));
            }
            newcpt = (int) Runtime.getRuntime().totalMemory() - (int) Runtime.getRuntime().freeMemory();
//                if (cpt != 0) {
//                    assertTrue(newcpt <= cpt + 10000);
//                }
            cpt = newcpt;
            System.out.print("|" + ChocoUtil.pad("" + n, -9, " ") + " |");
            System.out.print("|" + ChocoUtil.pad("" + (System.currentTimeMillis() - t), -5, " ") + " |");
            System.out.println("|" + ChocoUtil.pad("" + cpt, -10, " ") + " |");
        }
    }

}
