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
		bound = objective.getInf();
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
	
	
}
