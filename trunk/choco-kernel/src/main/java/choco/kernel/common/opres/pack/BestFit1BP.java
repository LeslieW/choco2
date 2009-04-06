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

import java.util.ListIterator;

/**
 * The classical Best Fit 1BP heuristic.
 * @author Arnaud Malapert
 *
 */
public class BestFit1BP extends AbstractHeurisic1BP {

	public BestFit1BP(final int[] sizes, final int capacity) {
		super(sizes, capacity);
	}

	public BestFit1BP(final int[] sizes, final int capacity, final int mode) {
		super(sizes, capacity, mode);
	}

	/**
	 * @see choco.kernel.common.opres.pack.AbstractHeurisic1BP#extract(int)
	 */
	@Override
	public Bin extract(final int item) {
		final ListIterator<Bin> iter=available.listIterator();
		Bin selected=new Bin(capacity);
		while(iter.hasNext()) {
			final Bin b=iter.next();
			if(b.remainingArea<selected.remainingArea &&
					b.isPackable(sizes[item])) {
				iter.remove();
				if(selected.remainingArea<capacity) {
					iter.add(selected);
				}
				if(b.fit(sizes[item])) {return b;}
				else {selected=b;}
			}
		}
		return selected;
	}

}
