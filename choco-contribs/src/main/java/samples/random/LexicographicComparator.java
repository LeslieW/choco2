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
package samples.random;

import java.util.Comparator;

/**
 * A comparator that allows to put arrays of integers in a lexicographic order.
 */
public class LexicographicComparator implements Comparator<int[]> {
	public int compare(int[] t1, int[] t2) {
		for (int i = 0; i < t1.length; i++) {
			if (t1[i] < t2[i])
				return -1;
			if (t1[i] > t2[i])
				return +1;
		}
		return 0;
	}
}
