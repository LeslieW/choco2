package choco.cp.solver.search.limit;

import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.limit.Limit;

public final class RestartLimit extends AbstractGlobalSearchLimit {

	
	public RestartLimit(AbstractGlobalSearchStrategy theStrategy, int theLimit) {
		super(theStrategy, theLimit, Limit.RESTART);
	}

	

	@Override
	public final int getNb() {
		return strategy.searchMeasures.getRestartCount();
	}

}
