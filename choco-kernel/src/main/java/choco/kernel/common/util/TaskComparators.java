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
package choco.kernel.common.util;

import static java.util.Collections.reverseOrder;

import java.util.Comparator;

import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

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
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
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

	//*****************************************************************//
	//*******************  RTasks  ********************************//
	//***************************************************************//
	private final static Comparator<IRTask> RT_EST = new TaskComparatorWrapper(makeEarliestStartingTimeCmp());

	private final static Comparator<IRTask> RT_ECT = new TaskComparatorWrapper(makeEarliestCompletionTimeCmp());

	private final static Comparator<IRTask> RT_LST = new TaskComparatorWrapper(makeLatestStartingTimeCmp());

	private final static Comparator<IRTask> RT_LCT = new TaskComparatorWrapper(makeLatestCompletionTimeCmp());

	private final static Comparator<IRTask> REV_RT_EST = reverseOrder(RT_EST);

	private final static Comparator<IRTask> REV_RT_ECT = reverseOrder(RT_ECT);

	private final static Comparator<IRTask> REV_RT_LST = reverseOrder(RT_LST);

	private final static Comparator<IRTask> REV_RT_LCT = reverseOrder(RT_LCT);

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


final class MinDurationCmp extends AbstractTaskComparator{

	public final static MinDurationCmp SINGLETON =new MinDurationCmp();

	public final static Comparator<ITask> RSINGLETON= reverseOrder(SINGLETON);

	private MinDurationCmp() {
		super();
	}



	@Override
	public int getValue(final ITask task) {
		return task.getMinDuration();
	}


}

/**
 * The Class LatestCompletionTimeComparator.
 */
final class LatestCompletionTimeCmp extends AbstractTaskComparator{

	public final static LatestCompletionTimeCmp SINGLETON=new LatestCompletionTimeCmp();

	public final static Comparator<ITask> RSINGLETON= reverseOrder(SINGLETON);

	private LatestCompletionTimeCmp() {
		super();
	}


	@Override
	public int getValue(final ITask task) {
		return task.getLCT();
	}


}

/**
 * The Class EarliestStartingTimeComparator.
 */
final class EarliestStartingTimeCmp extends AbstractTaskComparator {

	public final static EarliestStartingTimeCmp SINGLETON=new EarliestStartingTimeCmp();

	public final static Comparator<ITask> RSINGLETON= reverseOrder(SINGLETON);

	private EarliestStartingTimeCmp() {
		super();
	}

	@Override
	public int getValue(final ITask t) {
		return t.getEST();
	}


}

/**
 * The Class LatestStartingTimeComparator.
 */
final class LatestStartingTimeCmp extends AbstractTaskComparator{

	public final static LatestStartingTimeCmp SINGLETON=new LatestStartingTimeCmp();

	public final static Comparator<ITask> RSINGLETON= reverseOrder(SINGLETON);

	private LatestStartingTimeCmp() {
		super();
	}

	@Override
	public int getValue(final ITask t) {
		return t.getLST();
	}


}

/**
 * The Class EarliestCompletionTimeComparator.
 */
final class EarliestCompletionTimeCmp extends AbstractTaskComparator {

	public final static EarliestCompletionTimeCmp SINGLETON=new EarliestCompletionTimeCmp();

	public final static Comparator<ITask> RSINGLETON= reverseOrder(SINGLETON);

	private EarliestCompletionTimeCmp() {
		super();
	}

	@Override
	public int getValue(final ITask t) {
		return t.getECT();
	}

}
