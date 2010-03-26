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
import static choco.kernel.common.Constant.SET_INITIAL_CAPACITY;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.structure.iterators.SBVSIterator1;
import choco.kernel.memory.structure.iterators.SBVSIterator2;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.Var;

import java.util.Arrays;
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

    public StoredBipartiteVarSet(IEnvironment env) {
        super(env);
        //noinspection unchecked
        varsNotInstanciated = (E[])new Var[SET_INITIAL_CAPACITY];
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
    @SuppressWarnings({"unchecked"})
    public void ensureCapacity(int expectedSize) {
        if(elementData.length < expectedSize){
            int newSize = elementData.length;
            do{
                newSize *= 2;
            }while(newSize < expectedSize);

            E[] oldElements = elementData;
            elementData = (E[])new Var[newSize];
            System.arraycopy(oldElements, 0, elementData, 0, oldElements.length);

            oldElements = varsNotInstanciated;
            varsNotInstanciated = (E[])new Var[newSize];
            System.arraycopy(oldElements, 0, varsNotInstanciated, 0, oldElements.length);
        }
    }

    /**
	 * removal performs a swap on a pair of elements. Do not remove while iterating if you want to preserve the current order.
     * @param index index of the object to remove
     * @return the removed object
     */
    public E swap(int index) {
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
    public boolean contains(E o) {
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
    public int indexOf(E o) {
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

    /**
     * Iterator over non instanciated variables
     * BEWARE: initial order is not preserved
     * @return iterator
     */
    @SuppressWarnings({"unchecked"})
    public final DisposableIterator<E> getNotInstanciatedVariableIterator(){
        return SBVSIterator1.getIterator(this, varsNotInstanciated, last.get());    
    }
    /**
     * Iterator over instanciated variables
     * BEWARE: initial order is not preserved
     * @return iterator
     */
    @SuppressWarnings({"unchecked"})
    public final DisposableIterator<E> getInstanciatedVariableIterator(){
        return SBVSIterator2.getIterator(varsNotInstanciated, size);
    }
}
