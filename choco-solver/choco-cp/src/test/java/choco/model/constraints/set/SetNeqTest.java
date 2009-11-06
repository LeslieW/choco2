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
package choco.model.constraints.set;

import static choco.Choco.makeSetVar;
import static choco.Choco.neq;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.set.RandomSetValSelector;
import choco.cp.solver.search.set.RandomSetVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 23 avr. 2008
 * Time: 14:28:12
 */
public class SetNeqTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    Model m;
    Solver s;

    @After
    public void tearDown() throws Exception {
        LOGGER.info(s.pretty());
        s = null;
        m = null;
    }

    @Before
    public void setUp() throws Exception {
        m = new CPModel();
        s = new CPSolver();
    }

    @Test
    public void test2SetNotEq() {
		for (int seed = 1; seed < 20; seed++) {
            m = new CPModel();
            s = new CPSolver();
            SetVariable v1 = makeSetVar("v1", 1, 2);
			SetVariable v2 = makeSetVar("v2", 1, 2);

            m.addConstraint(neq(v1, v2));
			s.read(m);
			s.setVarSetSelector(new RandomSetVarSelector(s, seed));
            s.setValSetSelector(new RandomSetValSelector(seed+1));
            //CPSolver.setVerbosity(CPSolver.PROPAGATION);
            s.solve();
            do{
 //               LOGGER.info("v1 = " + s.getVar(v1));
 //               LOGGER.info("v2 = " + s.getVar(v2));
//                LOGGER.info("======================");
            } while(s.nextSolution());
            LOGGER.info("nb solution: " + s.getNbSolutions());

            //CPSolver.flushLogs();

            assertTrue(12 == s.getNbSolutions());
		}
	}
}
