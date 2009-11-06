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
 * an abstract class comparator. It is used to implement all tasks comparator.
 * The most comparators use singleton pattern.
 */
abstract class AbstractTaskComparator implements Comparator<ITask> {


	/**
	 * Computes the comparison criteria for a task.
	 *
	 * @param t the index of the task
	 *
	 * @return the value of the criteria
	 */
	public abstract int getValue(ITask task);



	/**
	 * @see java.util.Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(final ITask task1, final ITask task2) {
		final int c1=getValue(task1);
		final int c2=getValue(task2);
		if(c1<c2) {return -1;}
		else if(c1>c2){return 1;}
		else {return 0;}
	}

}
