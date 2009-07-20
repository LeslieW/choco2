package choco.cp.solver.search.objective;

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
		bound = objective.getSup();
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
	
	
}
