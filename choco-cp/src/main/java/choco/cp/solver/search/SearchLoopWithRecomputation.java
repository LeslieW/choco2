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

import java.util.Stack;

import choco.cp.solver.search.restart.IKickRestart;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.IntBranchingTrace;


public class SearchLoopWithRecomputation extends AbstractSearchLoopWithRestart {

	public final int gap;

	private int cpt = 0;

	private int lastSavedTraceIndex = 0;

	private final TIntStack savedTraceIndex;

    private final Stack<IntBranchingTrace> contexts;

    private final TIntStack ctxIndices;


    public SearchLoopWithRecomputation(AbstractGlobalSearchStrategy searchStrategy, IKickRestart kickRestart, int gap) {
		super(searchStrategy, kickRestart);
        this.gap = gap;
        final int n = searchStrategy.solver.getNbIntVars();
        savedTraceIndex = new TIntStack(n);
        contexts = new Stack<IntBranchingTrace>();
        ctxIndices = new TIntStack(n);
	}


	public final int getGap() {
		return gap;
	}

	@Override
	public void initialize() {
		super.initialize();
		savedTraceIndex.reset();
		lastSavedTraceIndex = 0;
        savedTraceIndex.push(lastSavedTraceIndex);
        ctxIndices.push(contexts.size());
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
        int ind = ctxIndices.pop();
        while(contexts.size()>ind){
            contexts.pop();
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
        int ind = ctxIndices.peek();
        for(int i = ind; i < contexts.size(); i++){
            IntBranchingTrace context = contexts.get(i);
            ctx.getBranching().goUpBranch(context.getBranchingObject(), context.getBranchIndex());
        }

		ctx.getBranching().goUpBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
		LOGGER.finest("continue ...");
		searchStrategy.solver.propagate();
        contexts.push(ctx.copy());
	}

	@Override
	protected void worldPush() {
		if( cpt % gap == 0) {
			searchStrategy.solver.worldPush();
			lastSavedTraceIndex = searchStrategy.getCurrentTraceIndex();
			savedTraceIndex.push(lastSavedTraceIndex);
		}
        ctxIndices.push(contexts.size());
        cpt++;
	}

}
