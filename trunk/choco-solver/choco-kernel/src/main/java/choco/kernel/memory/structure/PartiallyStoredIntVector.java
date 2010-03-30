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
package choco.kernel.memory.structure;

import static choco.kernel.common.Constant.*;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.structure.iterators.PSIVIterator;


/**
 * A class implementing a vector with two kind of storage:
 * standard static storage in an array, and backtrackable storage.
 * By convention, integers with small indices (0 .. 999999) are statically managed
 * as if they were in a standard array.
 * And integers with large indices (1000000 ... ) are "stored" in a backtrackable
 * manner, as if they were in a StoredIntVector
 */
public final class PartiallyStoredIntVector {

    private int[] staticInts;
    private int[] storedInts;

    private int nStaticInts;
    private final IStateInt nStoredInts;

    public PartiallyStoredIntVector(final IEnvironment env) {
        staticInts = new int[INITIAL_STATIC_CAPACITY];
        storedInts = new int[INITIAL_STORED_CAPACITY];
        nStaticInts = 0;
        nStoredInts = env.makeInt(0);
    }

    public int staticAdd(final int o) {
        ensureStaticCapacity(nStaticInts + 1);
        staticInts[nStaticInts++] = o;
        return nStaticInts - 1;
    }

    public void ensureStaticCapacity(final int n) {
        if (n > staticInts.length) {
            int newSize = staticInts.length;
            while (n >= newSize) {
                newSize = (3 * newSize) / 2;
            }
            final int[] newStaticObjects = new int[newSize];
            System.arraycopy(staticInts, 0, newStaticObjects, 0, staticInts.length);
            this.staticInts = newStaticObjects;
        }
    }

    public int add(final int o) {
        ensureStoredCapacity(nStoredInts.get() + 1);
        storedInts[nStoredInts.get()] = o;
        nStoredInts.add(1);
        return STORED_OFFSET + nStoredInts.get() - 1;
    }

    public void remove(final int o) {
        staticInts[o] = staticInts[nStaticInts];
        staticInts[nStaticInts] = 0;
        nStaticInts--;
    }

    public void ensureStoredCapacity(final int n) {
        if (n > storedInts.length) {
            int newSize = storedInts.length;
            while (n >= newSize) {
                newSize = (newSize*3) / 2 + 1;
            }
            final int[] newStoredObjects = new int[newSize];
            System.arraycopy(storedInts, 0, newStoredObjects, 0, storedInts.length);
            this.storedInts = newStoredObjects;
        }
    }

    public int get(final int index) {
        if (index < STORED_OFFSET) {
            return staticInts[index];
        } else {
            return storedInts[index - STORED_OFFSET];
        }
    }

    public boolean isEmpty() {
        return ((nStaticInts == 0) && (nStoredInts.get() == 0));
    }

    public int size() {
        return (nStaticInts + nStoredInts.get());
    }

    public DisposableIntIterator getIndexIterator() {
        return PSIVIterator.getIterator(nStaticInts, nStoredInts);
    }

    public static boolean isStaticIndex(final int idx) {
        return idx < STORED_OFFSET;
    }

    public static int getSmallIndex(final int idx) {
        if (idx < STORED_OFFSET){
            return idx;
        }else{
            return idx - STORED_OFFSET;
        }
    }

    public static int getGlobalIndex(final int idx, final boolean isStatic) {
        if (isStatic){
            return idx;
        }else{
            return idx + STORED_OFFSET;
        }
    }

}
