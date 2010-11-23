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
package choco.kernel.solver.constraints.global.scheduling;

import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

import java.util.Iterator;
import java.util.List;



/**
 * @author Arnaud Malapert</br> 
 * @since 23 janv. 2009 version 2.0.1</br>
 * @version 2.0.3</br>
 */
public interface IResource<T extends ITask> extends IResourceParameters {
	
	/**
	 * get the task with the given index
	 * @param idx index of the task
	 */
	T getTask(int idx);
	
	IRTask getRTask(int idx);

	int getNbTasks();

	/**
	 * an iterator over all tasks
	 * @return
	 */
	Iterator<T> getTaskIterator();
	
	/**
	 * A view of the resource as an immutable list.
	 */
	List<T> asTaskList();

	/**
	 * A view of the resource as an immutable list.
	 */
	List<IRTask> asRTaskList();

}
