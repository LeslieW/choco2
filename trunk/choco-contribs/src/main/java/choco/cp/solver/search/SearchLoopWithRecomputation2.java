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

import gnu.trove.TIntStack;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;


public class SearchLoopWithRecomputation2 extends AbstractSearchLoopWithRestart {

	private final int gap =10;

	private int cpt = 0;

	private int lastSavedTraceIndex = 0;

	private final TIntStack savedTraceIndex;

	public SearchLoopWithRecomputation2(AbstractGlobalSearchStrategy searchStrategy) {
		super(searchStrategy);
		savedTraceIndex = new TIntStack(searchStrategy.solver.getNbIntVars());
	}

	


	@Override
	public void initialize() {
		super.initialize();
		savedTraceIndex.reset();
		lastSavedTraceIndex = 0;
		savedTraceIndex.push(lastSavedTraceIndex);
		searchStrategy.solver.worldPush();
	}




	@Override
	protected void worldPop() {
		cpt--;
		//should we pop the delegated environment
		searchStrategy.solver.worldPop();
		if(searchStrategy.getCurrentTraceIndex() == lastSavedTraceIndex) {
			savedTraceIndex.pop();
			lastSavedTraceIndex = savedTraceIndex.peek();
			searchStrategy.solver.worldPop();
		}
		searchStrategy.solver.worldPush();
	}

	@Override
	protected void goUpBranch() throws ContradictionException {
		searchStrategy.postDynamicCut();
		LOGGER.finest("recomputation ...");
		for (int i = lastSavedTraceIndex; i < searchStrategy.getCurrentTraceIndex() ; i++) {
			ctx = searchStrategy.getTrace(i);
			ctx.getBranching().goDownBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
		}
		ctx = searchStrategy.topTrace();
		LOGGER.finest("backtrack ...");
		//FIXME should also store previous up branches ! 
		ctx.getBranching().goUpBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
		LOGGER.finest("continue ...");
		searchStrategy.solver.propagate();
	}

	@Override
	protected void worldPush() {
		if( cpt < gap) {
			cpt++;
		} else {
			searchStrategy.solver.worldPush();
			lastSavedTraceIndex = searchStrategy.getCurrentTraceIndex();
			savedTraceIndex.push(lastSavedTraceIndex);
			cpt = 0;

		}

	}


}
