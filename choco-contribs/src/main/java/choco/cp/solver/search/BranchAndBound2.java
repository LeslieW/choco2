package choco.cp.solver.search;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class BranchAndBound2 extends AbstractOptimize2 {

	public final IntDomainVar objective;

	public BranchAndBound2(IntDomainVar objective, boolean maximize) {
		super( makeBounds(objective, maximize), maximize);
		this.objective = objective;
	}
	
	private final static IBoundsManager makeBounds(IntDomainVar objective, boolean maximize) {
		return maximize ? new MaxIntBoundsManager(objective) : new MinIntBoundsManager(objective);
	}
	
	@Override
	public Var getObjective() {
		return objective;
	}
	
	public IntDomainVar getIntObjective() {
		return objective;
	}
	
	
}


abstract class IntBoundsManager implements IBoundsManager {
	
	public final IntDomainVar objective;
	
	protected int bound;
	
	protected int targetBound;
	
	
	public IntBoundsManager(IntDomainVar objective) {
		super();
		this.objective = objective;
	}

	@Override
	public final Var getObjective() {
		return objective;
	}

	@Override
	public final Number getObjectiveValue() {
		return Integer.valueOf(getObjectiveIntValue());
	}
	
	@Override
	public final double getObjectiveRealValue() {
		return getObjectiveValue().doubleValue();
	}

	@Override
	public final Number getBestObjectiveValue() {
		return Integer.valueOf(bound);
	}

	@Override
	public final Number getObjectiveTarget() {
		return Integer.valueOf(targetBound);
	}

	@Override
	public final void writeObjective(Solution sol) {
		sol.recordIntObjective(getObjectiveIntValue());
	}
	
}

final class MinIntBoundsManager extends IntBoundsManager {



	public MinIntBoundsManager(IntDomainVar objective) {
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



final class MaxIntBoundsManager extends IntBoundsManager {

	

	public MaxIntBoundsManager(IntDomainVar objective) {
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
