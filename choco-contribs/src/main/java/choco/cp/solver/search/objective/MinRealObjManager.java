package choco.cp.solver.search.objective;

import choco.kernel.solver.variables.real.RealIntervalConstant;
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
		bound = objective.getSup();
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
		targetBound = objective.getSup() - 1;
		setBoundInterval();
		
	}
	
	
}
