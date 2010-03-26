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
 * This class is a factory for tasks comparator.
 * It uses a singleton pattern for each type of comparator.
 *
 * @author Arnaud Malapert
 */
public final class TaskComparators {

	private TaskComparators() {}


	//*****************************************************************//
	//*******************  Tasks  ********************************//
	//***************************************************************//
	/**
	 * Compare according to the latest completion times of a pair of tasks.
	 * @return LCT comparator
	 */
	public static Comparator<ITask> makeLatestCompletionTimeCmp() {
		return LatestCompletionTimeCmp.SINGLETON;
	}



	/**
	 * Compare according to the earliest completion times of a pair of tasks.
	 * @return ECT comparator
	 */
	public static Comparator<ITask> makeEarliestCompletionTimeCmp() {
		return EarliestCompletionTimeCmp.SINGLETON;
	}

	/**
	 * Compare according to the latest starting times of a pair of tasks.
	 * @return LST comparator
	 */
	public static Comparator<ITask> makeLatestStartingTimeCmp() {
		return LatestStartingTimeCmp.SINGLETON;
	}

	/**
	 * Compare according to the earliest starting times of a pair of tasks.
	 * @return EST comparator
	 */
	public static Comparator<ITask> makeEarliestStartingTimeCmp() {
		return EarliestStartingTimeCmp.SINGLETON;
	}

	public static Comparator<ITask> makeReverseEarliestCompletionTimeCmp() {
		return EarliestCompletionTimeCmp.RSINGLETON;
	}

	/**
	 * Compare according to the latest starting times of a pair of tasks.
	 * @return LST comparator
	 */
	public static Comparator<ITask> makeReverseLatestStartingTimeCmp() {
		return LatestStartingTimeCmp.RSINGLETON;
	}

	public static Comparator<ITask> makeReverseLatestCompletionTimeCmp() {
		return LatestCompletionTimeCmp.RSINGLETON;
	}

	/**
	 * Compare according to the earliest starting times of a pair of tasks.
	 * @return EST comparator
	 */
	public static Comparator<ITask> makeReverseEarliestStartingTimeCmp() {
		return EarliestStartingTimeCmp.RSINGLETON;
	}

	/**
	 * Compare according to task's durations.
	 * @return a duration comparator.
	 */
	public static Comparator<ITask> makeMinDurationCmp() {
		return MinDurationCmp.SINGLETON;
	}
	
	public static Comparator<ITask> makeRMinDurationCmp() {
		return MinDurationCmp.RSINGLETON;
	}

	//*****************************************************************//
	//*******************  RTasks  ********************************//
	//***************************************************************//
	private final static Comparator<IRTask> RT_EST = new TaskComparatorWrapper(makeEarliestStartingTimeCmp());

	private final static Comparator<IRTask> RT_ECT = new TaskComparatorWrapper(makeEarliestCompletionTimeCmp());

	private final static Comparator<IRTask> RT_LST = new TaskComparatorWrapper(makeLatestStartingTimeCmp());

	private final static Comparator<IRTask> RT_LCT = new TaskComparatorWrapper(makeLatestCompletionTimeCmp());

	private final static Comparator<IRTask> REV_RT_EST = java.util.Collections.reverseOrder(RT_EST);

	private final static Comparator<IRTask> REV_RT_ECT = java.util.Collections.reverseOrder(RT_ECT);

	private final static Comparator<IRTask> REV_RT_LST = java.util.Collections.reverseOrder(RT_LST);

	private final static Comparator<IRTask> REV_RT_LCT = java.util.Collections.reverseOrder(RT_LCT);
	
	/**
	 * Compare according to the latest completion times of a pair of tasks.
	 * @return LCT comparator
	 */
	public static Comparator<IRTask> makeRLatestCompletionTimeCmp() {
		return RT_LCT;
	}

	/**
	 * Compare according to the earliest completion times of a pair of tasks.
	 * @return ECT comparator
	 */
	public static Comparator<IRTask> makeREarliestCompletionTimeCmp() {
		return RT_ECT;
	}

	/**
	 * Compare according to the latest starting times of a pair of tasks.
	 * @return LST comparator
	 */
	public static Comparator<IRTask> makeRLatestStartingTimeCmp() {
		return RT_LST;
	}

	/**
	 * Compare according to the earliest starting times of a pair of tasks.
	 * @return EST comparator
	 */
	public static Comparator<IRTask> makeREarliestStartingTimeCmp() {
		return RT_EST;
	}

	public static Comparator<IRTask> makeReverseREarliestCompletionTimeCmp() {
		return REV_RT_ECT;
	}

	/**
	 * Compare according to the latest starting times of a pair of tasks.
	 * @return LST comparator
	 */
	public static Comparator<IRTask> makeReverseRLatestStartingTimeCmp() {
		return REV_RT_LST;
	}

	public static Comparator<IRTask> makeReverseRLatestCompletionTimeCmp() {
		return REV_RT_LCT;
	}

	/**
	 * Compare according to the earliest starting times of a pair of tasks.
	 * @return EST comparator
	 */
	public static Comparator<IRTask> makeReverseREarliestStartingTimeCmp() {
		return REV_RT_EST;
	}

}

