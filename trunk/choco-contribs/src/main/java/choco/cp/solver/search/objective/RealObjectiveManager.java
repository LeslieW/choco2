package choco.cp.solver.search.objective;

import choco.cp.solver.search.IObjectiveManager;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.real.RealIntervalConstant;
import choco.kernel.solver.variables.real.RealVar;

public abstract class RealObjectiveManager implements IObjectiveManager {
	
	public final RealVar objective;
	
	protected RealIntervalConstant boundInterval;
	
	protected double bound;
	
	protected double targetBound;
	
	protected double oppositeBound;
	
	public RealObjectiveManager(RealVar objective) {
		super();
		this.objective = objective;
	}

	@Override
	public final Var getObjective() {
		return objective;
	}

	@Override
	public final Number getObjectiveValue() {
		return Double.valueOf(getObjectiveRealValue());
	}
	
	@Override
	public final int getObjectiveIntValue() {
		return getObjectiveValue().intValue();
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
	public void postTargetBound() throws ContradictionException {
		objective.intersect(boundInterval);
	}

	@Override
	public final void writeObjective(Solution sol) {
		sol.recordRealObjective(getObjectiveRealValue());
	}
	
}
