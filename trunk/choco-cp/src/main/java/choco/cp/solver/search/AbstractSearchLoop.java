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
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.ISearchLoop;


public abstract class AbstractSearchLoop implements ISearchLoop {

	public final AbstractGlobalSearchStrategy searchStrategy;

	private int restartCount;
	
	private int depth;

	protected boolean stop;

	
	public AbstractSearchLoop(AbstractGlobalSearchStrategy searchStrategy) {
		this.searchStrategy = searchStrategy;
	}

	

	public final AbstractGlobalSearchStrategy getSearchStrategy() {
		return searchStrategy;
	}



	public final int getRestartCount() {
		return restartCount;
	}


	public final int getCurrentDepth() {
		return depth;
	}

	
	@Override
	public void initialize() {
		restartCount = 0;
		depth = 0;
	}


	public final Boolean run() {
		stop = false;
		initLoop();
		while (!stop) {
			ChocoLogging.flushLogs();
			//TODO move nextMove into this class
			switch (searchStrategy.nextMove) {
			//The order of the condition is important. 
			//RESTART does not happen often and STOP only once.
			case OPEN_NODE: {
				openNode();
				break;
			}
			case UP_BRANCH: {
				depth--;
				upBranch();
				break;
			}
			case DOWN_BRANCH: {
				depth++;
				downBranch();
				break;
			}
			case RESTART: {
				depth = 0;
				restartCount++;
				restart();
				break;
			}
			case INIT_SEARCH: {
				initSearch();
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

	public abstract void initLoop();
	
	public abstract void openNode();
	
	public abstract void upBranch();
	
	public abstract void downBranch();
	
	public abstract void restart();
	
	public abstract void initSearch();
	
	public abstract Boolean endLoop();


}
