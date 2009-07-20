package choco.cp.solver.search;

import choco.cp.solver.search.objective.MaxRealObjManager;
import choco.cp.solver.search.objective.MinRealObjManager;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.real.RealVar;

public class RealBranchAndBound2 extends AbstractOptimize2 {

	public final RealVar objective;

	public RealBranchAndBound2(RealVar objective, boolean maximize) {
		super( makeBounds(objective, maximize), maximize);
		this.objective = objective;
	}
	
	private final static IObjectiveManager makeBounds(RealVar objective, boolean maximize) {
		return maximize ? new MaxRealObjManager(objective) : new MinRealObjManager(objective);
	}
	
	@Override
	public Var getObjective() {
		return objective;
	}
	
	public RealVar getIntObjective() {
		return objective;
	}
	
	
}
