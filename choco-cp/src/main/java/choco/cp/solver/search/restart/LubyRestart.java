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
package choco.cp.solver.search.restart;

import java.math.BigDecimal;

import choco.kernel.common.util.MathUtil;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.search.Limit;


/**
 * @author Arnaud Malapert</br> 
 * @since version 2.0.0</br>
 */
public class LubyRestart extends GeometricalRestart {


	private int geometricalIntFactor;

	private int divFactor;

	public LubyRestart(Limit type, int scaleFactor) {
		super(type, 2, scaleFactor);
	}

	public LubyRestart(Limit type, int geometricalFactor, int scaleFactor) {
		super(type, geometricalFactor, scaleFactor);
	}

	@Override
	public final void setGeometricalFactor(double geometricalFactor) {
		double f= Math.floor(geometricalFactor);
		if(f!=geometricalFactor) {throw new SolverException("Luby geometrical parameter should be an integer");}
		super.setGeometricalFactor(geometricalFactor);
		geometricalIntFactor = (int) geometricalFactor;
		divFactor = geometricalIntFactor - 1;
	}

	public static final int geometricalSum(int value, int exponent) {
		return  ( MathUtil.pow(value,exponent)-1 ) / ( value -1 );
	}
	

	public final int getLasVegasCoef(int i) {
		//<hca> I round it to PRECISION because of issues between versions of the jvm on mac and pc
		final double log = MathUtil.roundedLog( i * divFactor + 1,geometricalIntFactor);
		final int k = (int) Math.floor(log);
		if(log == k) {
			return MathUtil.pow(geometricalIntFactor,k-1);
		}else {
			//recursion
			return getLasVegasCoef(i - geometricalSum(geometricalIntFactor, k));
		}
	}

	@Override
	protected int getNextLimit() {
		return getLasVegasCoef(nbRestarts +1)*scaleFactor;
	}
	
}