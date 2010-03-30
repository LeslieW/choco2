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
package choco.cp.solver.variables.delta.iterators;

import choco.kernel.common.util.iterators.DisposableIntIterator;

import java.util.BitSet;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 29 mars 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class BitSetIterator extends DisposableIntIterator {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static BitSetIterator instance = BitSetIterator.build();

        private static void set(final BitSetIterator iterator) {
            instance = iterator;
        }
    }

    private int nextValue;
    private int offset;
    private BitSet contents;

    private BitSetIterator() {
    }

    private static BitSetIterator build() {
        return new BitSetIterator();
    }

    @SuppressWarnings({"unchecked"})
    public synchronized static BitSetIterator getIterator(final int theOffset,
                                                                   final BitSet theContents) {
        BitSetIterator it = Holder.instance;
        if (!it.isReusable()) {
            it = build();
        }
        it.init(theOffset, theContents);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final int theOffset, final BitSet theContents) {
        super.init();
        this.contents = theContents;
        this.offset = theOffset;
        nextValue = theContents.nextSetBit(0);
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
        return nextValue >= 0;
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
        final int v = nextValue;
        nextValue = contents.nextSetBit(nextValue + 1);
        return v + offset;
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