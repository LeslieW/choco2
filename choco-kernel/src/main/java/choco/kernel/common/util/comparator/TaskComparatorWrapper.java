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

import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

import java.util.Comparator;

/**
 *
 * @author Arnaud Malapert</br>
 * @since 2 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
final class TaskComparatorWrapper implements Comparator<IRTask> {

	private final Comparator<ITask> taskComparator;

	public TaskComparatorWrapper(Comparator<ITask> taskComparator) {
		super();
		this.taskComparator = taskComparator;
	}
	/**
	 * In case of ties optional activities should be taken before regular activities.
	 */
	@Override
	public int compare(IRTask o1, IRTask o2) {
		int cmp = taskComparator.compare(o1.getTaskVar(), o2.getTaskVar());
		if(cmp == 0) {
			if(o1.isOptional()) {
				cmp = -1;
			}else if(o2.isOptional()) {
				cmp = 1;
			}
		}
		return cmp;
	}




}
