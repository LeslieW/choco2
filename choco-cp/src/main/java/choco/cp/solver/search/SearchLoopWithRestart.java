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
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.OPEN_NODE;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.UP_BRANCH;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.restart.RestartStrategy;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.IntBranchingTrace;

/**
 * A solver allowing to restart searching when no solution was found during to many time.
 */
public class SearchLoopWithRestart extends SearchLoop {

	protected RestartStrategy restartStrategy;

	//TODO transform into a choco limit
	protected int nbRestarts = 0;

	protected int restartMoveMask = OPEN_NODE + DOWN_BRANCH + UP_BRANCH;

	public SearchLoopWithRestart(AbstractGlobalSearchStrategy searchStrategy,
			RestartStrategy restartStrategy) {
		super(searchStrategy);
		this.restartStrategy = restartStrategy;
	}

	public final RestartStrategy getRestartStrategy() {
		return restartStrategy;
	}


	public final int getRestartCount() {
		return nbRestarts;
	}

	public final int getRestartMoveMask() {
		return restartMoveMask;
	}

	public void setRestartMoveMask(int restartMask) {
		if(restartMask == 0) {
			throw new SolverException("empty mask is forbidden");
		}
		this.restartMoveMask = restartMask;
	}

	protected final boolean checkRestartMoveMask(int move) {
		return (restartMoveMask & move) == move;
	}


	protected void restoreRootNode(IntBranchingTrace ctx) {
		searchStrategy.clearTrace();
		searchStrategy.solver.worldPopUntil(searchStrategy.baseWorld + 1);
        ((CPSolver) searchStrategy.getSolver()).initNogoodBase();
    }

	/**
	 * perform the restart.
	 *
	 * @param ctx the branching trace
	 * @return <code>true</code> if the loop should stop
	 */
	protected boolean restart(IntBranchingTrace ctx) {
		//ne restart que dans les OpenNode et les up car finishedbranching ?
		if (searchStrategy.nextMove == AbstractGlobalSearchStrategy.UP_BRANCH
				&& searchStrategy.isTraceEmpty()) {
			return true;
		} else {
			restoreRootNode(ctx);
			searchStrategy.nextMove = AbstractGlobalSearchStrategy.OPEN_NODE;
			ctx.setBranching(searchStrategy.mainGoal);
			searchStrategy.clearTrace();
			try {
				searchStrategy.postDynamicCut();
				searchStrategy.solver.propagate();
			} catch (ContradictionException e) {
				return true;
			}
			nbRestarts++;
			return false;
		}

	}

	@Override
	public Boolean run() {
		int previousNbSolutions = searchStrategy.getSolutionCount();
		searchStrategy.setEncounteredLimit(null);
		ctx = null;
		stop = false;
		init();
		boolean restartLimit;
		do {
			restartLimit = false;
			while (!stop) {
				if ( checkRestartMoveMask(searchStrategy.nextMove) && 
						restartStrategy.shouldRestart(searchStrategy)) {
					LOGGER.finest("=== restarting ...");
					stop = restart(ctx);
					if (!stop) {
						restartLimit = true;
					}
					break;
				}
				switch (searchStrategy.nextMove) {
				case AbstractGlobalSearchStrategy.OPEN_NODE: {
					openNode();
					break;
				}
				case AbstractGlobalSearchStrategy.UP_BRANCH: {
					upBranch();
					break;
				}
				case AbstractGlobalSearchStrategy.DOWN_BRANCH: {
					downBranch();
					break;
				}
				case AbstractGlobalSearchStrategy.STOP: {
					stop = true;
					break;
				}
				}
			}
			searchStrategy.limitManager.reset();
		} while (restartLimit);

		if (searchStrategy.getSolutionCount() > previousNbSolutions) {
			return Boolean.TRUE;
		} else if (searchStrategy.isEncounteredLimit()) {
			return null;
		} else {
			return Boolean.FALSE;
		}
	}

	//TODO is it usefull to override method ?
//	@Override
//	public void openNode() {
//		try {
//			searchStrategy.newTreeNode();
//			Object branchingObj = null;
//			AbstractIntBranching currentBranching = (AbstractIntBranching) ctx.getBranching();
//			AbstractIntBranching nextBranching = currentBranching;
//			//super use a while loop instead of do-while.
//			do {
//				currentBranching = nextBranching;
//				branchingObj = currentBranching.selectBranchingObject();
//				nextBranching = (AbstractIntBranching) currentBranching.getNextBranching();
//			} while ((branchingObj == null) && (nextBranching != null));
//			if (branchingObj != null) {
//				ctx = searchStrategy.pushTrace();
//				ctx.setBranching(currentBranching);
//				ctx.setBranchingObject(branchingObj);
//				ctx.setBranchIndex(currentBranching.getFirstBranch(ctx.getBranchingObject()));
//				searchStrategy.nextMove = AbstractGlobalSearchStrategy.DOWN_BRANCH;
//			} else {
//				searchStrategy.recordSolution();
//				searchStrategy.nextMove = AbstractGlobalSearchStrategy.UP_BRANCH;
//				stop = true;
//			}
//		} catch (ContradictionException e) {
//			searchStrategy.nextMove = AbstractGlobalSearchStrategy.UP_BRANCH;
//		}
//	}
}
