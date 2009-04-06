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

/**
 * various mathematics utilities. The functions do not exist in the basic math package Math.*
 * @author Arnaud Malapert</br>
 * @since 8 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 */
public final class MathUtil {

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

}
