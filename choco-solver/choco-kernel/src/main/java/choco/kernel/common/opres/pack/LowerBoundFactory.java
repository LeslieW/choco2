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
