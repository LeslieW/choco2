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
package choco.kernel.memory.structure;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.variables.Var;

import java.util.*;



/**
 * @author Arnaud Malapert</br> 
 * @since 10 févr. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 * @param <E>
 */
public class StoredBipartiteList<E> extends AbstractList<E> {

    static final int INITIAL_CAPACITY = 8;

	/**
	 * The list of values
	 */
	protected E[] elementData;

	/**
	 * The index of last element of the list
	 */
	protected IStateInt last;

    StoredBipartiteList(IEnvironment env) {
        super();
        //noinspection unchecked
        elementData = (E[])new Var[INITIAL_CAPACITY];
		this.last = env.makeInt(0);
    }

    @SuppressWarnings("unchecked")
    public StoredBipartiteList(IEnvironment env, Collection<E>  coll) {
		super();
		this.elementData = (E[]) coll.toArray();
		this.last = env.makeInt(elementData.length);
	}

	public StoredBipartiteList(IEnvironment env, E[] elementData) {
		super();
		this.elementData = Arrays.copyOf(elementData, elementData.length);
		this.last = env.makeInt(elementData.length);
	}

    /**
	 * Checks if the given index is in range.  If not, throws an appropriate
	 * runtime exception.  This method does *not* check if the index is
	 * negative: It is always used immediately prior to an array access,
	 * which throws an ArrayIndexOutOfBoundsException if index is negative.
     * @param index index to check
     */
	void RangeCheck(int index) {
		if (index >= size())
			throw new IndexOutOfBoundsException(
					"Index: "+index+", Size: "+size());
	}

	
	
	@Override
	public E get(int index) {
		RangeCheck(index);
		return elementData[index];
	}
	
	public E getQuick(int index) {
		return elementData[index];
	}

	public Iterator<E> quickIterator() {
		return new QuickItr();
		
	}
	
	 private class QuickItr implements Iterator<E> {
			/**
			 * Index of element to be returned by subsequent call to next.
			 */
			int cursor = 0;

			public boolean hasNext() {
		            return cursor < size();
			}

			public E next() {
		            return elementData[cursor++];
			}

			public void remove() {
			    throw new UnsupportedOperationException("cant remove with quick iterator.");
			}
	 }
	 
	@Override
	public int size() {
		return last.get();
	}

	/**
	 * removal performs a swap on a pair of elements. Do not remove while iterating if you want to preserve the current order.
	 * @see java.util.AbstractList#remove(int)
	 */
	@Override
	public E remove(int index) {
		RangeCheck(index);
		final int idx = size()-1;
		//should swap the element to remove with the last element
		final E tmp = elementData[index];
		elementData[index] = elementData[idx];
		elementData[idx] = tmp;
		last.set(idx);
		return tmp;
	}

	public void sort(Comparator<E> cmp) {
		Arrays.sort(elementData, 0, size(), cmp);
	}

}
