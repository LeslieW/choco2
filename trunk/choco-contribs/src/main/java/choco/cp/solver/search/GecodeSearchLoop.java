/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.cp.solver.search;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractIntBranching;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 18 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class GecodeSearchLoop extends SearchLoop {

    static int MAX_STEP;
    private int currentstep;
    private AbstractIntBranching nextBranching;

    public GecodeSearchLoop(AbstractGlobalSearchStrategy searchStrategy) {
        super(searchStrategy);
        MAX_STEP = 10;
    }

    public GecodeSearchLoop(AbstractGlobalSearchStrategy searchStrategy, int currentstep) {
        super(searchStrategy);
        MAX_STEP = currentstep;
    }

    @Override
    public void init() {
        super.init();
        currentstep = 0;
    }


    @Override
    public void openNode() {
        try {
            searchStrategy.newTreeNode();
            Object branchingObj = null;
            AbstractIntBranching currentBranching = (AbstractIntBranching) ctx.getBranching();
            nextBranching = currentBranching;
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

    @Override
    public void downBranch() {
        try {
            searchStrategy.solver.getEnvironment().worldPush();
            ctx.getBranching().goDownBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
            if(currentstep == MAX_STEP || ctx.getBranching().selectBranchingObject()==null){
                searchStrategy.solver.propagate();
                currentstep = 0;
            }else{
                currentstep++;
            }
            searchStrategy.nextMove = AbstractGlobalSearchStrategy.OPEN_NODE;
        } catch (ContradictionException e) {
            searchStrategy.nextMove = AbstractGlobalSearchStrategy.UP_BRANCH;
        }
    }
}
