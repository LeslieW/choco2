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
package choco.cp.solver.search.integer.objective;

import choco.kernel.solver.Solution;
import choco.kernel.solver.search.IObjectiveManager;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

public abstract class IntObjectiveManager implements IObjectiveManager {
	
	public final IntDomainVar objective;
	
	protected int oppositeBound;
	
	protected int bound;
	
	protected int targetBound;
	
	
	public IntObjectiveManager(IntDomainVar objective) {
		super();
		this.objective = objective;
	}

	@Override
	public final Var getObjective() {
		return objective;
	}

	@Override
	public final Number getObjectiveValue() {
		return Integer.valueOf(getObjectiveIntValue());
	}
	
	@Override
	public final double getObjectiveRealValue() {
		return getObjectiveValue().doubleValue();
	}

	@Override
	public final Number getBestObjectiveValue() {
		return Integer.valueOf(bound);
	}

	@Override
	public final Number getObjectiveTarget() {
		return Integer.valueOf(targetBound);
	}

	@Override
	public final void writeObjective(Solution sol) {
		sol.recordIntObjective(getObjectiveIntValue());
	}
	
}
