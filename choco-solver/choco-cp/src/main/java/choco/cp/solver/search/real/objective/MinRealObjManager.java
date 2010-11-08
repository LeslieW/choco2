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
	public double getInitialBoundValue() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public double getFloorValue() {
		return objective.getInf();
	}
	
	@Override
	public double getCeilValue() {
		return objective.getSup();
	}

    protected void setBoundInterval() {
		boundInterval = new RealIntervalConstant(Double.NEGATIVE_INFINITY, targetBound);
	}

	
	
	@Override
	public void setBound() {
		final double v = getFloorValue();
		if( v < bound) { bound = v;}
	}
	

	@Override
	public void setTargetBound() {
		targetBound = RealMath.prevFloat(getFloorValue());
		setBoundInterval();
	}
			
	@Override
	public boolean isTargetInfeasible() {
		return targetBound < floorBound;
	}
	
}
