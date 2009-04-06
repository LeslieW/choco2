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
package choco.kernel.memory;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 12 juil. 2007
 * Time: 10:25:47
 */
public interface IStateVector<E> {
    /**
   * Minimal capacity of a vector
     */
    int MIN_CAPACITY = 8;

    /**
   * Returns the current size of the stored search vector.
     */

    int size();

    /**
   * Checks if the vector is empty.
     */

    boolean isEmpty();

    /**
   * Checks if the capacity is great enough, else the capacity
     * is extended.
     *
     * @param minCapacity the necessary capacity.
     */

    void ensureCapacity(int minCapacity);

    /**
   * Adds a new search at the end of the vector.
     *
     * @param i The search to add.
     */

    boolean add(E i);

    /**
   * removes the search at the end of the vector.
     * does nothing when called on an empty vector
     */

    void removeLast();

    /**
   * Returns the <code>index</code>th element of the vector.
     */

    E get(int index);

    /**
   * Assigns a new value <code>val</code> to the element <code>index</code>.
     */

    E set(int index, E val);
}
