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
package choco.solver.search;

import static choco.Choco.allDifferent;
import static choco.Choco.makeIntVarArray;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.configure.LimitFactory;
import choco.cp.solver.search.SearchLimitManager;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.search.limit.Limit;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Arnaud Malapert
 *
 */
public class LimitTest {
    

	public final static int SIZE=100;

	public CPModel model;

	public CPSolver solver;

	@Before
	public void initialize() {
		//ChocoLogging.setVerbosity(Verbosity.SEARCH);
		model=new CPModel();
		IntegerVariable[] vars=makeIntVarArray("v", SIZE, 0, SIZE);
		model.addConstraint(Options.C_ALLDIFFERENT_AC,allDifferent(vars));
		solver=new CPSolver();
		solver.read(model);
        solver.attachGoal(new AssignVar(new MinDomain(solver), new IncreasingDomain()));
	}


	private void check(Limit type) {
		solver.solveAll();
		assertTrue(solver.isEncounteredLimit());
		assertEquals(type, solver.getEncounteredLimit().getType());
	}

	@Test
	public void testNodeLimit() {
		solver.setNodeLimit(SIZE*2);
		check(Limit.NODE);
		solver.getBackTrackCount();

	}

	@Test
	public void testBacktrackLimit() {
		solver.setBackTrackLimit(SIZE * 2);
		check(Limit.BACKTRACK);
	}

	@Test
	public void testFailTimeLimit() {
        solver.setFailLimit(SIZE/10);
		check(Limit.FAIL);
	}


	@Test
	public void testTimeLimit() {
		solver.setTimeLimit(SIZE*10);
		check(Limit.TIME);
	}

    @Test
	public void testTimeLimit2() {
		solver.setTimeLimit(SIZE*10);
        solver.setFirstSolution(false);
        solver.generateSearchStrategy();
        SearchLimitManager slm = (SearchLimitManager)solver.getSearchStrategy().getLimitManager();
        slm.getSearchLimit().setNbMax(10);
        solver.launch();
        assertTrue(solver.isEncounteredLimit());
		assertEquals(Limit.TIME, solver.getEncounteredLimit().getType());
	}
	
	@Test
	public void testRestartLimit1() {
		final int lim =7;
		solver.setTimeLimit(SIZE*20);
		solver.setRestart(true);
		solver.setRestartLimit(lim);
		check(Limit.TIME);
		assertEquals(lim, solver.getRestartCount());
	}

	@Test
	public void testRestartLimit2() {
		final int lim =3;
		solver.setTimeLimit(SIZE*20);
		solver.setRestartLimit(lim);
		solver.setLubyRestart(1, 2, lim);
		check(Limit.TIME);
		assertEquals(lim, solver.getRestartCount());
	}
	
	@Test
	public void testRestartLimit3() {
		solver.setTimeLimit(SIZE*20);
		LimitFactory.setRestartLimit(solver, Limit.NODE, 2);
		solver.setLubyRestart(1, 2);
		solver.setLubyRestart(1, 3);
		check(Limit.TIME);
		assertEquals(1, solver.getRestartCount());
	}
	

}
