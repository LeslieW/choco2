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

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.recomputation.EnvironmentRecomputation;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;


public class SearchLoopWithRecomputation2 extends AbstractSearchLoopWithRestart {

	protected EnvironmentRecomputation env;

	public SearchLoopWithRecomputation2(AbstractGlobalSearchStrategy searchStrategy) {
		super(searchStrategy);
	}

	@Override
	public void initialize() {
		final IEnvironment e = searchStrategy.solver.getEnvironment();
		if (e instanceof EnvironmentRecomputation) {
			env = (EnvironmentRecomputation) e;
		}else {
			throw new SolverException("environment "+e.getClass().getSimpleName() +" incompatible with the search loop ");
		}
		super.initialize();
	}



	public void openNode() {
		try {
			searchStrategy.solver.propagate();
			searchStrategy.newTreeNode();
			doOpenNode();
		} catch (ContradictionException e) {
			searchStrategy.nextMove = e.getContradictionMove();
			env.setLastFail(env.getWorldIndex());
		}
	}


	@Override
	protected void doUpBranch() {
		try {
			 env.incNbFail();
			 searchStrategy.solver.worldPop();
			searchStrategy.endTreeNode();
			goUpBranch();
			env.pushContext(ctx,false);
			if (!ctx.getBranching().finishedBranching(ctx.getBranchingObject(), ctx.getBranchIndex())) {
				ctx.setBranchIndex(ctx.getBranching().getNextBranch(ctx.getBranchingObject(), ctx.getBranchIndex()));
				searchStrategy.nextMove = AbstractGlobalSearchStrategy.DOWN_BRANCH;
			} else {
				 env.popContext(ctx);
				 ctx = searchStrategy.popTrace();
				//env.pushContext(ctx,false);
				searchStrategy.nextMove = AbstractGlobalSearchStrategy.UP_BRANCH;
			}
		} catch (ContradictionException e) {
			 env.popContext(ctx);
			ctx = searchStrategy.popTrace();
			searchStrategy.nextMove = e.getContradictionMove();
			env.setLastFail(env.getWorldIndex());
		}
	}


	public void downBranch() {
		try {
			env.worldPush();
			env.pushContext(ctx, true);
			searchStrategy.solver.propagate();
			goDownBranch();
		} catch (ContradictionException e) {
			searchStrategy.nextMove = e.getContradictionMove();
			env.setLastFail(env.getWorldIndex());
		}
	}

}
