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

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.IntIterator;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 12, 2008
 * Time: 1:12:37 PM
 */
@Deprecated // see trove4j librairy
public abstract class AbstractIntCollection implements IntCollection {

    protected final static Logger LOGGER = ChocoLogging.getKernelLogger();

    protected AbstractIntCollection() {
    }

    public abstract IntIterator iterator();

    public abstract int size();

    public boolean isEmpty() {
        return size() == 0;
    }


    public boolean contains(int o) {
        IntIterator e = iterator();
        while (e.hasNext())
            if (o == e.next())
                return true;

        return false;
    }

    public int[] toArray() {
        // Estimate size of array; be prepared to see more or fewer elements
        int[] r = new int[size()];
        IntIterator it = iterator();
        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext())    // fewer elements than expected
                return Arrays.copyOf(r, i);
            r[i] = it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }


    public int[] toArray(int[] a) {
        // Estimate size of array; be prepared to see more or fewer elements
        int size = size();
        int[] r = a.length >= size ? a : new int[size];

        IntIterator it = iterator();

        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext()) { // fewer elements than expected
                if (a != r)
                    return Arrays.copyOf(r, i);
                r[i] = 0; // null-terminate
                return r;
            }
            r[i] = it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }


    private static int[] finishToArray(int[] r, IntIterator it) {
        int i = r.length;
        while (it.hasNext()) {
            int cap = r.length;
            if (i == cap) {
                int newCap = ((cap / 2) + 1) * 3;
                if (newCap <= cap) { // integer overflow
                    if (cap == Integer.MAX_VALUE)
                        throw new OutOfMemoryError
                                ("Required array size too large");
                    newCap = Integer.MAX_VALUE;
                }
                r = Arrays.copyOf(r, newCap);
            }
            r[i++] = it.next();
        }
        // trim if overallocated
        return (i == r.length) ? r : Arrays.copyOf(r, i);
    }


    public boolean add(int e) {
        throw new UnsupportedOperationException();
    }


    public boolean remove(int o) {
        IntIterator e = iterator();

        while (e.hasNext()) {
            if (o == e.next()) {
                e.remove();
                return true;
            }

        }
        return false;
    }


    public boolean containsAll(IntCollection c) {
        IntIterator e = c.iterator();
        while (e.hasNext())
            if (!contains(e.next()))
                return false;
        return true;
    }


    public boolean addAll(IntCollection c) {
        boolean modified = false;
        IntIterator e = c.iterator();
        while (e.hasNext()) {
            if (add(e.next()))
                modified = true;
        }
        return modified;
    }

    public boolean removeAll(IntCollection c) {
        boolean modified = false;
        IntIterator e = iterator();
        while (e.hasNext()) {
            if (c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }

    public boolean retainAll(IntCollection c) {
        boolean modified = false;
        IntIterator e = iterator();
        while (e.hasNext()) {
            if (!c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }


    public void clear() {
        IntIterator e = iterator();
        while (e.hasNext()) {
            e.next();
            e.remove();
        }
    }


    public String toString() {
        IntIterator i = iterator();
        if (!i.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (; ;) {
            int e = i.next();
            sb.append(e);
            if (!i.hasNext())
                return sb.append(']').toString();
            sb.append(", ");
        }
    }

}
