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
package choco.kernel.solver.search;

import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.AbstractIntBranchingStrategy;
import choco.kernel.solver.branch.IntBranching;

/**
 * An abstract class for all heuristics (variable, value, branching heuristics) related to search
 */
public abstract class AbstractSearchHeuristic {
	/**
	 * the branching object owning the variable heuristic
	 */
	protected AbstractIntBranchingStrategy branching;

	/**
	 * the model to which the heuristic is related
	 */
	protected final Solver solver;

	
	public AbstractSearchHeuristic(Solver solver) {
		super();
		this.solver = solver;
	}





	public final Solver getSolver() {
		return solver;
	}


	public final void setBranching(AbstractIntBranchingStrategy branching) {
		this.branching = branching;
	}

	/**
	 * each IVarSelector is associated to a branching strategy
	 *
	 * @return the associated branching strategy
	 */
	public final IntBranching getBranching() {
		return branching;
	}
}
