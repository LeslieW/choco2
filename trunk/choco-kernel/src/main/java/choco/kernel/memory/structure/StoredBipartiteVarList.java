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

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
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
* - a iterator over elements
* - a
*/
public final class StoredBipartiteVarList<E extends Var>{

    private static final int INITIAL_CAPACITY = 8;

    private E[] vars;

    private E[] varsNotInstanciated;

    private int size;

    private IStateInt offset;

    IEnvironment env;

    private boolean update = false;

    public StoredBipartiteVarList(IEnvironment env) {
        //noinspection unchecked
        this.env = env;
        vars = (E[])new Var[INITIAL_CAPACITY];
        varsNotInstanciated = (E[])new Var[INITIAL_CAPACITY];
        size = 0;
        offset = env.makeInt(0);
    }

    /**
     * Add a variable to the structure.
     * Check also wether the variable is instanciated or not.
     * @param e the new variable
     * @return the index of the variable in the variable
     */
    public boolean add(E e){
        assert(env.getWorldIndex() == 0);
        ensureCapacity(size +1);
        vars[size] = e;
        varsNotInstanciated[size++] = e;
        offset.add(1);
        return true;
    }

    /**
     * Ensure the capasities of array
     * @param expectedSize expected size
     */
    public void ensureCapacity(int expectedSize) {
        if(vars.length < expectedSize){
            int newSize = vars.length;
            do{
                newSize *= 2;
            }while(newSize < expectedSize);

            @SuppressWarnings({"unchecked"})
            E[] newElements = (E[])new Var[newSize];
            System.arraycopy(vars, 0, newElements, 0, vars.length);
            vars = newElements;

            System.arraycopy(varsNotInstanciated, 0, newElements, 0, varsNotInstanciated.length);
            varsNotInstanciated = newElements;
        }
    }

    /**
	 * Checks if the given index is in range.  If not, throws an appropriate
	 * runtime exception.  This method does *not* check if the index is
	 * negative: It is always used immediately prior to an array access,
	 * which throws an ArrayIndexOutOfBoundsException if index is negative.
	 */
	private void RangeCheck(int index) {
		if (index >= size())
			throw new IndexOutOfBoundsException(
					"Index: "+index+", Size: "+size());
	}

    /**
	 * removal performs a swap on a pair of elements. Do not remove while iterating if you want to preserve the current order.
     * @param index index of the object to remove
     * @return the removed object
     */
	private E swap(int index) {
		RangeCheck(index);
		final int idx = offset.get()-1;
		//should swap the element to remove with the last element
		final E tmp = varsNotInstanciated[index];
		varsNotInstanciated[index] = varsNotInstanciated[idx];
		varsNotInstanciated[idx] = tmp;
		offset.set(idx);
		return tmp;
	}

    public List<E> toList(){
        @SuppressWarnings({"unchecked"})
        E[] t = (E[])new Var[size];
        System.arraycopy(vars, 0, t , 0, size);
        return Arrays.asList(t);
    }

    public E[] toArray(){
        @SuppressWarnings({"unchecked"})
        E[] t = (E[])new Var[size];
        System.arraycopy(vars, 0, t , 0, size);
        return t;
    }

    public E get(int i){
        return vars[i];
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
		if (vars[i]==null)
		    return i;
	} else {
	    for (int i = 0; i < size; i++)
		if (o.equals(vars[i]))
		    return i;
	}
	return -1;
    }

    public int size(){
        return size;
    }

    /**
     * Iterator over every variables
     * @return iterator
     */
    public final Iterator<E> getIterator(){
        return new Iterator<E>(){
            int i = 0;

            /**
             * Returns <tt>true</tt> if the iteration has more elements. (In other
             * words, returns <tt>true</tt> if <tt>next</tt> would return an element
             * rather than throwing an exception.)
             *
             * @return <tt>true</tt> if the iterator has more elements.
             */
            @Override
            public boolean hasNext() {
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
                return vars[i++];
            }

            /**
             * Removes from the underlying collection the last element returned by the
             * iterator (optional operation).  This method can be called only once per
             * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
             * the underlying collection is modified while the iteration is in
             * progress in any way other than by calling this method.
             *
             * @throws UnsupportedOperationException if the <tt>remove</tt>
             *                                       operation is not supported by this Iterator.
             * @throws IllegalStateException         if the <tt>next</tt> method has not
             *                                       yet been called, or the <tt>remove</tt> method has already
             *                                       been called after the last call to the <tt>next</tt>
             *                                       method.
             */
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot remove within iterator");
            }
        };
    }

    /**
     * Iterator over non instanciated variables
     * BEWARE: initial order is not preserved
     * @return iterator
     */
    public final Iterator<E> getNotInstanciatedVariableIterator(){
        return new QuickIterator();
    }

    /**
     * Iterator over instanciated variables
     * BEWARE: initial order is not preserved
     * @return iterator
     */
    public final Iterator<E> getInstanciatedVariableIterator(){
        return new DualIterator();
    }

    private class QuickIterator implements Iterator<E> {
        int i = -1;
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
            while(i < offset.get() && varsNotInstanciated[i].isInstantiated()){
                swap(i);
            }
            return i < offset.get();
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

        /**
         * Removes from the underlying collection the last element returned by the
         * iterator (optional operation).  This method can be called only once per
         * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
         * the underlying collection is modified while the iteration is in
         * progress in any way other than by calling this method.
         *
         * @throws UnsupportedOperationException if the <tt>remove</tt>
         *                                       operation is not supported by this Iterator.
         * @throws IllegalStateException         if the <tt>next</tt> method has not
         *                                       yet been called, or the <tt>remove</tt> method has already
         *                                       been called after the last call to the <tt>next</tt>
         *                                       method.
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not allowed int QuickIterator");
        }
    }

    private class DualIterator implements Iterator<E> {
        int i = -1;
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

        /**
         * Removes from the underlying collection the last element returned by the
         * iterator (optional operation).  This method can be called only once per
         * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
         * the underlying collection is modified while the iteration is in
         * progress in any way other than by calling this method.
         *
         * @throws UnsupportedOperationException if the <tt>remove</tt>
         *                                       operation is not supported by this Iterator.
         * @throws IllegalStateException         if the <tt>next</tt> method has not
         *                                       yet been called, or the <tt>remove</tt> method has already
         *                                       been called after the last call to the <tt>next</tt>
         *                                       method.
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not allowed int DualIterator");
        }
    }
}
