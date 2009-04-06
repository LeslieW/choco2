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

import java.util.BitSet;

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
	 * Creates the DDFF objects.
	 *
	 * @param sizes the sizes of the item
	 * @param capacity the capacity of the bins
	 * @param selected the selected items
	 *
	 * @return the abstract DDF f[]
	 */
	protected static AbstractDDFF[] createDDFF(final int[] sizes,final int capacity,final BitSet selected) {
		return new AbstractDDFF[]{new FunctionDFF_f0(sizes,capacity,selected),new FunctionDDFF_f1(sizes,capacity,selected),new FunctionDFF_f2(sizes,capacity,selected)};
	}

	/**
	 * Compute L_{DFF}^{1BP} as defined in the phd thesis of F. Clautiaux
	 *
	 * @param sizes the sizes of the items
	 * @param capacity the capacity of the bin
	 *
	 * @return a Lower bound on the number of bins
	 */
	public static int  computeL_DFF_1BP(final int[] sizes,final int capacity) {
		final AbstractDDFF[] ddff=createDDFF(sizes, capacity,null);
		return LowerBoundFactory.computeL_DFF_1BP(ddff);
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
		final AbstractDDFF[] ddff=createDDFF(sizes, capacity,null);
		return LowerBoundFactory.computeL_DFF_1BP(ddff,ub);
	}

	/**
	 * Compute L_{DFF}^{1BP} as defined in the phd thesis of F. Clautiaux
	 *
	 * @param sizes the sizes of the items
	 * @param capacity the capacity of the bin
	 * @param selected the selected
	 *
	 * @return a Lower bound on the number of bins
	 */
	public static int  computeL_DFF_1BP(final int[] sizes,final int capacity,final BitSet selected) {
		if(sizes.length>0  && (selected==null || selected.cardinality()>0) ){
			final AbstractDDFF[] ddff=createDDFF(sizes, capacity,selected);
			return LowerBoundFactory.computeL_DFF_1BP(ddff);
		}else {return 0;}
	}

	/**
	 * Compute L_{DFF}^{1BP} for the given DFF and DDFF.
	 *
	 * @param ddff the ddff
	 *
	 * @return L_{DFF}^{1BP}
	 */
	protected static int  computeL_DFF_1BP(final AbstractDDFF[] ddff,final int ub) {
		int lbv=0;
		for (int u = 0; u < ddff.length; u++) {
			final BitSet params=ddff[u].selectParameters();
			for (int k = params.nextSetBit(0); k>=0 ;k=params.nextSetBit(k+1)) {
				final int lb= ddff[u].applyFunction(k);
				if(lb>lbv) {
					lbv=lb;
					if(lbv==ub) {return lbv;}
				}
				ddff[u].reset();

			}
		}
		return lbv;
	}

	/**
	 * Compute L_{DFF}^{1BP} for the given DFF and DDFF.
	 *
	 * @param ddff the ddff
	 *
	 * @return L_{DFF}^{1BP}
	 */
	protected static int  computeL_DFF_1BP(final AbstractDDFF[] ddff) {
		//FIXME Que se passe t'il en cas de selected ?
		return computeL_DFF_1BP(ddff, ddff[0].computeUB());
	}

	protected static BitSet intersection(final AbstractDDFF ddff1,final AbstractDDFF ddff2) {
		final BitSet r= (BitSet) ddff1.selected.clone();
		r.and(ddff2.selected);
		return r;
	}

	/**
	 * Compute 2D L0 for two DDFF.
	 *
	 * @param ddff1 the first DDFF
	 * @param ddff2 the second DDFF
	 *
	 * @return the lower bound
	 */
	public static int computeL0(final AbstractDDFF ddff1,final AbstractDDFF ddff2) {
		double lb=0;
		final BitSet inter=intersection(ddff1,ddff2);
		for (int i = inter.nextSetBit(0); i >= 0; i = inter.nextSetBit(i + 1)) {
			lb+=ddff1.sizes[i]*ddff2.sizes[i];
		}
		return AbstractDDFF.round(lb, ddff1.getCapacity()*ddff2.getCapacity());
	}


	/**
	 * Compute L_^{2CM}.
	 *
	 * @return max(L^{DFF},L_^{2CM}_2
	 */
	public static int computeL_2CM(final int[] widths,final int cwidth,final int[] heights,final int cheight) {
		return new LowerBound2BP(widths,cwidth,heights,cheight).computeL_2CM();
	}
}
