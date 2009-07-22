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
package choco.cp.solver.search;

import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.DOWN_BRANCH;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.INIT_SEARCH;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.OPEN_NODE;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.RESTART;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.STOP;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.UP_BRANCH;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractBranching;
import choco.kernel.solver.branch.AbstractIntBranching;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.ISearchLoop;
import choco.kernel.solver.search.IntBranchingTrace;


public abstract class AbstractSearchLoop implements ISearchLoop {

	protected final AbstractGlobalSearchStrategy searchStrategy;

	protected boolean stop = false;


	public AbstractSearchLoop(AbstractGlobalSearchStrategy searchStrategy) {
		this.searchStrategy = searchStrategy;
	}


	public final Boolean run() {
		initLoop();
		stop = false;
		while (!stop) {
			//ChocoLogging.flushLogs();
			//TODO move nextMove into this class
			switch (searchStrategy.nextMove) {
			//The order of the condition is important. 
			//RESTART does not happen often and STOP only once.
			case OPEN_NODE: {
				openNode();
				break;
			}
			case UP_BRANCH: {
				upBranch();
				break;
			}
			case DOWN_BRANCH: {
				downBranch();
				break;
			}
			case RESTART: {
				restart();
				break;
			}
			case INIT_SEARCH: {
				initialize();
				break;
			}
			case STOP: {
				stop = true;
				break;
			}
			}
		}
		return endLoop();
	}

	protected abstract void initLoop();
	
	
	protected abstract Boolean endLoop();


}
