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
// Petite API de Graphes pour tester BK73 
// TP 16/02/2007
// --------------------------------------

package choco.kernel.common.util.objects;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A simple representation of a 0-1 sparse matrix
 */
public class BooleanSparseMatrix implements ISparseMatrix{

    /**
     * valued rows
     */
    private long[] elements;


    private int nbElement;
    /**
     * Number of rows of the matrix
     */
    final int size;

    public BooleanSparseMatrix(final int n) {
        size = n;
        elements = new long[n];
        nbElement = 0;
    }

    /**
     * Add a new element in the matrix
     * @param i
     * @param j
     */
    public void add(int i, int j){
        ensureCapacity(nbElement+1);
        long v  = ((long)Math.min(i,j)*(long)size+(long)Math.max(i,j));
        elements[nbElement++] = v;
    }

    private void ensureCapacity(int nsize) {
        if(elements.length<nsize){
            long[] newElm = new long[nsize * 3/2];
            System.arraycopy(elements, 0, newElm, 0, elements.length);
            elements = newElm;
        }
    }


    /**
     * get the number of element contained in the matrix
     * @return the number of element
     */
    public int getNbElement(){
        return nbElement;
    }


    /**
     * Return an iterator over the values
     *
     * @return an iterator
     */
    @Override
    public Iterator<Long> iterator() {
        return new Iterator<Long>(){
            int i = 0;
            /**
             * Returns <tt>true</tt> if the iteration has more elements. (In other
             * words, returns <tt>true</tt> if <tt>next</tt> would return an element
             * rather than throwing an exception.)
             *
             * @return <tt>true</tt> if the getIterator has more elements.
             */
            @Override
            public boolean hasNext() {
                return i < nbElement;
            }

            /**
             * Returns the next element in the iteration.
             *
             * @return the next element in the iteration.
             * @throws java.util.NoSuchElementException
             *          iteration has no more elements.
             */
            @Override
            public Long next() {
                return elements[i++];
            }

            /**
             * Removes from the underlying collection the last element returned by the
             * getIterator (optional operation).  This method can be called only once per
             * call to <tt>next</tt>.  The behavior of an getIterator is unspecified if
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
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Prepare the matrix for correct iteration.
     */
    @Override
    public void prepare() {
        long[] n = new long[nbElement];
        System.arraycopy(elements, 0, n, 0, nbElement);
        Arrays.sort(n);
        elements = n;
    }
}