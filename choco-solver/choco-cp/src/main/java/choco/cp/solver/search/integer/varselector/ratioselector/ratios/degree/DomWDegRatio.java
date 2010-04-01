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
package choco.cp.solver.search.integer.varselector.ratioselector.ratios.degree;

import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.computeWeightedDegreeFromScratch;
import choco.kernel.solver.variables.integer.IntDomainVar;

public final class DomWDegRatio extends DomDegRatio {

	public DomWDegRatio(IntDomainVar var) {
		super(var);
	}

	@Override
	public int getDivisor() {
		return computeWeightedDegreeFromScratch(var);
	}
	
	
}
