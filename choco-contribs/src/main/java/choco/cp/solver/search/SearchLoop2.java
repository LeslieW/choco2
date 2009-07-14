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

import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.*;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractBranching;
import choco.kernel.solver.branch.AbstractIntBranching;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.ISearchLoop;
import choco.kernel.solver.search.IntBranchingTrace;


public class SearchLoop2 implements ISearchLoop {

	protected final AbstractGlobalSearchStrategy searchStrategy;

	protected int restartCount = 0;

	private int moveAfterSolution = UP_BRANCH;

	//It seems that it is important to reinit the branching when restarting after each solution with DWDeg.
	//If you only set up an OPEN_NODE, it really decreases performance
	//So we have to check the combinatin of DWDeg an a restart strategy.
	private int moveAfterRestart=  INIT_SEARCH;

	protected int previousNbSolutions = 0;

	protected IntBranchingTrace ctx = null;

	protected boolean stop = false;


	public SearchLoop2(AbstractGlobalSearchStrategy searchStrategy) {
		this.searchStrategy = searchStrategy;
	}


	public final void setRestartAfterEachSolution(boolean restart) {
		moveAfterSolution = restart ? RESTART : UP_BRANCH; 
	}

	public final int getRestartCount() {
		return restartCount;
	}


	private AbstractBranching br;

	@Override
	public void initialize() {
		br = searchStrategy.mainGoal;
		while (br != null) {
			br.initBranching();
			br = br.getNextBranching();
		}
		searchStrategy.nextMove = OPEN_NODE;
	}



	public final Boolean run() {

		previousNbSolutions = searchStrategy.getSolutionCount();
		searchStrategy.setEncounteredLimit(null);
		ctx = searchStrategy.initialTrace();
		stop = false;

		while (!stop) {
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

	public void init() {
		// specific initialization for the very first solution search (start from the tree root, instead of last leaf)
//		if (searchStrategy.nextMove == AbstractGlobalSearchStrategy.INIT_SEARCH) {
//			searchStrategy.nextMove = AbstractGlobalSearchStrategy.OPEN_NODE;
//			ctx = new IntBranchingTrace(searchStrategy.mainGoal);
//			AbstractBranching b = searchStrategy.mainGoal;
//			while (b != null) {
//				b.initBranching();
//				b = b.getNextBranching();
//			}
//		} else {
//			ctx = searchStrategy.topTrace();
//		}
	}

	//TODO create private fields and remove cast.
	public void openNode() {
		try {
			searchStrategy.newTreeNode();
			//looking for the next branching object
			Object branchingObj = null;
			AbstractIntBranching currentBranching = (AbstractIntBranching) ctx.getBranching();
			while(currentBranching != null) {
				branchingObj = currentBranching.selectBranchingObject();
				if( branchingObj == null) {
					//the branching is achieved, check next branching
					currentBranching = (AbstractIntBranching) currentBranching.getNextBranching();
				}else {
					//create the node associated with the branching object
					ctx = searchStrategy.pushTrace();
					ctx.setBranching(currentBranching);
					ctx.setBranchingObject(branchingObj);
					ctx.setBranchIndex(currentBranching.getFirstBranch(ctx.getBranchingObject()));
					searchStrategy.nextMove = AbstractGlobalSearchStrategy.DOWN_BRANCH;
					return; //the new node is opened.
				}
			}
			//WE FOUND A VALID SOLUTION
			//the solution must instantiate at least the decision variables
			//Other variables should be fixed by propagation or remained not instantiated 
			searchStrategy.recordSolution();
			searchStrategy.nextMove = moveAfterSolution; //set a backtrack or restart move
			stop = true; //a solution has been found, we should run the loop again to find another solution

			//The original version makes more comparisons and affectations 

			//			Object branchingObj = null;
			//			AbstractIntBranching currentBranching = (AbstractIntBranching) ctx.getBranching();
			//			AbstractIntBranching nextBranching = currentBranching;
			//			while ((branchingObj == null) && (nextBranching != null)) {
			//				currentBranching = nextBranching;
			//				branchingObj = currentBranching.selectBranchingObject();
			//				nextBranching = (AbstractIntBranching) currentBranching.getNextBranching();
			//			}
			//			if (branchingObj != null) {
			//				ctx = searchStrategy.pushTrace();
			//				ctx.setBranching(currentBranching);
			//				ctx.setBranchingObject(branchingObj);
			//				ctx.setBranchIndex(currentBranching.getFirstBranch(ctx.getBranchingObject()));
			//				searchStrategy.nextMove = AbstractGlobalSearchStrategy.DOWN_BRANCH;
			//			} else {
			//				searchStrategy.recordSolution();
			//				searchStrategy.nextMove = moveAfterSolution;
			//				stop = true;
			//			}
		} catch (ContradictionException e) {
			searchStrategy.nextMove = e.getContradictionMove();
		}
	}

	public void upBranch() {
		if (searchStrategy.isTraceEmpty()) {
			stop = true;
		} else {
			try {
				searchStrategy.solver.worldPop();
				//searchStrategy.model.propagate();
				searchStrategy.endTreeNode();
				searchStrategy.postDynamicCut();
				ctx.getBranching().goUpBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
				searchStrategy.solver.propagate();
				if (!ctx.getBranching().finishedBranching(ctx.getBranchingObject(), ctx.getBranchIndex())) {
					ctx.setBranchIndex(ctx.getBranching().getNextBranch(ctx.getBranchingObject(), ctx.getBranchIndex()));
					searchStrategy.nextMove = AbstractGlobalSearchStrategy.DOWN_BRANCH;
				} else {
					ctx = searchStrategy.popTrace();
					//env.pushContext(ctx,false);
					searchStrategy.nextMove = AbstractGlobalSearchStrategy.UP_BRANCH;
				}
			} catch (ContradictionException e) {
				ctx = searchStrategy.popTrace();
				searchStrategy.nextMove = e.getContradictionMove();
			}
		}
	}

	public void downBranch() {
		try {
			searchStrategy.solver.getEnvironment().worldPush();
			ctx.getBranching().goDownBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
			searchStrategy.solver.propagate();
			searchStrategy.nextMove = AbstractGlobalSearchStrategy.OPEN_NODE;
		} catch (ContradictionException e) {
			searchStrategy.nextMove = e.getContradictionMove();
		}
	}

	public Boolean endLoop() {
		searchStrategy.resetLimits(false);
		if (searchStrategy.getSolutionCount() > previousNbSolutions) {
			return Boolean.TRUE;
		} else if (searchStrategy.isEncounteredLimit()) {
			return null;
		} else {
			return Boolean.FALSE;
		}
	}

	protected void restoreRootNode(IntBranchingTrace ctx) {
		searchStrategy.clearTrace();
		searchStrategy.solver.worldPopUntil(searchStrategy.baseWorld + 1);
		//((CPSolver) searchStrategy.getSolver()).initNogoodBase();
	}

	/**
	 * perform the restart.
	 *
	 * @param ctx the branching trace
	 * @return <code>true</code> if the loop should stop
	 */
	public void restart() {
		restartCount++;
		restoreRootNode(ctx);
		try {
			searchStrategy.postDynamicCut();
			searchStrategy.solver.propagate();
		} catch (ContradictionException e) {
			stop = true;
		}
		searchStrategy.nextMove = moveAfterRestart;
	}





}
