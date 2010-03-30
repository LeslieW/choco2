/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.memory.structure.iterators;

import static choco.kernel.common.Constant.STORED_OFFSET;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IStateInt;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 29 mars 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public final class PSVIndexIterator<E> extends DisposableIntIterator {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static PSVIndexIterator instance = PSVIndexIterator.build();

        private static void set(final PSVIndexIterator iterator) {
            instance = iterator;
        }
    }

    private int nStaticObjects;

    private int nStoredObjects;

    private E[] staticObjects;

    private int idx;

    private PSVIndexIterator() {
    }

    private static PSVIndexIterator build() {
        return new PSVIndexIterator();
    }

    @SuppressWarnings({"unchecked"})
    public static synchronized <E> PSVIndexIterator getIterator(final int theNStaticObjects, final E[] theStaticObjects,
                                                            final IStateInt theNStoredObjects) {
        PSVIndexIterator it = Holder.instance;
        if (!it.isReusable()) {
            it = build();
        }
        it.init(theNStaticObjects, theStaticObjects, theNStoredObjects);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final int theNStaticObjects, final E[] theStaticObjects, final IStateInt theNStoredObjects) {
        super.init();
        idx = -1;
        this.nStaticObjects = theNStaticObjects;
        this.staticObjects = theStaticObjects;
        this.nStoredObjects = theNStoredObjects.get();
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
        if (idx < STORED_OFFSET) {
            return idx + 1 < nStaticObjects || nStoredObjects > 0;
        } else return idx + 1 < STORED_OFFSET + nStoredObjects;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @throws java.util.NoSuchElementException
     *          iteration has no more elements.
     */
    @Override
    public int next() {
        if (idx < STORED_OFFSET) {
            if (idx + 1 < nStaticObjects) {
                idx++;
                while (staticObjects[idx] == null && idx < nStaticObjects) {
                    idx++;
                }
            } else if (nStoredObjects > 0) {
                idx = STORED_OFFSET;
            } else {
                throw new java.util.NoSuchElementException();
            }
        } else if (idx + 1 < STORED_OFFSET + nStoredObjects) {
            idx++;
        } else {
            throw new java.util.NoSuchElementException();
        }
        return idx;
    }


    /**
     * This method allows to declare that the iterator is not used anymoure. It
     * can be reused by another object.
     */
    @Override
    public void dispose() {
        super.dispose();
        Holder.set(this);
    }
}