/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.configure;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Arnaud Malapert</br> 
 * @since 27 juil. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public class LimitConfiguration {

	public final static Logger LOGGER = ChocoLogging.getEngineLogger();

	public Limit searchLimitType;

	protected int searchLimit = Integer.MAX_VALUE;

	protected Limit restartLimitType;

	protected int restartLimit = Integer.MAX_VALUE;

	protected Limit restartStrategyLimitType = Limit.BACKTRACK;

	public LimitConfiguration() {}

	private void checkPreviousConfiguration(String operation, Limit prevType, int prevLimit, Limit currentType, int currentLimit) {
		if(restartLimitType != null && restartLimit != Integer.MAX_VALUE 
		 && ( prevType != currentType || prevLimit !=  currentLimit) ) {
			LOGGER.log(Level.WARNING, 
					"override previous setting of the {0} limit: {1} {2} => {3} {4}.\n",
					new Object[]{operation, prevLimit, prevType.getUnit(), currentLimit, currentType.getUnit()}
			);
		}
	}


	public final Limit getRestartStrategyLimitType() {
		return restartStrategyLimitType;
	}

	public final void setRestartStrategyLimitType(Limit restartStrategyLimitType) {
		this.restartStrategyLimitType = restartStrategyLimitType != null ? restartStrategyLimitType : Limit.BACKTRACK;
	}

	/**
	 * A limit controlling the restarts. When the limit is reached, the last restart is performed.
	 */
	public final void setRestartLimit(Limit type, int limit) {
		checkPreviousConfiguration("restart", restartLimitType, restartLimit, type, limit);
		restartLimitType = type;
		restartLimit = limit;
	}


	/**
	 * A limit controlling the search. When the limit is reached, the search is interrupted.
	 */	
	public final void setSearchLimit(Limit type, int limit) {
		checkPreviousConfiguration("search", searchLimitType, searchLimit, type, limit);
		searchLimitType = type;
		searchLimit = limit;
	}

	public final AbstractGlobalSearchLimit makeRestartLimit(AbstractGlobalSearchStrategy strategy) {
		return makeLimit(strategy, restartLimitType, restartLimit);
	}

	public final AbstractGlobalSearchLimit makeSearchLimit(AbstractGlobalSearchStrategy strategy) {
		return makeLimit(strategy, searchLimitType, searchLimit);
	}

	public final AbstractGlobalSearchLimit createLimit(AbstractGlobalSearchStrategy strategy, Limit type, int theLimit) {
		switch (type) {
		case TIME: return new TimeLimit(strategy, theLimit);
		case NODE: return new NodeLimit(strategy, theLimit);
		case BACKTRACK: return new BackTrackLimit(strategy, theLimit);
		case RESTART: return new RestartLimit(strategy, theLimit);
		case FAIL: {
			strategy.solver.monitorFailLimit(true);
			return new FailLimit(strategy, theLimit);
		}
		default: 
			return null;
		}
	}

	public final AbstractGlobalSearchLimit makeLimit(AbstractGlobalSearchStrategy strategy, Limit type, int theLimit) {
		if( strategy != null && type !=null && theLimit != Integer.MAX_VALUE) {
			return createLimit(strategy, type, theLimit);
		} else {return null;}
	}

}
