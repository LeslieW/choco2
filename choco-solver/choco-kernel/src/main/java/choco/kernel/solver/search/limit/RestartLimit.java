package choco.kernel.solver.search.limit;

import choco.kernel.solver.search.AbstractGlobalSearchStrategy;

public final class RestartLimit extends AbstractGlobalSearchLimit {

	
	public RestartLimit(AbstractGlobalSearchStrategy theStrategy, int theLimit) {
		super(theStrategy, theLimit, Limit.RESTART);
	}

	

	@Override
	public final int getNb() {
		return strategy.getRestartCount();
	}

}
