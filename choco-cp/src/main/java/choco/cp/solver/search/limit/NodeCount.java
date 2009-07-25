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

public class NodeCount extends AbstractGlobalSearchLimit {

	
	public NodeCount(AbstractGlobalSearchStrategy theStrategy) {
		this(theStrategy, Integer.MAX_VALUE);
	}
	
	protected NodeCount(AbstractGlobalSearchStrategy theStrategy, int theLimit) {
		super(theStrategy, theLimit,Limit.NODE);
		limitMask = NEW_NODE;
	}

	@Override
	public void newNode() throws ContradictionException {
		nb++;
	}
	
	@Override
	public final void endNode() throws ContradictionException {}

		
}



