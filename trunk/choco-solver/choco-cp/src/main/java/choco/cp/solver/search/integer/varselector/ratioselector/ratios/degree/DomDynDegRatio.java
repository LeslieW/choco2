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

import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.getFineDegree;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.hasAtLeastTwoNotInstVars;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

public final class DomDynDegRatio extends DomDegRatio {

	public DomDynDegRatio(IntDomainVar var) {
		super(var);
	}

	@Override
	public int initializeDivisor() {
		int ddeg = 0;
		DisposableIntIterator it = var.getIndexVector().getIndexIterator();
		while (it.hasNext()) {
			final int cIdx = it.next();
			final SConstraint<?> ct = var.getConstraint(cIdx);
			if ( hasAtLeastTwoNotInstVars(ct)) {
				ddeg+= getFineDegree(var, ct, cIdx);
			}
		}
		return ddeg;
	}
	
	

}
