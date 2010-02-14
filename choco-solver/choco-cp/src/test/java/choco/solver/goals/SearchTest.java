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
package choco.solver.goals;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.goals.choice.Generate;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 20 mars 2008
 * Time: 08:35:12
 */
public class SearchTest {
	
	protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	@Test
	public void testnode() {
		//ChocoLogging.setVerbosity(Verbosity.SEARCH);
		int n1 = testNQueens(true);
		int n2 = testNQueens(false);
		assertEquals("Nb Nodes", n1 ,n2);
	}

	//return the number of nodes needed to solve the problem
	private int testNQueens(boolean withgoal) {
		int NB_REINES = 8;

		Model m = new CPModel();


		IntegerVariable[] vars = new IntegerVariable[NB_REINES];
		for (int i = 0; i < NB_REINES; i++) {
			vars[i] = makeIntVar("x" + i, 0, NB_REINES - 1);
		}

		for (int i = 0; i < NB_REINES; i++) {
			for (int j = i + 1; j < NB_REINES; j++) {
				m.addConstraint(neq(vars[i], vars[j]));
			}                                                                                             
		}

		for (int i = 0; i < NB_REINES; i++) {
			for (int j = i + 1; j < NB_REINES; j++) {
				int k = j - i;
				m.addConstraint(neq(vars[i], plus(vars[j], k)));
				m.addConstraint(neq(vars[i], minus(vars[j], k)));
			}
		}

		Solver s = new CPSolver();
		s.read(m);
		s.attachGoal(new AssignVar(new MinDomain(s), new IncreasingDomain()));
		if (withgoal) {
			s.setIlogGoal(new Generate(s.getVar(vars)));
		}

		s.solveAll();
		LOGGER.info("Nb solutions = " + s.getNbSolutions());
		return s.getNodeCount();
	}

}