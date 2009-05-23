/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  Â°(..)  |                           *
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
package choco.kernel.common.util;

import java.awt.Point;


/**
 * various mathematics utilities. The functions do not exist in the basic math package Math.*
 * @author Arnaud Malapert</br>
 * @since 8 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 */
public final class MathUtil {

	public final static double ROUNDED_LOG_PRECISION = 10000;
	/**
	 *
	 */
	private MathUtil() {
		//do nothing
	}

	/**
	 * simple recursive version of factorielle
	 */
	public static long factoriel(int n) {
		return n<2 ? 1 : n * factoriel(n-1);
	}

	/**
	 * it computes the number of combinaison C_n^p.
	 * The function is oonly recursive and do not use an array to store temporary results
	 * @param n
	 * @param p
	 * @return
	 */
	public static int combinaison(int n,int p) {
		if(n==p) {return 1;}
		else if(p==0) {return 1;}
		else if(p==1) {return n;}
		else {return combinaison(n-1, p) + combinaison(n-1, p-1);}
	}

	public static final boolean isPowerOfTwo(int x) {
		return (x & (x - 1)) == 0;
	}

	public static int pow(int value,int exp){
		return value==2 ? 1 << exp : (int) Math.pow(value, exp);
	}

	public static double log(double value,double exponent){
		return Math.log(value)/Math.log(exponent);
	}

	/**
	 * a rounded logarithm to avoid issues with jvm dependant math functions
	 */
	public static double roundedLog(double value,double exponent){
		return Math.round( log(value, exponent) * ROUNDED_LOG_PRECISION) / ROUNDED_LOG_PRECISION;
	}
	
	public static int max(int[] values) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < values.length; i++) {
			if(values[i] > max) {max = values[i];}
		}
		return max;
	}
	
	public static int min(int[] values) {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < values.length; i++) {
			if(values[i] < min) {min = values[i];}
		}
		return min;
	}
	
	public static Point bounds(int[] values) {
		if(values == null || values.length == 0) {return new Point(Integer.MAX_VALUE, Integer.MIN_VALUE);}
		else {
			final Point b = new Point(values[0], values[0]);
			for (int i = 1; i < values.length; i++) {
			if(values[i] < b.x) {b.x= values[i];}
			else if(values[i] > b.y) {b.y= values[i];}
		}
		return b;
		}
	}

}
