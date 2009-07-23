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

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.*;
import choco.kernel.solver.search.ISearchLoop;


public abstract class AbstractSearchLoop implements ISearchLoop {

	protected final AbstractGlobalSearchStrategy searchStrategy;

	protected boolean stop = false;

    protected int depth;

	public AbstractSearchLoop(AbstractGlobalSearchStrategy searchStrategy) {
		this.searchStrategy = searchStrategy;
	}

    public int getCurrentDepth(){
        return depth;
    }

	public final Boolean run() {
		initLoop();
		stop = false;
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
				restart();
				break;
			}
			case INIT_SEARCH: {
                depth = 0;
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
