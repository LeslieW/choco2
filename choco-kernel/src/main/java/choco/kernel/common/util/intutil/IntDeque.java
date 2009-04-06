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
public interface IntDeque extends IntQueue {

    void addFirst(int e);

    void addLast(int e);


    boolean offerFirst(int e);


    boolean offerLast(int e);

    int removeFirst();

    int removeLast();

    int pollFirst();

    int pollLast();

    int getFirst();

    int getLast();

    int peekFirst();

    int peekLast();

    boolean removeFirstOccurrence(int o);

    boolean removeLastOccurrence(int o);

    boolean add(int e);

    boolean offer(int e);

    int remove();

    int poll();

    int element();

    int peek();


    void push(int e);

    int pop();

    boolean remove(int o);

    boolean contains(int o);

    public int size();

    IntIterator iterator();

    IntIterator descendingIterator();

}
