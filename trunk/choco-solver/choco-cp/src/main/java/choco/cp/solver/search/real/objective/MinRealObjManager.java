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
package choco.cp.solver.search.real.objective;

import choco.kernel.solver.variables.real.RealIntervalConstant;
import choco.kernel.solver.variables.real.RealMath;
import choco.kernel.solver.variables.real.RealVar;

public final class MinRealObjManager extends RealObjectiveManager {

	
	public MinRealObjManager(RealVar objective) {
		super(objective);
	}

	@Override
	public double getObjectiveRealValue() {
		return objective.getInf();
	}

	private void setBoundInterval() {
		boundInterval = new RealIntervalConstant(Double.NEGATIVE_INFINITY, targetBound);
	}

	@Override
	public void initBounds() {
		bound = Double.POSITIVE_INFINITY;
		oppositeBound = objective.getInf();
		targetBound = objective.getSup();
		setBoundInterval();
	}

	@Override
	public void setBound() {
		final double v = objective.getInf();
		if( v < bound) { bound = v;}
	}
	

	@Override
	public void setTargetBound() {
		targetBound = RealMath.prevFloat(objective.getInf());
		setBoundInterval();
		
	}
	
	@Override
	public boolean isTargetInfeasible() {
		return targetBound < objective.getInf();
	}
	
	
}
