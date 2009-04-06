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

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractBranching;
import choco.kernel.solver.branch.AbstractIntBranching;
import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.ISearchLoop;
import choco.kernel.solver.search.IntBranchingTrace;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 16 juil. 2007
 * Time: 09:48:05
 */
public class SearchLoop implements ISearchLoop {

    protected AbstractGlobalSearchStrategy searchStrategy;
    protected IntBranchingTrace ctx = null;
    protected boolean stop = false;


    public SearchLoop(AbstractGlobalSearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }


    public Boolean run() {

        int previousNbSolutions = searchStrategy.nbSolutions;
        searchStrategy.setEncounteredLimit(null);
        ctx = null;
        stop = false;

        init();
        while (!stop) {
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
            }
        }
        for (int i = 0; i < searchStrategy.limits.size(); i++) {
            AbstractGlobalSearchLimit lim = searchStrategy.limits.get(i);
            lim.reset(false);
        }
        if (searchStrategy.nbSolutions > previousNbSolutions) {
            return Boolean.TRUE;
        } else if (searchStrategy.isEncounteredLimit()) {
            return null;
        } else {
            return Boolean.FALSE;
        }
    }

    public void init() {
        // specific initialization for the very first solution search (start from the tree root, instead of last leaf)
        if (searchStrategy.nextMove == AbstractGlobalSearchStrategy.INIT_SEARCH) {
            searchStrategy.nextMove = AbstractGlobalSearchStrategy.OPEN_NODE;
            ctx = new IntBranchingTrace(searchStrategy.mainGoal);
            AbstractBranching b = searchStrategy.mainGoal;
            while (b != null) {
                b.initBranching();
                b = b.getNextBranching();
            }
        } else {
            ctx = searchStrategy.topTrace();
        }
    }

    public void openNode() {
        try {
            searchStrategy.newTreeNode();
            Object branchingObj = null;
            AbstractIntBranching currentBranching = (AbstractIntBranching) ctx.getBranching();
            AbstractIntBranching nextBranching = currentBranching;
            while ((branchingObj == null) && (nextBranching != null)) {
                currentBranching = nextBranching;
                branchingObj = currentBranching.selectBranchingObject();
                nextBranching = (AbstractIntBranching) currentBranching.getNextBranching();
            }
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
        }
    }

    public void upBranch() {
        if (searchStrategy.currentTraceIndex < 0) {
            stop = true;
        } else {
            try {
                searchStrategy.solver.worldPop();
                //searchStrategy.model.propagate();
                searchStrategy.endTreeNode();
                searchStrategy.postDynamicCut();
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
            searchStrategy.nextMove = AbstractGlobalSearchStrategy.UP_BRANCH;
        }
    }
}
