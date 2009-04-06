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

import java.util.NoSuchElementException;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 12, 2008
 * Time: 1:12:37 PM
 */
@Deprecated // see trove4j librairy
public abstract class AbstractIntQueue
        extends AbstractIntCollection
        implements IntQueue {


    protected AbstractIntQueue() {
    }


    public boolean add(int e) {
        if (offer(e))
            return true;
        else
            throw new IllegalStateException("IntQueue full");
    }


    public int remove() {
        int x = poll();
        if (x == Integer.MAX_VALUE)
            throw new NoSuchElementException();
        return x;
    }


    public int element() {
        int x = peek();
        if (x == Integer.MAX_VALUE)
            throw new NoSuchElementException();
        return x;
    }


    public void clear() {
        while (poll() != Integer.MAX_VALUE)
            ;
    }


    public boolean addAll(IntCollection c) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        boolean modified = false;
        IntIterator e = c.iterator();
        while (e.hasNext()) {
            if (add(e.next()))
                modified = true;
        }
        return modified;
    }

}
