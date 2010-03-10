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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.model.constraints;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.matching.AllDifferent;
import choco.cp.solver.constraints.integer.IntLinComb;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.variables.integer.IntDomainVar;
import org.junit.Test;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 10 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class LangfordNumberTest {


    IntDomainVar[] decVars;

    // n the size of the number of dist
    // dist[i] distance between two occurences of the same color, |dist| = n
    // k the number of occurences of each number, here k = 2
    public void langfordNumber(CPSolver ls, int n, int[] dist) {
        IEnvironment env = ls.getEnvironment();
        // allvars[i] index of the first occurence of color i
        // allvars[i+n] index of the second occurence of color i
        IntDomainVar[] vars = new IntDomainVar[2*n];
        decVars = new IntDomainVar[n];
        for (int i = 0; i < 2*n; i++) {
            vars[i] = ls.createEnumIntVar("oc"+i,0,2*n-1);
            if (i < n) {
                decVars[i] = vars[i];
            }
        }

        for (int i = 0; i < n; i++) {
            // this.vars[i+n] - this.vars[i] = dist[i];
            ls.post(new IntLinComb(new IntDomainVar[]{vars[i+n],vars[i]},
                                    new int[]{1,-1}, 1, dist[i]+1, IntLinComb.EQ));
        }

        ls.post(new AllDifferent(vars, env));
    }


    @Test
    public void langfordNumber() {
        CPSolver ls = new CPSolver();
//        ls.unsafeSetPropagationEngine(new StaticEngine(ls));
        int[] dist = {1,2,3,4,5,6,7};
        langfordNumber(ls, 7, dist);
        ls.attachGoal(new AssignVar(new StaticVarOrder(ls, decVars), new IncreasingDomain()));
        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        ls.solveAll();
    }

}
