package choco.cp.solver.search.objective;

import choco.cp.solver.search.IObjectiveManager;
import choco.kernel.solver.Solution;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

public abstract class IntObjectiveManager implements IObjectiveManager {
	
	public final IntDomainVar objective;
	
	protected int bound;
	
	protected int targetBound;
	
	
	public IntObjectiveManager(IntDomainVar objective) {
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
