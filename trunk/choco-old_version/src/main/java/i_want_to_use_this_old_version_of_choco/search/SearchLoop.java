package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 16 juil. 2007
 * Time: 09:48:05
 */
public class SearchLoop implements ISearchLoop {

	AbstractGlobalSearchSolver searchSolver;


	public SearchLoop(AbstractGlobalSearchSolver searchSolver) {
		this.searchSolver = searchSolver;
	}


	public Boolean run() {

		int previousNbSolutions = searchSolver.nbSolutions;
		searchSolver.encounteredLimit = null;
		IntBranchingTrace ctx = null;
		boolean stop = false;

		// specific initialization for the very first solution search (start from the tree root, instead of last leaf)
		if (searchSolver.nextMove == AbstractGlobalSearchSolver.INIT_SEARCH) {
			searchSolver.nextMove = AbstractGlobalSearchSolver.OPEN_NODE;
			ctx = new IntBranchingTrace(searchSolver.mainGoal);
		} else {
			ctx = searchSolver.topTrace();
		}
		while (!stop) {
			switch (searchSolver.nextMove) {
				case AbstractGlobalSearchSolver.OPEN_NODE: {
					try {
						searchSolver.newTreeNode();
						Object branchingObj = null;
						AbstractIntBranching currentBranching = (AbstractIntBranching) ctx.getBranching();
						AbstractIntBranching nextBranching = currentBranching;
						do {
							currentBranching = nextBranching;
							branchingObj = currentBranching.selectBranchingObject();
							nextBranching = (AbstractIntBranching) currentBranching.getNextBranching();
						} while ((branchingObj == null) && (nextBranching != null));
						if (branchingObj != null) {
							ctx = searchSolver.pushTrace();
							ctx.setBranching(currentBranching);
							ctx.setBranchingObject(branchingObj);
							ctx.setBranchIndex(currentBranching.getFirstBranch(ctx.getBranchingObject()));
							searchSolver.nextMove = AbstractGlobalSearchSolver.DOWN_BRANCH;
						} else {
							searchSolver.recordSolution();
							searchSolver.nextMove = AbstractGlobalSearchSolver.UP_BRANCH;
							stop = true;
						}
					} catch (ContradictionException e) {
						searchSolver.nextMove = AbstractGlobalSearchSolver.UP_BRANCH;
					}
					break;
				}
				case AbstractGlobalSearchSolver.UP_BRANCH: {
					if (searchSolver.currentTraceIndex < 0) {
						stop = true;
					} else {
						try {
							searchSolver.problem.worldPop();
							//searchSolver.problem.propagate();
							searchSolver.endTreeNode();
							searchSolver.postDynamicCut();
							ctx.getBranching().goUpBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
							searchSolver.problem.propagate();
							Solver.flushLogs();
							if (!ctx.getBranching().finishedBranching(ctx.getBranchingObject(), ctx.getBranchIndex())) {
								ctx.setBranchIndex(ctx.getBranching().getNextBranch(ctx.getBranchingObject(), ctx.getBranchIndex()));
								searchSolver.nextMove = AbstractGlobalSearchSolver.DOWN_BRANCH;
							} else {
								ctx = searchSolver.popTrace();
								searchSolver.nextMove = AbstractGlobalSearchSolver.UP_BRANCH;
							}
						} catch (ContradictionException e) {
							ctx = searchSolver.popTrace();
							searchSolver.nextMove = AbstractGlobalSearchSolver.UP_BRANCH;
						}
					}
					break;
				}
				case AbstractGlobalSearchSolver.DOWN_BRANCH: {
					try {
						searchSolver.problem.getEnvironment().worldPush();
						ctx.getBranching().goDownBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
						searchSolver.problem.propagate();
						searchSolver.nextMove = AbstractGlobalSearchSolver.OPEN_NODE;
					} catch (ContradictionException e) {
						searchSolver.nextMove = AbstractGlobalSearchSolver.UP_BRANCH;
					}
					break;
				}
			}
		}
		for (int i = 0; i < searchSolver.limits.size(); i++) {
			AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) searchSolver.limits.get(i);
			lim.reset(false);
		}
		if (searchSolver.nbSolutions > previousNbSolutions) {
			return Boolean.TRUE;
		} else if (searchSolver.isEncounteredLimit()) {
			return null;
		} else {
			return Boolean.FALSE;
		}
	}


}
