/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
public abstract class AbstractHeuristic1BP implements TIntProcedure, TObjectProcedure<TLinkedBin> {

	/** The capacity of the bins. */
	public int capacity;

	private int nbBins;

	protected int reuseSize;

	protected TLinkedBin reuseBin;

	/** The available bins. */
	private final TLinkedList<TLinkedBin> bins;

	public AbstractHeuristic1BP(final int capacity) {
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






