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

import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.limit.AbstractGlobalTimeLimit;
import choco.kernel.solver.search.limit.Limit;

/**
 * check the total amount of CPU time (user + system).
 * call {@link AbstractGlobalTimeLimit#updateTimeStamp()} before calling {@link AbstractGlobalSearchLimit#getNb()}.
 *
 */
public final class TimeCount extends TimeLimit {


	public TimeCount(AbstractGlobalSearchStrategy theStrategy) {
		super(theStrategy, Integer.MAX_VALUE);
		limitMask = 0;
	}


	/**
	 * should update the value before recording solution as the event are not handled.
	 */
	@Override
	public int getUpdatedNb() {
		newh = getTimeStamp();
		update();
		return nb;
	}
}

