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
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;


public class SearchLoop3 extends AbstractSearchLoopWithRestart {


	public SearchLoop3(AbstractGlobalSearchStrategy searchStrategy) {
		super(searchStrategy);
	}


	public void openNode() {
		try {
			searchStrategy.newTreeNode();
			doOpenNode();
		} catch (ContradictionException e) {
			searchStrategy.nextMove = e.getContradictionMove();
		}
	}

	
	@Override
	protected void doUpBranch() {
		try {
			searchStrategy.solver.worldPop();
			searchStrategy.endTreeNode();
			goUpBranch();
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


	public void downBranch() {
		try {
			searchStrategy.solver.getEnvironment().worldPush();
			goDownBranch();
		} catch (ContradictionException e) {
			searchStrategy.nextMove = e.getContradictionMove();
		}
	}

}
