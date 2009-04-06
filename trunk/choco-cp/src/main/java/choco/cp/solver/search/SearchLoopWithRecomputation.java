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

import choco.kernel.memory.recomputation.EnvironmentRecomputation;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractIntBranching;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.IntBranchingTrace;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 16 juil. 2007
 * Time: 09:48:05
 */
public class SearchLoopWithRecomputation extends SearchLoop {

    private EnvironmentRecomputation env = null;


    public SearchLoopWithRecomputation(AbstractGlobalSearchStrategy searchStrategy) {
        super(searchStrategy);
    }


    @Override
    public Boolean run() {
        env = (EnvironmentRecomputation) searchStrategy.solver.getEnvironment();
        return super.run();
    }


    @Override
    public final void init() {
        // specific initialization for the very first solution search (start from the tree root, instead of last leaf)
        if (searchStrategy.nextMove == AbstractGlobalSearchStrategy.INIT_SEARCH) {
            searchStrategy.nextMove = AbstractGlobalSearchStrategy.OPEN_NODE;
            ctx = new IntBranchingTrace(searchStrategy.mainGoal);
        } else {
            ctx = searchStrategy.topTrace();
        }
    }

    @Override
    public void openNode() {
        try {
            searchStrategy.solver.propagate();
            searchStrategy.newTreeNode();
            Object branchingObj = null;
            AbstractIntBranching currentBranching = (AbstractIntBranching) ctx.getBranching();
            AbstractIntBranching nextBranching = currentBranching;

            do {
                currentBranching = nextBranching;
                branchingObj = currentBranching.selectBranchingObject();
                nextBranching = (AbstractIntBranching) currentBranching.getNextBranching();
            } while ((branchingObj == null) && (nextBranching != null));
            if (branchingObj != null) {
                ctx = searchStrategy.pushTrace();
                ctx.setBranching(currentBranching);
                ctx.setBranchingObject(branchingObj);
                ctx.setBranchIndex(currentBranching.getFirstBranch(ctx.getBranchingObject()));
                searchStrategy.nextMove = AbstractGlobalSearchStrategy.DOWN_BRANCH;
            } else {
                searchStrategy.recordSolution();
                searchStrategy.nextMove = AbstractGlobalSearchStrategy.UP_BRANCH;
                stop = true;
            }
        } catch (ContradictionException e) {
            searchStrategy.nextMove = AbstractGlobalSearchStrategy.UP_BRANCH;
            env.setLastFail(env.getWorldIndex());
        }
    }

    @Override
    public void upBranch() {
        if (searchStrategy.currentTraceIndex < 0) {
            stop = true;
        } else {
            try {

                env.incNbFail();

                searchStrategy.solver.worldPop();
                //searchStrategy.model.propagate();
                searchStrategy.endTreeNode();
                searchStrategy.postDynamicCut();
                //env.pushContext(ctx,false);

                ctx.getBranching().goUpBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
                searchStrategy.solver.propagate();
                //CPSolver.flushLogs();
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
                searchStrategy.nextMove = AbstractGlobalSearchStrategy.UP_BRANCH;
                env.setLastFail(env.getWorldIndex());
            }
        }
    }

    @Override
    public void downBranch() {
        try {
            env.pushContext(ctx, true);
            env.worldPush();
            searchStrategy.solver.propagate();
            ctx.getBranching().goDownBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
            searchStrategy.solver.propagate();
            searchStrategy.nextMove = AbstractGlobalSearchStrategy.OPEN_NODE;
        } catch (ContradictionException e) {
            searchStrategy.nextMove = AbstractGlobalSearchStrategy.UP_BRANCH;
            env.setLastFail(env.getWorldIndex());
        }
    }
}
