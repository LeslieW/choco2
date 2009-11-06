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
package choco.cp.solver.constraints.integer.extension;

import choco.kernel.solver.variables.integer.IntDomainVar;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Jul 31, 2008
 * Since : Choco 2.0.0
 *
 */
public final class FastValidityChecker extends ValidityChecker {

	public FastValidityChecker(int arity, IntDomainVar[] vars) {
		super(arity, vars);
	}

	// Is tuple valide ?
	public final boolean isValid(final int[] tuple) {
        nbCheck++;
        for (int i = 0; i < arity; i++)
			if (!sortedvs[i].getDomain().contains(tuple[position[i]]))
				return false;
		return true;
	}

    public boolean isValid(int[] tuple, int i) {
        nbCheck++;
		return sortedvs[i].getDomain().contains(tuple[position[i]]);
	}

}
