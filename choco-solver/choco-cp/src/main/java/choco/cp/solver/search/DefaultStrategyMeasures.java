package choco.cp.solver.search;

import choco.cp.solver.search.limit.LimitManager;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.measures.AbstractMeasures;

public final class DefaultStrategyMeasures extends AbstractMeasures {

	
	public final AbstractGlobalSearchStrategy searchStrategy;
	
	private final AbstractSearchLoop searchLoop;
	
	private final LimitManager limitManager;
	
	
	public DefaultStrategyMeasures(AbstractGlobalSearchStrategy searchStrategy,
			AbstractSearchLoop searchLoop, LimitManager limitManager) {
		super();
		this.searchStrategy = searchStrategy;
		this.searchLoop = searchLoop;
		this.limitManager = limitManager;
	}

	public DefaultStrategyMeasures(AbstractGlobalSearchStrategy searchStrategy) {
		super();
		this.searchStrategy = searchStrategy;
		if (searchStrategy.searchLoop instanceof AbstractSearchLoop) {
			searchLoop = (AbstractSearchLoop) searchStrategy.searchLoop;
		}else {
			LOGGER.warning("search loop is incompatible with the strategy measures");
			throw new SolverException("search loop is incompatible with the strategy measures");
		}
		if (searchStrategy.limitManager instanceof LimitManager) {
			limitManager = (LimitManager) searchStrategy.limitManager;
		}else {
			LOGGER.warning("limit manager is incompatible with the strategy measures");
			throw new SolverException("limit manager is incompatible with the strategy measures");
		}
	}

	@Override
	public int getBackTrackCount() {
		return searchLoop.getBacktrackCount();
	}

	@Override
	public final int getFailCount() {
		return limitManager.getFailCount();
	}

	@Override
	public final int getRestartCount() {
		return searchLoop.getRestartCount();
	}

	@Override
	public final int getNodeCount() {
		return searchLoop.getNodeCount();
	}

	@Override
	public final int getTimeCount() {
		return limitManager.getTimeCount();
	}
	
	
	
	
}
