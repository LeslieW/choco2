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
package choco.cp.solver.search.limit;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.limit.Limit;

/**
 * compute the total amount of time (ms).
 *
 */
public final class TimeCount extends AbstractGlobalSearchLimit {


	public TimeCount(AbstractGlobalSearchStrategy theStrategy) {
		super(theStrategy, Integer.MAX_VALUE, Limit.TIME);
	}

	
	@Override
	public void initialize() {
		super.initialize();
		TimeCacheThread.reset();
	}


	@Override
	public void reset() {
		super.reset();
		TimeCacheThread.reset();
	}


	@Override
	public void endNode() throws ContradictionException {
		nb = TimeCacheThread.elapsedTimeMillis;
	}

	@Override
	public void newNode() throws ContradictionException {
		nb = TimeCacheThread.elapsedTimeMillis;
	}
	
}

