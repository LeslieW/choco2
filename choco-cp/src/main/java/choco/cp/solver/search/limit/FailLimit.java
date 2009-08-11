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
import choco.kernel.solver.propagation.PropagationEngineListener;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.limit.Limit;

/**
 * Limit counting the fail number
 */
public final class FailLimit extends AbstractGlobalSearchLimit implements PropagationEngineListener {

	private int nb;
	
	public FailLimit(AbstractGlobalSearchStrategy theStrategy, int theLimit) {
		super(theStrategy, theLimit, Limit.FAIL);
		strategy.getSolver().getPropagationEngine().addPropagationEngineListener(this);
	}

	/**
	 * Define action to do just before a deletion.
	 */
	@Override
	public final void safeDelete() {
		strategy.getSolver().getPropagationEngine().removePropagationEngineListener(this);
	}
	

	
	@Override
	public final int getNb() {
		return nb;
	}

	public void contradictionOccured(ContradictionException e) {
		if(!e.isSearchLimitCause()) {nb++;}
	}
}
