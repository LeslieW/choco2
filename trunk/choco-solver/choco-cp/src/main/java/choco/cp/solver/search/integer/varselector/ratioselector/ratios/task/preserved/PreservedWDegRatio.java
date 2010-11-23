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
package choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved;

import choco.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.AbstractPrecedenceRatio;

public class PreservedWDegRatio extends AbstractPrecedenceRatio {
	
	protected final static int DIVISOR = 1 << 12;
	
	public PreservedWDegRatio(ITemporalSRelation precedence) {
		super(precedence);
	}

	@Override
	public int initializeDivisor() {
		return DomWDegUtils.computeWeightedDegreeFromScratch(getIntVar());
	}

	@Override
	public final int initializeDividend() {
		return (int) Math.floor(ITemporalSRelation.PRESERVED_PRECISION * (precedence.getBackwardPreserved() + precedence.getForwardPreserved()));
	}
	
}
