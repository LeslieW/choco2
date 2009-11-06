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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class Bin represent a 1BP bin.
 */
class Bin {

	/**remaining area in the bin*/
	public int remainingArea;

	/** list of item index packed into the bin*/
	public final List<Integer> itemIndexL=new LinkedList<Integer>();

	/**
	 * @param capacity inital capacity
	 */
	public Bin(final int capacity) {
		super();
		this.remainingArea = capacity;
	}

	public boolean isPackable(final int size) {
		return size<=remainingArea;
	}

	public boolean fit(final int size) {
		return size==remainingArea;
	}


	public final void pack(final int idx,final int size) {
		remainingArea-=size;
		itemIndexL.add(Integer.valueOf(idx));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.valueOf(remainingArea);
	}


}

/**
 * The Class AbstractHeurisic1BP.
 */
public abstract class AbstractHeurisic1BP {

	/** The Constant COPY_AND_SORT. */
	public static final int COPY_AND_SORT=0;

	/** The Constant SORT. */
	public static final int SORT=1;

	/** The Constant INCREASING. */
	public static final int INCREASING=2;

	/** The Constant DECREASING. */
	public static final int DECREASING=3;

	/** The sizes of the items. */
	public final int[] sizes;

	/** The capacity of the bins. */
	public final int capacity;

	/** The available bins. */
	protected final List<Bin> available=new LinkedList<Bin>();

	/** The filled bins. */
	protected final List<Bin> filled=new LinkedList<Bin>();

	/** sort order of the sizes. */
	private final boolean increasing;

	/** The smallest item index. */
	private final int siIndex;


	/**
	 *
	 * @param sizes the sizes of the item
	 * @param capacity the capacity of the bins
	 */
	public AbstractHeurisic1BP(final int[] sizes,final int capacity) {
		this(sizes,capacity,COPY_AND_SORT);
	}


	/**
	 * @param sizes the sizes of the item
	 * @param capacity the capacity of the bins
	 * @param mode the mode of instantiation
	 */
	public AbstractHeurisic1BP(final int[] sizes,final int capacity,final int mode) {
		this.sizes = mode==COPY_AND_SORT ? Arrays.copyOf(sizes,sizes.length) : sizes;
		if(mode==SORT || mode==COPY_AND_SORT) {
			Arrays.sort(this.sizes);
		}
		increasing= mode!=DECREASING;
		this.capacity=capacity;
		siIndex=initialize();
	}


	/**
	 * Initialize the smallest item index.
	 */
	private int initialize() {
		int k;
		if(increasing) {
			k=0;
			while(k<sizes.length && sizes[k]==0) {k++;}
		}else {
			k=sizes.length-1;
			while(k>=0 && sizes[k]==0) {k--;}
		}
		return k;

	}

	/**
	 * Reset.
	 */
	public void reset() {
		this.available.clear();
		this.filled.clear();
	}

	/**
	 * Extract the bin where the item will be packed into.
	 *
	 * @param item the item index
	 *
	 * @return the bin
	 */
	public abstract Bin extract(int item);

	/**
	 * Pack an item into a bin.
	 *
	 * @param item the item index
	 * @param bin the concerned bin
	 */
	protected final void pack(final int item,final Bin bin) {
		bin.pack(item, sizes[item]);
		if(bin.isPackable(sizes[siIndex])) {available.add(bin);}
		else {filled.add(bin);}
	}


	/**
	 * Compute an Upper Bound (solution) for the one-dimensiona Bin Packing Problem.
	 *
	 * @return the UB
	 */
	public final int computeUB() {
		reset();
		if(increasing) {
			for(int item = sizes.length-1; item >=siIndex; item--) {
				pack(item,extract(item));
			}
		}else {
			for(int item = 0; item <siIndex; item++) {
				pack(item,extract(item));
			}
		}
		return filled.size()+available.size();
	}
}






