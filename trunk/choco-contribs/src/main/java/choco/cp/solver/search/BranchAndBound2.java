package choco.cp.solver.search;

import choco.cp.solver.search.objective.MaxIntObjManager;
import choco.cp.solver.search.objective.MinIntObjManager;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class BranchAndBound2 extends AbstractOptimize2 {

	public final IntDomainVar objective;

	public BranchAndBound2(IntDomainVar objective, boolean maximize) {
		super( makeBounds(objective, maximize), maximize);
		this.objective = objective;
	}
	
	private final static IObjectiveManager makeBounds(IntDomainVar objective, boolean maximize) {
		return maximize ? new MaxIntObjManager(objective) : new MinIntObjManager(objective);
	}
	
	@Override
	public Var getObjective() {
		return objective;
	}
	
	public IntDomainVar getIntObjective() {
		return objective;
	}
	
	
}
