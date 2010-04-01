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

import choco.kernel.solver.variables.integer.IntDomainVar;


public final class SimpleRatio extends AbstractRatio {

	public int dividend, divisor;

	public SimpleRatio() {
		super();
	}

	public SimpleRatio(int dividend, int divisor) {
		super();
		setDividend(dividend);
		setDivisor(divisor);
	}

	@Override
	public IntDomainVar getIntVar() {
		return null;
	}
	
	@Override
	public boolean isActive() {
		return true;
	}

	public final int getDividend() {
		return dividend;
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
		setDividend(ratio.getDividend());
		setDivisor(ratio.getDivisor());
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
}