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
