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

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.search.IObjectiveManager;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.real.RealIntervalConstant;
import choco.kernel.solver.variables.real.RealVar;

public abstract class RealObjectiveManager implements IObjectiveManager {

	public final RealVar objective;

	protected RealIntervalConstant boundInterval;

	protected double bound;

	protected double floorBound;

	protected double targetBound;

	public RealObjectiveManager(RealVar objective) {
		super();
		this.objective = objective;
	}

	public abstract double getInitialBoundValue();

	public abstract double getFloorValue();

	public abstract double getCeilValue();

	@Override
	public final Var getObjective() {
		return objective;
	}

	@Override
	public final Number getObjectiveValue() {
		return Double.valueOf(getFloorValue());
	}

	@Override
	public final Number getBestObjectiveValue() {
		return Double.valueOf(bound);
	}

	@Override
	public final Number getObjectiveTarget() {
		return Double.valueOf(targetBound);
	}

	@Override
	public final Number getObjectiveFloor() {
		return Double.valueOf(floorBound);
	}
	
	@Override
	public final void initBounds() {
		bound = getInitialBoundValue();
		floorBound = getFloorValue();
		targetBound = getCeilValue();
        setBoundInterval();
	}

    protected abstract void setBoundInterval();

	@Override
	public final void postTargetBound() throws ContradictionException {
		objective.intersect(boundInterval);
	}

	@Override
	public final void postFloorBound() throws ContradictionException {
		throw new SolverException("not yet implemented");
	}

	@Override
	public final void postIncFloorBound() throws ContradictionException {
		throw new SolverException("not yet implemented");
	}


	@Override
	public final void incrementFloorBound() {
		throw new SolverException("not yet implemented");
	}

	@Override
	public final void writeObjective(Solution sol) {
		sol.recordRealObjective(getFloorValue());
	}
}
