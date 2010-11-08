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
package choco.cp.solver.search.integer.varselector;

import choco.kernel.solver.Solver;
import choco.kernel.solver.search.integer.IntHeuristicIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class MaxDomain extends IntHeuristicIntVarSelector {
	public MaxDomain(Solver solver) {
		super(solver);
	}

	public MaxDomain(Solver solver, IntDomainVar[] vs) {
		super(solver, vs);
	}

	@Override
	public int getHeuristic(IntDomainVar v) {
		return -v.getDomainSize();
	}

}