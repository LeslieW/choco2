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

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

public final class MinIntObjManager extends IntObjectiveManager {



	public MinIntObjManager(IntDomainVar objective) {
		super(objective);
	}

	@Override
	public int getObjectiveIntValue() {
		return objective.getInf();
	}

	@Override
	public void initBounds() {
		bound = Integer.MAX_VALUE;
		oppositeBound = objective.getInf();
		targetBound = objective.getSup();
	}

	@Override
	public void postTargetBound() throws ContradictionException {
		objective.setSup(targetBound);
		
	}

	@Override
	public void setBound() {
		final int v = objective.getInf();
		if( v < bound) { bound = v;}
	}
	

	@Override
	public void setTargetBound() {
		targetBound = objective.getSup() - 1;
	}
	
	@Override
	public boolean isTargetInfeasible() {
		return targetBound < oppositeBound;
	}
}
