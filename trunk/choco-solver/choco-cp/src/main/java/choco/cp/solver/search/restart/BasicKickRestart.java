package choco.cp.solver.search.restart;

import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.IntBranchingTrace;

public class BasicKickRestart extends AbstractKickRestart {

	public BasicKickRestart(AbstractGlobalSearchStrategy searchStrategy) {
		super(searchStrategy);
	}

	@Override
	public void restoreRootNode(IntBranchingTrace ctx) {
		searchStrategy.clearTrace();
		searchStrategy.solver.worldPopUntil(searchStrategy.baseWorld + 1);
	}

}
