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
package choco.kernel.solver.variables.scheduling;

import choco.IPretty;




/**
 * The Interface ITask represent a scheduling entity : a task or activity.
 *  A task is defined by a starting time and a duration.
 *  We do not allow preemption.
 *
 * @author Arnaud Malapert
 */
public interface ITask extends IPretty {


	/**
	 * Gets the ID of the task.
	 *
	 * @return the iD
	 */
	int getID();

	/**
	 * Gets the name of the task.
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Gets the Earliest Starting Time (EST).
	 *
	 * @return the EST
	 */
	int getEST();

	/**
	 * Gets the Earliest Completion Time (ECT).
	 *
	 * @return the ECT
	 */
	int getECT();

	/**
	 * Gets the Latest Starting Time (LST).
	 *
	 * @return the LST
	 */
	int getLST();

	/**
	 * Gets the Latest Completion Time (LCT).
	 *
	 * @return the LCT
	 */
	int getLCT();


	/**
	 * Gets the minimum duration.
	 *
	 * @return the minimum duration the task
	 */
	int getMinDuration();

	/**
	 * Gets the maximum duration.
	 *
	 * @return the max duration
	 */
	int getMaxDuration();

	/**
	 * Checks for if the duration is constant.
	 *
	 * @return true, if successful
	 */
	boolean hasConstantDuration();


	/**
	 * Checks if the task is scheduled. The task is scheduled if its starting time and its duration are fixed.
	 *
	 * @return true, if the tasks is scheduled
	 */
	boolean isScheduled();

	int getSlack();
	
	double getCentroid();
	
	boolean hasCompulsoryPart();

}




