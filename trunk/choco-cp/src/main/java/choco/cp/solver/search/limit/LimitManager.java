package choco.cp.solver.search.limit;


import static choco.kernel.solver.ContradictionException.SEARCH_LIMIT;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.RESTART;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.STOP;

import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.GlobalSearchLimit;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.restart.NoRestartStrategy;
import choco.kernel.solver.search.restart.UniversalRestartStrategy;

public class LimitManager implements GlobalSearchLimit {

	public final static Logger LOGGER = ChocoLogging.getSearchLogger();

	protected final AbstractGlobalSearchStrategy searchStrategy;
	
	protected AbstractGlobalSearchLimit restartLimit;

	protected AbstractGlobalSearchLimit searchLimit;

	//RESTART LIMIT
	protected UniversalRestartStrategy restartStrategy;

	protected AbstractGlobalSearchLimit restartStrategyLimit;
	
	private int restartFromStrategyCount = 0;

	private int restartCutoff = 0;

	//COUNTERS
	private int timeCount;

	private long starth;

	private AbstractGlobalSearchLimit failCount; //if only want to monitor the limit
	
	public LimitManager(AbstractGlobalSearchStrategy searchStrategy) {
		super();
		this.searchStrategy = searchStrategy;
	}

	//*****************************************************************//
	//*******************  GETTERS/SETTERS ****************************//
	//***************************************************************//
	public final AbstractGlobalSearchLimit getRestartLimit() {
		return restartLimit;
	}

	public final UniversalRestartStrategy getRestartStrategy() {
		return restartStrategy;
	}

	public final AbstractGlobalSearchLimit getRestartStrategyLimit() {
		return restartStrategyLimit;
	}

	public final void setRestartLimit(AbstractGlobalSearchLimit restartLimit) {
		this.restartLimit = restartLimit == null ? NoLimit.SINGLOTON : restartLimit;
	}

	public final AbstractGlobalSearchLimit getSearchLimit() {
		return searchLimit;
	}



	public final void setSearchLimit(AbstractGlobalSearchLimit searchLimit) {
		this.searchLimit = searchLimit == null ? NoLimit.SINGLOTON : searchLimit;
	}

	
	public final void setRestartStrategy(UniversalRestartStrategy restartStrategy, AbstractGlobalSearchLimit restartStrategyLimit) {
		if( restartStrategyLimit == null || restartStrategy == null) {
			this.restartStrategyLimit = NoLimit.SINGLOTON;
			this.restartStrategy = NoRestartStrategy.SINGLOTON;
		}else {
			this.restartStrategy = restartStrategy;
			this.restartStrategyLimit = restartStrategyLimit;
		}
	}


	public final int getRestartFromStrategyCount() {
		return restartFromStrategyCount;
	}


	public final int getRestartCutoff() {
		return restartCutoff;
	}


	public final int getFailCount() {
		return failCount == null ? -1 : failCount.getNb();
	}

	public final int getTimeCount() {
		return timeCount;
	}

	@Override
	public final AbstractGlobalSearchStrategy getSearchStrategy() {
		return searchStrategy;
	}


	
	//*****************************************************************//
	//*******************  LIMIT MANAGEMENT **************************//
	//***************************************************************//
	protected final void updateTimeCount() {
		timeCount = (int) (TimeCacheThread.currentTimeMillis - starth);
	}

	@Override
	public final void initialize() {
		//starth = TimeCacheThread.currentTimeMillis;
		starth = System.currentTimeMillis();
		TimeCacheThread.currentTimeMillis = starth;
		restartFromStrategyCount = 0;
		restartCutoff = restartStrategy.getScaleFactor();
		restartStrategyLimit.setNbMax(restartCutoff);
	}

	@Override
	public final void reset() {
		//TODO create EndTreeSearch method to have maximal time precision when ending search.
		updateTimeCount();
		restartStrategyLimit.setNbMax( restartStrategyLimit.getNb() + restartCutoff);
	}

	

	@Override
	public void endTreeSearch() {
		timeCount = (int) (System.currentTimeMillis() - starth);
		
	}

	@Override
	public final void newNode() throws ContradictionException {
		updateTimeCount();
		if( searchLimit.getNb() >= searchLimit.getNbMax()) {
			//end search
			searchStrategy.setEncounteredLimit(searchLimit);
			searchStrategy.solver.getPropagationEngine().raiseContradiction(searchLimit, SEARCH_LIMIT, STOP);
		}
		if( restartStrategyLimit.getNb() >= restartStrategyLimit.getNbMax()) {
			//update cutoff
			restartFromStrategyCount++;
			restartCutoff = restartStrategy.getNextCutoff(restartFromStrategyCount);
			restartStrategyLimit.setNbMax( restartStrategyLimit.getNb() + restartCutoff);
			//perform restart
			//TODO set a flag to indicate if we are recording nogoods
			searchStrategy.solver.getPropagationEngine().raiseContradiction(searchLimit, SEARCH_LIMIT, RESTART);
		}
	}

	@Override
	public final void endNode() throws ContradictionException {
		updateTimeCount();
		if( searchLimit.getNb() >= searchLimit.getNbMax()) {
			//end search
			searchStrategy.setEncounteredLimit(searchLimit);
			searchStrategy.solver.getPropagationEngine().raiseContradiction(searchLimit, SEARCH_LIMIT, STOP);
		}
		//do not restart while backtraking.
		//side effects with nogood recording
		//can also miss the end of the search
	}

	@Override
	public final boolean newRestart() {
		return restartLimit.getNb() >= restartLimit.getNbMax();
	}

	
	public final void cancelRestart() {
		restartLimit = NoLimit.SINGLOTON;
		restartStrategyLimit = NoLimit.SINGLOTON;
		restartStrategy = NoRestartStrategy.SINGLOTON;
	}


	@Override
	public String pretty() {
		final StringBuilder stb = new StringBuilder();
		if( searchLimit.getUnit() != NoLimit.NO_LIMIT_UNIT) {
			stb.append("Limits{ search:").append(searchLimit.pretty());
			if( restartLimit.getUnit() != NoLimit.NO_LIMIT_UNIT) {
				stb.append(" ; restart:").append(restartLimit.pretty());
			}
			stb.append(" }");
		}else if( restartLimit.getUnit() != NoLimit.NO_LIMIT_UNIT) {
			stb.append("Limits{ restart:").append(restartLimit.pretty()).append(" }");
		}
		if(restartStrategy != null && restartStrategy.getName() != NoRestartStrategy.NO_RESTART_NAME) {
			stb.append(" RestartStrategy{ policy:").append(restartStrategy.pretty());
			stb.append(" ; count:").append(restartFromStrategyCount);
			stb.append(" ; cutoff:").append(restartStrategyLimit.pretty());
			stb.append(" }");
		}
		return new String(stb);
	}
}






