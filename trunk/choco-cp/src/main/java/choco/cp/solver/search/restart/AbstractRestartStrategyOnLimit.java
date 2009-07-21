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

import choco.kernel.solver.SolverException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.limit.Limit;

public abstract class AbstractRestartStrategyOnLimit implements RestartStrategy {

	protected int nbRestarts = 0;

	protected final Limit type;

	protected AbstractGlobalSearchLimit failLimit;

	private int currentLimit;

	public AbstractRestartStrategyOnLimit(Limit failLimit, int initialLimit) {
		super();
		this.type = failLimit;
		this.currentLimit = initialLimit;
		if(currentLimit < 1) {
			throw new SolverException("initial restart limit shoud be strictly positive.");
		}
	}

	public final Limit getLimit() {
		return type;
	}

	public final AbstractGlobalSearchLimit getFailLimit() {
		return failLimit;
	}

	public final void setFailLimit(AbstractGlobalSearchLimit failLimit) {
		this.failLimit = failLimit;
	}


	public final int getCurrentLimit() {
		return currentLimit;
	}

	protected abstract int getNextLimit();

	@Override
	public boolean shouldRestart(AbstractGlobalSearchStrategy search) {
		final boolean shouldRestart = (failLimit.getNb() >= currentLimit);
		if (shouldRestart) {
			nbRestarts++;
			currentLimit = getNextLimit();
		}
		return shouldRestart;
	}


	public int[] getExample(int lenght) {
		final int[] r=new int[lenght];
		nbRestarts = 0;
		r[0] = getCurrentLimit();
		for (int i = 1; i < r.length; i++) {
			nbRestarts++;
			r[i] = getNextLimit();
			
		}
		nbRestarts = 0;
		return r;
	}
}


