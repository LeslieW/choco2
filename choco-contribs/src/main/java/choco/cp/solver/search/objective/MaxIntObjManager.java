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
package choco.cp.solver.search.objective;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

public final class MaxIntObjManager extends IntObjectiveManager {

	
	public MaxIntObjManager(IntDomainVar objective) {
		super(objective);
	}

	@Override
	public int getObjectiveIntValue() {
		return objective.getSup();
	}

	@Override
	public void initBounds() {
		bound = Integer.MIN_VALUE;
		oppositeBound = objective.getSup();
		targetBound = objective.getInf();
	}

	@Override
	public void postTargetBound() throws ContradictionException {
		objective.setInf(targetBound);
		
	}

	@Override
	public void setBound() {
		final int v = objective.getSup();
		if( v > bound) { bound = v;}
	}
	

	@Override
	public void setTargetBound() {
		targetBound = objective.getInf() + 1;
	}

	@Override
	public boolean isTargetInfeasible() {
		return targetBound > oppositeBound;
	}
	
	
	
}
