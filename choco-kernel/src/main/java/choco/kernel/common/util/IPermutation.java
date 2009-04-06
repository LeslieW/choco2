/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  Â°(..)  |                           *
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

package choco.kernel.common.util;

import choco.kernel.model.variables.integer.IntegerConstantVariable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
/**
 * Modelize a sorting permutation of a set.
 * @author Arnaud Malapert</br>
 * @since 4 déc. 2008 <b>version</b> 2.0.1</br>
 * @version 2.0.1</br>
 */
public interface IPermutation {


	/**
	 * apply the permutation on the source array
	 * @param source the array to be permuted
	 * @param dest the permuted array
	 */
	public <T> void applyPermutation(T[] source,T[] dest);

	/**
	 * apply the permutation to the integer array
	 * @return the permuted array
	 */
	public int[] applyPermutation(int[] source);

	/**
	 * retu
	 * @param idx the permutation index
	 * @return the index in the original order
	 */
	public int getOriginalIndex(int idx);

	/**
	 * return
	 * @param idx the index in the original order
	 * @return the index in the permutation
	 */
	public int getPermutationIndex(int idx);

	public boolean isIdentity();


}

/**
 * identity permutation.
 * @author Arnaud Malapert</br>
 * @since 4 déc. 2008 <b>version</b> 2.0.1</br>
 * @version 2.0.1</br>
 */
class Identity implements IPermutation {

	public final static IPermutation SINGLETON = new Identity();

	private Identity() {
		super();
	}

	@Override
	public int[] applyPermutation(int[] source) {
		return source;
	}

	@Override
	public <T> void applyPermutation(T[] source, T[] dest) {
		if(source.length != dest.length) {
			throw new ArrayIndexOutOfBoundsException("the two arguments should have the same length.");
		}else {
			System.arraycopy(source, 0, dest, 0, source.length);
			for (int i = 0; i < source.length; i++) {
				dest[i]=source[i];
			}
		}

	}

	@Override
	public int getOriginalIndex(int idx) {
		return idx;
	}

	@Override
	public int getPermutationIndex(int idx) {
		return idx;
	}

	@Override
	public boolean isIdentity() {
		return true;
	}

	@Override
	public String toString() {
		return "IDENTITY";
	}





}



abstract class AbstractSortingPermutation implements IPermutation,Comparator<Integer> {

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

class IntPermutation extends AbstractSortingPermutation {

	protected final int[] elements;

	public IntPermutation(int[] elements, boolean reverse) {
		super(elements.length);
		this.elements = elements;
		this.sort(reverse);
	}

	@Override
	public int compare(Integer o1, Integer o2) {
		if(elements[o1]>elements[o2]) {return 1;}
		else if(elements[o1]<elements[o2]) {return -1;}
		else {return 0;}
	}
}

class ConstantPermutation extends AbstractSortingPermutation {

	protected final IntegerConstantVariable[] elements;

	public ConstantPermutation(IntegerConstantVariable[] elements, boolean reverse) {
		super(elements.length);
		this.elements = elements;
		this.sort(reverse);
	}

	@Override
	public int compare(Integer o1, Integer o2) {
		return elements[o1].compareTo(elements[o2]);
	}


}


class SortingPermutation implements IPermutation,Comparator<Integer> {

	public final static IPermutation IDENTITY = Identity.SINGLETON;

	protected boolean identity;

	protected final int[] elements;

	protected final Integer[] orderingPermutation;

	protected final Integer[] reversePermutation;

	public SortingPermutation(int[] elements,boolean reverse) {
		super();
		this.elements = elements;
		orderingPermutation =new Integer[elements.length];
		reversePermutation =new Integer[elements.length];
		for (int i = 0; i < orderingPermutation.length; i++) {
			orderingPermutation[i]=Integer.valueOf(i);
		}
		sort(reverse);
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
		int[] dest=new int[elements.length];
		for (int i = 0; i < elements.length; i++) {
			dest[i]=source[orderingPermutation[i]];
		}
		return dest;
	}


	public final int[] applyPermutation() {
		return applyPermutation(elements);
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



	@Override
	public int compare(Integer o1, Integer o2) {
		if(elements[o1]>elements[o2]) {return 1;}
		else if(elements[o1]<elements[o2]) {return -1;}
		else {return 0;}
	}


	public final boolean isIdentity() {
		return identity;
	}


	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Criteria: ");
		b.append(Arrays.toString(elements));
		b.append("\nOrdering permutation: ");
		b.append(Arrays.toString(orderingPermutation));
		b.append('\n');
		return new String(b);
	}


}



