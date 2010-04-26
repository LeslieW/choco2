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
package choco.cp.solver.search;

import choco.cp.solver.search.integer.objective.MaxIntObjManager;
import choco.cp.solver.search.integer.objective.MinIntObjManager;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.AbstractOptimize;
import choco.kernel.solver.search.IObjectiveManager;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class BranchAndBound extends AbstractOptimize {

	/**
	 * Builds a new optimizing strategy with the specified variable.
	 * @param solver
     * @param maximize states if the objective variable should be maximized
     * @param configuration
     */
	public BranchAndBound(Solver solver, IntDomainVar objective, boolean maximize, Configuration configuration) {
		super(solver, makeDefaultObjManager(objective, maximize), maximize, configuration);
	}

	private static IObjectiveManager makeDefaultObjManager(IntDomainVar objective, boolean maximize) {
		return maximize ? new MaxIntObjManager(objective) : new MinIntObjManager(objective);
	}
	

}