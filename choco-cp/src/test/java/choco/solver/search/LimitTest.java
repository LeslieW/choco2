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
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.cp.solver.search.limit.*;
import choco.kernel.model.variables.integer.IntegerVariable;
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
		model=new CPModel();
		IntegerVariable[] vars=makeIntVarArray("v", SIZE, 0, SIZE);
		model.addConstraint("cp:ac",allDifferent(vars));
		solver=new CPSolver();
		solver.read(model);
        solver.attachGoal(new AssignVar(new MinDomain(solver), new IncreasingDomain()));
	}


	private void check(Class<?> c) {
		solver.solveAll();
		assertTrue(solver.isEncounteredLimit());
		assertEquals(c, solver.getEncounteredLimit().getClass());
	}

	@Test
	public void testNodeLimit() {
		solver.setNodeLimit(SIZE*2);
		check(NodeLimit.class);
		solver.getBackTrackCount();

	}

	@Test
	public void testBacktrackLimit() {
		solver.setBackTrackLimit(SIZE/10);
		check(BackTrackLimit.class);
	}

	@Test
	public void testFailTimeLimit() {
		solver.setFailLimit(SIZE/10);
		check(FailLimit.class);
	}


	@Test
	public void testTimeLimit() {
		solver.setTimeLimit(SIZE*10);
		check(TimeLimit.class);
	}

	@Test
	public void testCpuTimeLimit() {
		solver.setCpuTimeLimit(SIZE*10);
		check(CpuTimeLimit.class);
	}


}
