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

import choco.cp.solver.search.restart.IKickRestart;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractBranching;
import choco.kernel.solver.branch.AbstractIntBranching;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.*;
import choco.kernel.solver.search.IntBranchingTrace;


public abstract class AbstractSearchLoopWithRestart extends AbstractSearchLoop {

	private int moveAfterSolution = UP_BRANCH;

	//It seems that it is important to reinit the branching when restarting after each solution with DWDeg.
	//If you only set up an OPEN_NODE, it really decreases performance
	//So we have to check the combinatin of DWDeg an a restart strategy.
	private int moveAfterRestart=  INIT_SEARCH;

	private int previousNbSolutions;

	private IKickRestart kickRestart;

	/**
	 * current trace object. 
	 */
	protected IntBranchingTrace ctx = null;


	public AbstractSearchLoopWithRestart(AbstractGlobalSearchStrategy searchStrategy, IKickRestart kickRestart) {
		super(searchStrategy);
		this.kickRestart = kickRestart;
	}
	
	

	public final IntBranchingTrace getCurrentTrace() {
		return ctx;
	}



	public final IKickRestart getKickRestart() {
		return kickRestart;
	}


	public final void setInitializeSearchAfterRestart(boolean reinit) {
		moveAfterRestart = reinit ? INIT_SEARCH : OPEN_NODE; 
	}
	
	public final void setRestartAfterEachSolution(boolean restart) {
		moveAfterSolution = restart ? RESTART : UP_BRANCH; 
	}



	public final void setKickRestart(IKickRestart kickRestart) {
		this.kickRestart = kickRestart;
	}



	//*****************************************************************//
	//*******************  INITIALIZATIONS ***************************//
	//***************************************************************//





	@Override
	public void initialize() {
		previousNbSolutions = 0;
		super.initialize();
	}

	private AbstractBranching br;

	@Override
	public void initSearch() {
		br = searchStrategy.mainGoal;
		while (br != null) {
			br.initBranching();
			br = br.getNextBranching();
		}
		searchStrategy.nextMove = OPEN_NODE;
	}



	@Override
	public void initLoop() {
		previousNbSolutions = searchStrategy.getSolutionCount();
		searchStrategy.setEncounteredLimit(null);
		ctx = searchStrategy.initialTrace();
	}


	/**
	 * reset the limit and compute the exit status. 
	 */
	@Override
	public Boolean endLoop() {
		searchStrategy.limitManager.reset();
		if (searchStrategy.getSolutionCount() > previousNbSolutions) {
			return Boolean.TRUE;
		} else if (searchStrategy.isEncounteredLimit()) {
			return null;
		} else {
			return Boolean.FALSE;
		}
	}


	//*****************************************************************//
	//*******************  OPEN_NODE  ********************************//
	//***************************************************************//

	private Object branchingObj;

	private AbstractIntBranching currentBranching;

	@Override
	public void openNode() {
		try {
			searchStrategy.newTreeNode();
			//looking for the next branching object
			currentBranching =  ctx.getBranching();
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
					currentBranching.setFirstBranch(ctx);
					searchStrategy.nextMove = AbstractGlobalSearchStrategy.DOWN_BRANCH;
					return; //the new node is opened.
				}
			}
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

			//we found a valid solution because there is no more branching object
			//the solution must instantiate at least the decision variables
			//Other variables should be fixed by propagation or remained not instantiated 
			searchStrategy.nextMove = moveAfterSolution; //set the next move (backtrack, restart or stop)
			stop = true; //a solution has been found, we should run the loop again to find another solution
			searchStrategy.recordSolution(); //record the solution (could change the nextMove)
		} catch (ContradictionException e) {
			searchStrategy.nextMove = e.getContradictionMove();
		}
	}


	//*****************************************************************//
	//*******************  UP_BRANCH  ********************************//
	//***************************************************************//

	protected abstract void worldPop();

	/**
	 * post the dynamic cut, backtrack and propagate.
	 * @throws choco.kernel.solver.ContradictionException can be thrown 
	 */
	protected void goUpBranch() throws ContradictionException {
		searchStrategy.postDynamicCut();
		ctx.getBranching().goUpBranch(ctx);
		searchStrategy.solver.propagate();
	}





	@Override
	public final void upBranch() {
		try {
			searchStrategy.endTreeNode(); //check limit
			worldPop();
			goUpBranch(); //backtrack
			//compute the next move
			if (!ctx.getBranching().finishedBranching(ctx)) {
				ctx.getBranching().setNextBranch(ctx);
				ctx.incrementBranchIndex();
				searchStrategy.nextMove = AbstractGlobalSearchStrategy.DOWN_BRANCH;
			} else {
				ctx = searchStrategy.popTrace();
				searchStrategy.nextMove = AbstractGlobalSearchStrategy.UP_BRANCH;
			}
		} catch (ContradictionException e) {
			ctx = searchStrategy.popTrace();
			searchStrategy.nextMove = e.getContradictionMove();
		}
	}


//*****************************************************************//
//*******************  DOWN_BRANCH  ********************************//
//***************************************************************//

protected abstract void worldPush();

@Override
public void downBranch() {
	try {
		worldPush();
		ctx.getBranching().goDownBranch(ctx);
		searchStrategy.solver.propagate();
		searchStrategy.nextMove = AbstractGlobalSearchStrategy.OPEN_NODE;
	} catch (ContradictionException e) {
		searchStrategy.nextMove = e.getContradictionMove();
	}
}

//*****************************************************************//
//*******************  RESTART  ********************************//
//***************************************************************//


/**
 * perform the restart.
 */
@Override
public void restart() {
	LOGGER.finest("=== restarting ...");
	kickRestart.restoreRootNode(ctx);
	try {
		searchStrategy.postDynamicCut();
		searchStrategy.solver.propagate();
		ctx = searchStrategy.getReusableInitialTrace();
		searchStrategy.nextMove = moveAfterRestart;
		if( searchStrategy.limitManager.newRestart()) {
			setRestartAfterEachSolution(false);
			searchStrategy.limitManager.cancelRestart();
		}
	} catch (ContradictionException e) {
		stop = true;
	}
}

}
