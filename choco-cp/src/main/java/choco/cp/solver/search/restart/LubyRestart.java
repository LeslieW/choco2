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

	public final static int pow(int value,int exp){
		return value==2 ? 1 << exp : (int) Math.pow(value, exp);
	}

	public final static double log(double value,double exponent){
		return Math.log(value)/Math.log(exponent);
	}


	public static final int geometricalSum(int value, int exponent) {
		return  ( pow(value,exponent)-1 ) / ( value -1 );
	}
//	public final static int getLasVegasCoef(int i,int factor) {
//		double log = log(i+1,factor);
//		int k = (int) Math.floor(log);
//		if(log == k) {
//			// i = factor^k -1
//			return pow(factor,k-1);
//		}else {
//			//recursion
//			return getLasVegasCoef(i - pow(factor,k)+1,factor);
//		}
//	}


	public final int getLasVegasCoef(int i) {
		double log = log( i * divFactor + 1,geometricalIntFactor);
		int k = (int) Math.floor(log);
		if(log == k) {
			// i = factor^k -1
			return pow(geometricalIntFactor,k-1);
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