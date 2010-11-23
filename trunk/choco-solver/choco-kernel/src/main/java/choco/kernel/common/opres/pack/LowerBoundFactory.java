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

package choco.kernel.common.opres.pack;

import gnu.trove.TIntArrayList;

/**
 * @author Arnaud Malapert
 *
 */
public final class LowerBoundFactory {

	/**
	 * empty constructor
	 */
	private LowerBoundFactory() {}

	/**
	 * Compute L_{DFF}^{1BP} as defined in the phd thesis of F. Clautiaux
	 *
	 * @param sizes the sizes of the items
	 * @param capacity the capacity of the bin
	 *
	 * @return a Lower bound on the number of bins
	 */
	public static boolean  consistencyTestLDFF(TIntArrayList sizes,final int capacity, int threshold) {
		sizes.sort();
		final int ub = (new BestFit1BP(capacity)).computeUB(sizes);
		if (ub > threshold) {
			//the heuristics solution is greater than the threshold
			//so, what about the lower bound ?
			return computeL_DFF(sizes, capacity, ub) <= threshold;
		}//otherwise, best fit gives a number of bins lower than the threshold
		return true;
	}


/**
 * Compute L_{DFF}^{1BP} as defined in the phd thesis of F. Clautiaux
 *
 * @param sizes the sizes of the items
 * @param capacity the capacity of the bin
 * @param ub an upper bound
 * @return a Lower bound on the number of bins
 */
public static int  computeL_DFF_1BP(final int[] sizes,final int capacity,final int ub) {
	final TIntArrayList items = new TIntArrayList(sizes);
	items.sort();
	return computeL_DFF(items, capacity, ub);
}

public static int  computeL_DFF(final TIntArrayList sizes,final int capacity,final int ub) {
	PackDDFF ddffs = new PackDDFF(capacity);
	ddffs.setItems(sizes);
	ddffs.setUB(ub);
	return ddffs.computeDDFF();
}


}
