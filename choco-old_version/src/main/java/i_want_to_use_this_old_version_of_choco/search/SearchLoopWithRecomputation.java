package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;
import i_want_to_use_this_old_version_of_choco.mem.recomputation.EnvironmentRecomputation;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 16 juil. 2007
 * Time: 09:48:05
 */
public class SearchLoopWithRecomputation implements ISearchLoop {

    AbstractGlobalSearchSolver searchSolver;



    public SearchLoopWithRecomputation(AbstractGlobalSearchSolver searchSolver) {
        this.searchSolver = searchSolver;
    }


    public Boolean run() {

        int previousNbSolutions = searchSolver.nbSolutions;
        searchSolver.encounteredLimit = null;
        IntBranchingTrace ctx = null;
        boolean stop = false;
        EnvironmentRecomputation env = (EnvironmentRecomputation) searchSolver.problem.getEnvironment();

        // specific initialization for the very first solution search (start from the tree root, instead of last leaf)
        if (searchSolver.nextMove == AbstractGlobalSearchSolver.INIT_SEARCH) {
            searchSolver.nextMove = AbstractGlobalSearchSolver.OPEN_NODE;
            ctx = new IntBranchingTrace(searchSolver.mainGoal);
        } else {
            ctx = searchSolver.topTrace();
        }
        while (!stop) {
            switch (searchSolver.nextMove) {
                case AbstractGlobalSearchSolver.OPEN_NODE:
                {
                    try {
                        searchSolver.problem.propagate();
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
                        env.setLastFail(env.getWorldIndex());
                    }
                    break;
                }
                case AbstractGlobalSearchSolver.UP_BRANCH:
                {
                    if (searchSolver.currentTraceIndex < 0) {
                        stop = true;
                    } else {
                        try {

                            env.incNbFail();

                            searchSolver.problem.worldPop();
                            //searchSolver.problem.propagate();
                            searchSolver.endTreeNode();
                            searchSolver.postDynamicCut();
                            //env.pushContext(ctx,false);

                            ctx.getBranching().goUpBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
                            searchSolver.problem.propagate();
                            Solver.flushLogs();
                            if (!ctx.getBranching().finishedBranching(ctx.getBranchingObject(), ctx.getBranchIndex())) {
                                ctx.setBranchIndex(ctx.getBranching().getNextBranch(ctx.getBranchingObject(), ctx.getBranchIndex()));

                                searchSolver.nextMove = AbstractGlobalSearchSolver.DOWN_BRANCH;
                            } else {
                                ctx = searchSolver.popTrace();
                                //env.pushContext(ctx,false);
                                searchSolver.nextMove = AbstractGlobalSearchSolver.UP_BRANCH;
                            }
                        } catch (ContradictionException e) {
                            ctx = searchSolver.popTrace();
                            searchSolver.nextMove = AbstractGlobalSearchSolver.UP_BRANCH;
                            env.setLastFail(env.getWorldIndex());
                        }
                    }
                    break;
                }
                case AbstractGlobalSearchSolver.DOWN_BRANCH:
                {
                    try {

                        env.pushContext(ctx,true);
                        

                        env.worldPush();

                        searchSolver.problem.propagate();
                        ctx.getBranching().goDownBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
                        searchSolver.problem.propagate();

                        searchSolver.nextMove = AbstractGlobalSearchSolver.OPEN_NODE;
                    } catch (ContradictionException e) {
                        searchSolver.nextMove = AbstractGlobalSearchSolver.UP_BRANCH;
                        env.setLastFail(env.getWorldIndex());
                    }
                    break;
                }
            }
        }
        for (Object limit : searchSolver.limits) {
            AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limit;
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

