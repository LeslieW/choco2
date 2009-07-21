package choco.cp.solver.search.objective;

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

	private final void setBoundInterval() {
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
		targetBound = RealMath.prevFloat(objective.getSup());
		setBoundInterval();
		
	}
	
	@Override
	public boolean isTargetInfeasible() {
		return targetBound < objective.getInf();
	}
	
	
}
