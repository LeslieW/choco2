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
package choco.solver.search.set;

import static choco.Choco.makeSetVar;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.set.RandomSetValSelector;
import choco.cp.solver.search.set.RandomSetVarSelector;
import choco.kernel.model.Model;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.set.SetVar;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 23 avr. 2008
 * Time: 15:44:33
 * To change this template use File | Settings | File Templates.
 */
public class RandomSetValSelectorTest {

    @Test
    public void test1() {
        for (int j = 0; j < 20; j++) {
            Model m = new CPModel();
            Solver solver = new CPSolver();

            SetVariable s1 = makeSetVar("s1", 1, 7);
            SetVariable s2 = makeSetVar("s2", 1, 7);
            m.addVariables(s1, s2);
            solver.read(m);

            RandomSetVarSelector t = new RandomSetVarSelector(solver, j);
            RandomSetValSelector p = new RandomSetValSelector(j + 1);
            int n=0;
            do {
                SetVar s = t.selectSetVar();
                int i = p.getBestVal(s);
                try {
                    s.remFromEnveloppe(i, -1);
                    n++;
                } catch (ContradictionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } while (solver.getVar(s1).getEnveloppeDomainSize() > 0 || solver.getVar(s2).getEnveloppeDomainSize() > 0);
            assertEquals(n, 14);
        }
    }
}
