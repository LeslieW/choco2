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

import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.INIT_SEARCH;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.OPEN_NODE;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.RESTART;
import static choco.kernel.solver.search.AbstractGlobalSearchStrategy.UP_BRANCH;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractBranching;
import choco.kernel.solver.branch.AbstractIntBranching;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.IntBranchingTrace;


public abstract class AbstractSearchLoopWithRestart extends AbstractSearchLoop {

	protected int restartCount = 0;

	private int moveAfterSolution = UP_BRANCH;

	//It seems that it is important to reinit the branching when restarting after each solution with DWDeg.
	//If you only set up an OPEN_NODE, it really decreases performance
	//So we have to check the combinatin of DWDeg an a restart strategy.
	private int moveAfterRestart=  INIT_SEARCH;

	protected int previousNbSolutions = 0;

	protected IntBranchingTrace ctx = null;


	public AbstractSearchLoopWithRestart(AbstractGlobalSearchStrategy searchStrategy) {
		super(searchStrategy);
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



	@Override
	protected void initLoop() {
		previousNbSolutions = searchStrategy.getSolutionCount();
		searchStrategy.setEncounteredLimit(null);
		ctx = searchStrategy.initialTrace();
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

	private Object branchingObj;

	private AbstractIntBranching currentBranching;
	
	protected final void doOpenNode() throws ContradictionException {
		//looking for the next branching object
		currentBranching = (AbstractIntBranching) ctx.getBranching(); //TODO avoid cast
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
		doRecordSolution();
		

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
	}


	protected void doRecordSolution() {
		//the solution must instantiate at least the decision variables
		//Other variables should be fixed by propagation or remained not instantiated 
		searchStrategy.nextMove = moveAfterSolution; //set the next move (backtrack, restart or stop)
		stop = true; //a solution has been found, we should run the loop again to find another solution
		searchStrategy.recordSolution(); //record the solution (could change the nextMove)
	}

	
	/**
	 * post the dynamic cut, backtrack and propagate.
	 */
	protected final void goUpBranch() throws ContradictionException {
		searchStrategy.postDynamicCut();
		ctx.getBranching().goUpBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
		searchStrategy.solver.propagate();
	}

	
	protected abstract void doUpBranch();

	public final void upBranch() {
		if (searchStrategy.isTraceEmpty()) {
			stop = true;
		} else {
			doUpBranch();
		}
	}

	/**
	 * set the next decision, propagate and set the next move.
	 * @throws ContradictionException
	 */
	protected final void goDownBranch() throws ContradictionException {
		ctx.getBranching().goDownBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
		searchStrategy.solver.propagate();
		searchStrategy.nextMove = AbstractGlobalSearchStrategy.OPEN_NODE;
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

	protected void restoreRootNode() {
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
	@Override
	public void restart() {
		LOGGER.finest("=== restarting ...");
		restartCount++;
		searchStrategy.setEncounteredLimit(null);
		restoreRootNode();
		try {
			searchStrategy.postDynamicCut();
			searchStrategy.solver.propagate();
			searchStrategy.nextMove = moveAfterRestart;
		} catch (ContradictionException e) {
			stop = true;
		}
	}





}
