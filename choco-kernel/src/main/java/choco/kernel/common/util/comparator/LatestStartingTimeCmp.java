/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package choco.kernel.common.util.comparator;

import choco.kernel.solver.variables.scheduling.ITask;

import java.util.Comparator;

/**
 * The Class LatestStartingTimeComparator.
 */
public final class LatestStartingTimeCmp extends AbstractTaskComparator {

	public final static LatestStartingTimeCmp SINGLETON=new LatestStartingTimeCmp();

	public final static Comparator<ITask> RSINGLETON= java.util.Collections.reverseOrder(SINGLETON);

	private LatestStartingTimeCmp() {
		super();
	}

	@Override
	public int getValue(final ITask t) {
		return t.getLST();
	}


}
