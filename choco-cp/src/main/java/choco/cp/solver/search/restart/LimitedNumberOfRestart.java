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
package choco.cp.solver.search.restart;

import choco.kernel.solver.search.AbstractGlobalSearchStrategy;

/**
 * @author Arnaud Malapert
 *
 */
public class LimitedNumberOfRestart implements RestartStrategy {


	protected RestartStrategy strategy;

	protected int restartLimit;

	protected int nbRestart=0;

	public LimitedNumberOfRestart(RestartStrategy strategy, int restartLimit) {
		super();
		this.strategy =strategy;
		this.restartLimit = restartLimit;
	}

	public void reset() {
		nbRestart=0;
	}

	public final RestartStrategy getRstrategy() {
		return strategy;
	}

	public final void setRstrategy(RestartStrategy strategy) {
		this.strategy = strategy;
	}

	public final int getRestartLimit() {
		return restartLimit;
	}

	public final void setRestartLimit(int restartLimit) {
		this.restartLimit = restartLimit;
	}


	@Override
	public boolean shouldRestart(AbstractGlobalSearchStrategy search) {
		if(nbRestart < restartLimit) {
			boolean shouldRestart = strategy.shouldRestart(search);
			if (shouldRestart) {
				restartLimit++;
			}
			return shouldRestart;
		}else {return false;}
	}

}
