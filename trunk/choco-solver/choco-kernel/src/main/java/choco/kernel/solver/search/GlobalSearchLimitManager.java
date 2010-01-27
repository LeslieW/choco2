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
package choco.kernel.solver.search;

import choco.IPretty;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.restart.UniversalRestartStrategy;


/**
 * The interface of objects limiting the global search exploration
 */
public interface GlobalSearchLimitManager extends IPretty {


	/**
	 * @return strategy the controller of the search exploration, managing the limit
	 */
	AbstractGlobalSearchStrategy getSearchStrategy();

	/**
	 * initialize the limit.
	 */
	void initialize();

	/**
	 * resets the limit (the counter run from now on)
	 *
	 */
	void reset();

	/**
	 * notify the limit object whenever a new node is created in the search tree
	 *
	 * @throws ContradictionException if the limit does not accept the creation of the new node.
	 */
	void newNode() throws ContradictionException;

	/**
	 * notify the limit object whenever the search closes a node in the search tree
	 *
	 *
	 * @throws ContradictionException if the limit does not accept the death of the node.
	 */
	void endNode() throws ContradictionException;

	void endTreeSearch();

	/**
	 * notify the limit object whenever the search has been restarted.
	 * return <code>true</code> if the limit does stop the restart process.
	 */
	boolean newRestart();
	
	/**
	 * Get the time in milliseconds elapsed since the beginning of the search.
	 */
	int getTimeCount();
	
	/**
	 * get the restart strategy, if any.
	 */
	UniversalRestartStrategy getRestartStrategy();
	
	/**
	 * cancel the restart strategy.
	 */
	void cancelRestartStrategy();
	
	/**
	 * get the number of restarts caused by the restart strategy.
	 * @return
	 */
	int getRestartFromStrategyCount();
	
	
}

