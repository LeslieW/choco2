/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
package choco.model.constraints.integer;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Test;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 20 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class SumTest {

    @Test
    public void testJSR331(){
        Model m = new CPModel();
        IntegerVariable[] vars = makeIntVarArray("v", 10, 0, 10);

		for (int i = 0; i < vars.length; i++) {
			if (i%2 == 0) {
                m.addConstraint(gt(vars[i],i));
            }
			else {
                m.addConstraint(lt(vars[i], i));
            }
		}
        IntegerVariable sum = Choco.makeIntVar("sum", 0, 100);

        m.addConstraint(eq(sum(vars), sum));
        Solver s = new CPSolver();
        s.read(m);
        s.setValIntIterator(new DecreasingDomain());
        ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        s.solve();
    }
}
