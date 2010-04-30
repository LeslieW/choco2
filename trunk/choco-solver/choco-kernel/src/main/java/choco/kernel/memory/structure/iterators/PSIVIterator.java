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
import choco.kernel.common.util.disposable.Disposable;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IStateInt;

import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 29 mars 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public final class PSIVIterator extends DisposableIntIterator {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static final Queue<PSIVIterator> container = Disposable.createContainer();
    }

    private int nStaticInts;

    private int nStoredInts;

    private int idx;

    private boolean stats;

    private boolean storeds;

    private PSIVIterator() {
    }

    private static PSIVIterator build() {
        return new PSIVIterator();
    }

    @SuppressWarnings({"unchecked"})
    public static synchronized PSIVIterator getIterator(final int theNStaticInts, final IStateInt theNStoredInts) {
        PSIVIterator it;
        try{
            it = Holder.container.remove();
        }catch (NoSuchElementException e){
            it = build();
        }
        it.init(theNStaticInts, theNStoredInts);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final int theNStaticInts, final IStateInt theNStoredInts) {
        init();
        this.nStaticInts = theNStaticInts;
        this.nStoredInts = theNStoredInts.get();
        stats = (nStaticInts> 0);
        storeds = (nStoredInts > 0);
        idx = -1;
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
        if (idx == -1) {
            return stats || storeds;
        } else {
            return ((stats && idx < nStaticInts - 1)
                    || (idx == nStaticInts - 1 && storeds)
                    || (storeds && STORED_OFFSET <= idx && idx < STORED_OFFSET + nStoredInts - 1));
        }
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
        idx++;
        if (idx == nStaticInts) {
            idx = STORED_OFFSET;
        }
        return idx;
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