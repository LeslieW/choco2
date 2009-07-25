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

import choco.cp.solver.search.objective.MaxRealObjManager;
import choco.cp.solver.search.objective.MinRealObjManager;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.real.RealVar;

public class RealBranchAndBound2 extends AbstractOptimize2 {

	public final RealVar objective;

	public RealBranchAndBound2(RealVar objective, boolean maximize) {
		super( makeBounds(objective, maximize), maximize);
		this.objective = objective;
	}
	
	private final static IObjectiveManager makeBounds(RealVar objective, boolean maximize) {
		return maximize ? new MaxRealObjManager(objective) : new MinRealObjManager(objective);
	}
	
	@Override
	public final Var getObjective() {
		return objective;
	}
	
	public final RealVar getIntObjective() {
		return objective;
	}
	
	
}
