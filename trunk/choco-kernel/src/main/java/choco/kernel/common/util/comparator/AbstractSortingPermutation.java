/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package choco.kernel.common.util.comparator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 2 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public abstract class AbstractSortingPermutation implements IPermutation, Comparator<Integer> {

	public final static IPermutation IDENTITY = Identity.SINGLETON;


	protected boolean identity;

	protected final Integer[] orderingPermutation;

	protected final Integer[] reversePermutation;

	public AbstractSortingPermutation(int size) {
		super();
		orderingPermutation =new Integer[size];
		reversePermutation =new Integer[size];
		for (int i = 0; i < orderingPermutation.length; i++) {
			orderingPermutation[i]=Integer.valueOf(i);
		}
	}

	public final void sort(boolean reverse) {
		Arrays.sort(orderingPermutation,  reverse ? Collections.reverseOrder(this) : this);
		identity=true;
		for (int i = 0; i < orderingPermutation.length; i++) {
			reversePermutation[orderingPermutation[i]]=i;
			if(identity && i!=orderingPermutation[i]) {identity=false;}
		}
	}


	public final <T> void applyPermutation(T[] source,T[] dest) {
		if(source.length != orderingPermutation.length || source.length != dest.length) {
			throw new ArrayIndexOutOfBoundsException("the two arguments should have the same length than the permutation array");
		}else {
			for (int i = 0; i < source.length; i++) {
				dest[i]=source[orderingPermutation[i]];
			}
		}

	}


	@Override
	public int[] applyPermutation(int[] source) {
		int[] dest=new int[orderingPermutation.length];
		for (int i = 0; i < orderingPermutation.length; i++) {
			dest[i]=source[orderingPermutation[i]];
		}
		return dest;
	}



	/**
	 * return the original index of the idx-th element of the permuted array
	 */
	public final int getOriginalIndex(int idx) {
		return this.orderingPermutation[idx];
	}
	/**
	 * return the index in the permutation of the idx-th element
	 */
	public final int getPermutationIndex(int idx) {
		return reversePermutation[idx];
	}



	public final boolean isIdentity() {
		return identity;
	}




	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Ordering permutation: ");
		b.append(Arrays.toString(orderingPermutation));
		return new String(b);
	}


}
