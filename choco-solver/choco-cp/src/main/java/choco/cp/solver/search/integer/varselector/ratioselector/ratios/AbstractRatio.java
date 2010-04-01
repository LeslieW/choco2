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
package choco.cp.solver.search.integer.varselector.ratioselector.ratios;



public abstract class AbstractRatio implements IntRatio {

	public AbstractRatio() {
		super();
	}

	@Override
	public boolean isActive() {
		return ! getIntVar().isInstantiated();
	}

	@Override
	public String toString() {
		return getIntVar().toString();
	}

	@Override
	public final int compareTo(IntRatio o) {
		final long a = getLeftMember(o);
		final long b = getRightMember(o);
		if(a > b) return 1;
		else if( a < b) return -1;
		else return 0;
	}
	
	public final long getLeftMember(IntRatio ratio) {
		return  ( (long) getDividend() ) * ratio.getDivisor();
	}

	public final long getRightMember(IntRatio ratio) {
		return ( (long) ratio.getDividend() ) * getDivisor();
	}

}
