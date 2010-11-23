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

import choco.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.AbstractRatio;
import choco.kernel.solver.variables.integer.IntDomainVar;

public abstract class AbstractPrecedenceRatio extends AbstractRatio implements ITemporalRatio {
	
	public final ITemporalSRelation precedence;
		
	public AbstractPrecedenceRatio(ITemporalSRelation precedence) {
		super();
		this.precedence = precedence;
	}
	
	@Override
	public final ITemporalSRelation getTemporalRelation() {
		return precedence;
	}

	@Override
	public final IntDomainVar getIntVar() {
		return  precedence.getDirection();
	}
		
}
