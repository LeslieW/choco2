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
package choco.cp.solver.search.limit;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.limit.Limit;

/**
 * compute the total amount of time (ms).
 *
 */
public class TimeCount extends AbstractGlobalSearchLimit {


	private long starth;
		
	
	public TimeCount(AbstractGlobalSearchStrategy theStrategy) {
		this(theStrategy, Integer.MAX_VALUE);
	}
	
	protected TimeCount(AbstractGlobalSearchStrategy theStrategy, int theLimit) {
		super(theStrategy, theLimit, Limit.TIME);
	}

	
	@Override
	public final void initialize() {
		super.initialize();
		starth = TimeCacheThread.currentTimeMillis;
	}


	@Override
	public final void reset() {
		super.reset();
		starth = TimeCacheThread.currentTimeMillis;
		
	}


	@Override
	public void endNode() throws ContradictionException {
		nb =  (int) (TimeCacheThread.currentTimeMillis - starth);
	}

	@Override
	public void newNode() throws ContradictionException {
		nb =  (int) (TimeCacheThread.currentTimeMillis - starth);
	}
	
}

