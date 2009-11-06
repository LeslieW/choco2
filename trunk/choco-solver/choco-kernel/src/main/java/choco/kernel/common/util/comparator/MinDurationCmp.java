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

public final class MinDurationCmp extends AbstractTaskComparator{

	public final static MinDurationCmp SINGLETON =new MinDurationCmp();

	public final static Comparator<ITask> RSINGLETON= java.util.Collections.reverseOrder(SINGLETON);

	private MinDurationCmp() {
		super();
	}



	@Override
	public int getValue(final ITask task) {
		return task.getMinDuration();
	}


}
