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
package choco.cp.solver.search.restart;

import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.IntBranchingTrace;

/**
 * an interface allow to control the restarting process in the search loop.
 * @author Arnaud Malapert</br> 
 * @since 29 juil. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public interface IKickRestart {

	
	AbstractGlobalSearchStrategy getSearchStrategy();
	
	/**
	 * This function restore the root state (trace, memory). 
	 * @param ctx the last decision as we restart only when opening node.
	 */
	void restoreRootNode(IntBranchingTrace ctx);
}

