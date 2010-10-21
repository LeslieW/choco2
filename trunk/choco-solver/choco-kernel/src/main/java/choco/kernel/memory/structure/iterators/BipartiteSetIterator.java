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

import choco.kernel.common.util.disposable.Disposable;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.structure.IndexedObject;

import java.util.Queue;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 29 mars 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public final class BipartiteSetIterator extends DisposableIntIterator {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static final Queue<BipartiteSetIterator> container = Disposable.createContainer();
    }

    private int[] list;

    private int[] position;

    private IndexedObject[] idxToObjects;

    private IStateInt last;

    private int nlast, idx;

    private BipartiteSetIterator() {
    }

    private static BipartiteSetIterator build() {
        return new BipartiteSetIterator();
    }

    @SuppressWarnings({"unchecked"})
    public static BipartiteSetIterator getIterator(final int[] aList, final int[] aPosition,
                                                                final IStateInt aLast, final IndexedObject[] idxToObjects) {
        BipartiteSetIterator it;
        synchronized (Holder.container) {
            if (Holder.container.isEmpty()) {
                it = build();
            } else {
                it = Holder.container.remove();
            }
        }
        it.init(aList, aPosition, aLast, idxToObjects);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final int[] aList, final int[] aPosition, final IStateInt aLast, final IndexedObject[] anIdxToObjects) {
        init();
        idx = 0;
        list = aList;
        position = aPosition;
        idxToObjects = anIdxToObjects;
        last = aLast;
        nlast = last.get();
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
        return idx <= nlast;
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
        return list[idx++];
    }


    public IndexedObject nextObject() {
        return idxToObjects[list[idx++]];
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
        idx--;
        final int idxToRem = idx;
        if (idxToRem == nlast) {
            last.add(-1);
            nlast--;
        } else {
            final int temp = list[nlast];
            list[nlast] = list[idxToRem];
            list[idxToRem] = temp;
            position[list[nlast]] = last.get();
            position[temp] = idxToRem;
            last.add(-1);
            nlast--;
        }
    }

    /**
     * Get the containerof disposable objects where free ones are available
     *
     * @return a {@link java.util.Deque}
     */
    @Override
    public Queue getContainer() {
        return Holder.container;
    }
}