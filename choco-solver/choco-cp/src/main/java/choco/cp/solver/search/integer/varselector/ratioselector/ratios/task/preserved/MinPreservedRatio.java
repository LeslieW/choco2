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
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.AbstractPrecedenceRatio;

/**
 * Must handle properly integer overflow.
 * @author Arnaud Malapert</br> 
 * @since 26 mars 2010 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public final class MinPreservedRatio extends AbstractPrecedenceRatio {
	
	public MinPreservedRatio(ITemporalSRelation precedence) {
		super(precedence);
	}

	@Override
	public int initializeDividend() {
		return (int) Math.floor(ITemporalSRelation.PRESERVED_PRECISION * Math.min(precedence.getBackwardPreserved(),precedence.getForwardPreserved()));
	}

	@Override
	protected int initializeDivisor() {
		return 1;
	}
	
	

}