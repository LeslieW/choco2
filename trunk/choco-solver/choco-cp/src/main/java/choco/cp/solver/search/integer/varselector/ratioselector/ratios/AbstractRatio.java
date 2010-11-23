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

	private int dividend, divisor;

	public AbstractRatio() {
		super();
	}

	public final int initailizeDividend() {
		return dividend;
	}

	public final void setDividend(int dividend) {
		assert dividend >= 0;
		this.dividend = dividend;
	}

	public final int getDivisor() {
		return divisor;
	}

	public final void setDivisor(int divisor) {
		assert divisor >= 0;
		this.divisor = divisor;
	}

	protected abstract int initializeDividend();
	
	protected abstract int initializeDivisor();
	
	@Override
	public boolean isActive() {
		if( getIntVar().isInstantiated()) return false;
		else {
			dividend = initializeDividend();
			divisor = initializeDivisor();
			return true;
		}
	}

	public final void setMaxRatioValue() {
		this.dividend= 1;
		this.divisor=0;
	}

	public final void setZeroRatioValue() {
		this.dividend= 0;
		this.divisor=1;
	}

	public final void setRatio(IntRatio ratio) {
		setDividend(ratio.initailizeDividend());
		setDivisor(ratio.getDivisor());
	}


	@Override
	public String toString() {
		return (getIntVar() == null ? "" :getIntVar().toString()+"->")
		+initailizeDividend()+"/"+getDivisor();
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
		return  ( (long) initailizeDividend() ) * ratio.getDivisor();
	}

	public final long getRightMember(IntRatio ratio) {
		return ( (long) ratio.initailizeDividend() ) * getDivisor();
	}

}
