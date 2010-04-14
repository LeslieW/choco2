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
import gnu.trove.TIntProcedure;
import gnu.trove.TLinkableAdapter;
import gnu.trove.TLinkedList;
import gnu.trove.TObjectProcedure;

final class TLinkedBin extends TLinkableAdapter {

	private static final long serialVersionUID = -808638405297420985L;

	/**remaining area in the bin*/
	public int remainingArea;

	public TLinkedBin(int remainingArea) {
		super();
		this.remainingArea = remainingArea;
	}

	public final int getRemainingArea() {
		return remainingArea;
	}

	public final void setRemainingArea(int remainingArea) {
		this.remainingArea = remainingArea;
	}

	public void pack(final int size) {
		remainingArea -= size;
	}

	public boolean isPackable(final int size) {
		return size<=remainingArea;
	}

	public boolean isFit(final int size) {
		return size==remainingArea;
	}

	@Override
	public String toString() {
		return String.valueOf(remainingArea);
	}

}
/**
 * The Class AbstractHeurisic1BP.
 */
public abstract class AbstractHeurisic1BP implements TIntProcedure, TObjectProcedure<TLinkedBin> {

	/** The capacity of the bins. */
	public int capacity;

	private int nbBins;

	protected int reuseSize;

	protected TLinkedBin reuseBin;

	/** The available bins. */
	private final TLinkedList<TLinkedBin> bins;

	public AbstractHeurisic1BP(final int capacity) {
		bins = new TLinkedList<TLinkedBin>();
		this.capacity = capacity;
	}

	/**
	 * Reset.
	 */
	public void reset() {
		bins.clear();
		nbBins = 0;
	}

	@Override
	public boolean execute(int size) {
		if(size > 0) {
			reuseSize = size;
			reuseBin = null;
			bins.forEachValue(this);
			if(reuseBin == null) {
				bins.add(new TLinkedBin(capacity - size));
			}else if(reuseBin.isFit(size)) {
				bins.remove(reuseBin);
				nbBins++;
			}else reuseBin.pack(size);
			return true;
		} else return false;
	}


	protected abstract boolean handleInsertion(TLinkedBin bin);

	@Override
	public boolean execute(TLinkedBin bin) {
		if(bin.remainingArea >= reuseSize) {
			return handleInsertion(bin);
		}
		return true;
	}


	/**
	 * Compute an Upper Bound (solution) for the one-dimensiona Bin Packing Problem.
	 *
	 * @return the UB
	 */
	public final int computeUB(TIntArrayList items) {
		reset();
		items.forEachDescending(this);
		nbBins += bins.size();
		return nbBins; 
	}


}






