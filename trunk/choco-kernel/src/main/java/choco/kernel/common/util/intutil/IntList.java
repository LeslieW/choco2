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
package choco.kernel.common.util.intutil;

import choco.kernel.common.util.IntIterator;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 12, 2008
 * Time: 1:12:37 PM
 */
@Deprecated // see trove4j librairy
public interface IntList extends IntCollection {

    int size();


    boolean isEmpty();


    boolean contains(int o);

    IntIterator iterator();


    int[] toArray();


    int[] toArray(int[] a);



    boolean add(int e);

    boolean remove(int o);

    boolean containsAll(IntCollection c);

    boolean addAll(IntCollection c);


    boolean addAll(int index, IntCollection c);

    boolean removeAll(IntCollection c);

    boolean retainAll(IntCollection c);

    void clear();

    boolean equals(Object o);

    int hashCode();

    int get(int index);

    int set(int index, int element);

    void add(int index, int element);

    int removeAtPosition(int index);

    int indexOf(int o);

    int lastIndexOf(int o);

    IntListIterator listIterator();

    IntListIterator listIterator(int index);

    IntList subList(int fromIndex, int toIndex);
}
