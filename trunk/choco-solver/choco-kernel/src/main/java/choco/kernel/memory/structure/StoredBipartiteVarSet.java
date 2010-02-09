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
package choco.kernel.memory.structure;

import choco.IPretty;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.Var;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 17 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*
*
* Provide a structure for Var-like objects.
* It ensures:
* - an iterator over every variables
* - an efficient iterator over not instanciated variables
* - an iterator over instanciated variables
*/
public final class StoredBipartiteVarSet<E extends Var> extends StoredBipartiteSet<E> implements IPretty {


    private E[] varsNotInstanciated;

    private int size;

    IEnvironment env;

    public StoredBipartiteVarSet(IEnvironment env) {
        super(env);
        this.env = env;
        //noinspection unchecked
        varsNotInstanciated = (E[])new Var[INITIAL_CAPACITY];
        size = 0;
    }

    /**
     * Clear datastructures for safe reuses
     */
    @Override
    public void clear() {
        Arrays.fill(varsNotInstanciated, null);
        size = 0;
    }

    /**
     * Add a variable to the structure.
     * @param e the new variable
     * @return the index of the variable in the variable
     */
    @Override
    public boolean add(E e){
        ensureCapacity(size +1);
        elementData[size] = e;
        varsNotInstanciated[size++] = e;
        last.add(1);
        return true;
    }

    /**
     * Ensure the capasities of array
     * @param expectedSize expected size
     */
    public void ensureCapacity(int expectedSize) {
        if(elementData.length < expectedSize){
            int newSize = elementData.length;
            do{
                newSize *= 2;
            }while(newSize < expectedSize);

            @SuppressWarnings({"unchecked"})
            E[] newElements = (E[])new Var[newSize];
            System.arraycopy(elementData, 0, newElements, 0, elementData.length);
            elementData = newElements;

            System.arraycopy(varsNotInstanciated, 0, newElements, 0, varsNotInstanciated.length);
            varsNotInstanciated = newElements;
        }
    }

    /**
	 * removal performs a swap on a pair of elements. Do not remove while iterating if you want to preserve the current order.
     * @param index index of the object to remove
     * @return the removed object
     */
	private E swap(int index) {
		RangeCheck(index);
		final int idx = last.get()-1;
		//should swap the element to remove with the last element
		final E tmp = varsNotInstanciated[index];
		varsNotInstanciated[index] = varsNotInstanciated[idx];
		varsNotInstanciated[idx] = tmp;
		last.set(idx);
		return tmp;
	}

    public List<E> toList(){
        @SuppressWarnings({"unchecked"})
        E[] t = (E[])new Var[size];
        System.arraycopy(elementData, 0, t , 0, size);
        return Arrays.asList(t);
    }

    @Override
    public E[] toArray(){
        @SuppressWarnings({"unchecked"})
        E[] t = (E[])new Var[size];
        System.arraycopy(elementData, 0, t , 0, size);
        return t;
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this list contains
     * at least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     */
    public boolean contains(Object o) {
	return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     * @param o search object
     * @return index of o
     */
    public int indexOf(Object o) {
	if (o == null) {
	    for (int i = 0; i < size; i++)
		if (elementData[i]==null)
		    return i;
	} else {
	    for (int i = 0; i < size; i++)
		if (o.equals(elementData[i]))
		    return i;
	}
	return -1;
    }

    @Override
    public int size(){
        return size;
    }
    
    @Override
	public String pretty() {
    	return StringUtils.pretty(elementData, 0, size);
	}

	@Override
	public String toString() {
		return Arrays.toString(elementData);
	}

    /**
     * removal performs a swap on a pair of elements. Do not remove while iterating if you want to preserve the current order.
     *
     * @see java.util.AbstractList#remove(int)
     */
    @Override
    public E remove(int index) {
        throw new SolverException("Not yet implemented");
    }

    protected QuickIterator _cachedQuickIterator = null;
    /**
     * Iterator over non instanciated variables
     * BEWARE: initial order is not preserved
     * @return iterator
     */
    public final DisposableIterator<E> getNotInstanciatedVariableIterator(){
        @SuppressWarnings({"unchecked"})
        QuickIterator iter = _cachedQuickIterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        _cachedQuickIterator = new QuickIterator();
        return _cachedQuickIterator;
    }

    protected DualIterator _cachedDualterator = null;
    /**
     * Iterator over instanciated variables
     * BEWARE: initial order is not preserved
     * @return iterator
     */
    public final Iterator<E> getInstanciatedVariableIterator(){
        @SuppressWarnings({"unchecked"})
        DualIterator iter = _cachedDualterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        _cachedDualterator = new DualIterator();
        return _cachedDualterator;
    }



    private class QuickIterator extends DisposableIterator<E> {
        int i = -1;

        @Override
        public void init() {
            super.init();
            i = -1;
        }

		/**
         * Returns <tt>true</tt> if the iteration has more elements. (In other
         * words, returns <tt>true</tt> if <tt>next</tt> would return an element
         * rather than throwing an exception.)
         *
         * @return <tt>true</tt> if the iterator has more elements.
         */
        @Override
        public boolean hasNext() {
            i++;
            while(i < last.get() && varsNotInstanciated[i].isInstantiated()){
                swap(i);
            }
            return i < last.get();
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration.
         * @throws java.util.NoSuchElementException
         *          iteration has no more elements.
         */
        @Override
        public E next() {
            return varsNotInstanciated[i];
        }
    }

    private class DualIterator extends DisposableIterator<E> {
        int i = -1;

        @Override
        public void init() {
            super.init();
            i = -1;
        }

        /**
         * Returns <tt>true</tt> if the iteration has more elements. (In other
         * words, returns <tt>true</tt> if <tt>next</tt> would return an element
         * rather than throwing an exception.)
         *
         * @return <tt>true</tt> if the iterator has more elements.
         */
        @Override
        public boolean hasNext() {
            i ++ ;
            while(i < size && !varsNotInstanciated[i].isInstantiated())i++;
            return i < size;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration.
         * @throws java.util.NoSuchElementException
         *          iteration has no more elements.
         */
        @Override
        public E next() {
            return varsNotInstanciated[i];
        }
    }
}
