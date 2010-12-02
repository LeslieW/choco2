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

import java.util.Arrays;

public class FunctionF1 extends AbstractFunctionDDFF {

	/**
	 * by ascending sizes.
	 */
	public TIntArrayList items;
	
	private final int[] knapsacks;
	
	public FunctionF1(int capacity) {
		super(capacity);
		knapsacks = new int[capacity + 1];
	}

	@Override
	public int apply(int size) {
		if(size > midCapacity) return knapsacks[capacity] - knapsacks[capacity - size];
		else if( size >= k) return 1;
		else return 0;
	}
	
	public final TIntArrayList getItems() {
		return items;
	}

	public final void setItems(TIntArrayList items) {
		this.items = items;
	}

	@Override
	public void setParameter(int k) {
		super.setParameter(k);
		final int n = items.size();
		int idx = 0;
		while( idx < n && items.getQuick(idx) < k) {idx++;}
		int nbI = 0;
		int offset = 0;
		int size = 0;
		while(idx < n) {
			//final int sizeI = items.getQuick(idx);
			size += items.getQuick(idx);
			if(size > capacity) break;
			else {
				Arrays.fill(knapsacks, offset, size, nbI);
				offset = size;
				nbI++;
			}
			idx++;
		}
		Arrays.fill(knapsacks, offset, capacity + 1, nbI);
	}

	@Override
	public int findParameter(int size) {
		if( size > midCapacity)	return size == capacity ? midCapacity : size - midCapacity;
		else return size;
	}

	@Override
	public int getCurrentCapacity() {
		return knapsacks[capacity];
	}

	public static void main(String[] args) {
		FunctionF1 f1 = new FunctionF1(10);
		f1.items = new TIntArrayList(new int[]{1,2,3,4,5});
		f1.setParameter(5);
		System.out.println(Arrays.toString(f1.knapsacks));
	}
		
}
