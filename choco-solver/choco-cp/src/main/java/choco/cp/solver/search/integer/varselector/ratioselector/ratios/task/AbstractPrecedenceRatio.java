/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  �(..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.search.integer.varselector.ratioselector.ratios.task;

import choco.cp.solver.search.integer.varselector.ratioselector.ratios.AbstractRatio;
import choco.kernel.solver.constraints.global.scheduling.IPrecedence;
import choco.kernel.solver.variables.integer.IntDomainVar;

public abstract class AbstractPrecedenceRatio extends AbstractRatio implements IPrecedenceRatio {
	
	public final IPrecedence precedence;
		
	public AbstractPrecedenceRatio(IPrecedence precedence) {
		super();
		this.precedence = precedence;
	}
	
	@Override
	public final IPrecedence getPrecedence() {
		return precedence;
	}

	@Override
	public final IntDomainVar getIntVar() {
		return  precedence.getBoolVar();
	}
		
}
