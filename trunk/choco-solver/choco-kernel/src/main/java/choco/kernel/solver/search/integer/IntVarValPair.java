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
package choco.kernel.solver.search.integer;

import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * a struct-like class implementing (IntVar, int) pairs
 * (useful for binary branchings that assign a value to a variable or remove
 * that same value from the variable domain
 */
public final class IntVarValPair {
	public final IntDomainVar var;
	public final int val;

	public IntVarValPair(IntDomainVar var, int val) {
		this.var = var;
		this.val = val;
	}

	public String pretty() {
		return "(" + var + "," + val + ")";
	}

	@Override
	public String toString() {
		return "(" + var + "," + val + ")";
	}

}
