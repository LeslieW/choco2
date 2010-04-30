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

import choco.kernel.common.util.disposable.Disposable;
import choco.kernel.common.util.iterators.DisposableIntIterator;

import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 29 mars 2010br/>
 * Since : Choco <br/>
 */
public final class IntervalIntIterator extends DisposableIntIterator {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static final Queue<IntervalIntIterator> container = Disposable.createContainer();
    }

    private int current, currentInfPropagated, currentSupPropagated, lastSupPropagated;

    private IntervalIntIterator() {
    }

    private static IntervalIntIterator build() {
        return new IntervalIntIterator();
    }

    @SuppressWarnings({"unchecked"})
    public static synchronized IntervalIntIterator getIterator(final int theCurrentInfPropagated, final int theCurrentSupPropagated,
                     final int theLastIntPropagated, final int theLastSupPropagated) {
        IntervalIntIterator it;
        try{
            it = Holder.container.remove();
        }catch (NoSuchElementException e){
            it = build();
        }
        it.init(theCurrentInfPropagated, theCurrentSupPropagated, theLastIntPropagated, theLastSupPropagated);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final int theCurrentInfPropagated, final int theCurrentSupPropagated,
                     final int theLastIntPropagated, final int theLastSupPropagated) {
        current = theLastIntPropagated - 1;
        this.currentInfPropagated = theCurrentInfPropagated;
        this.currentSupPropagated = theCurrentSupPropagated;
        this.lastSupPropagated = theLastSupPropagated;
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
        if (current + 1 == currentInfPropagated) return currentSupPropagated < lastSupPropagated;
        if (current > currentSupPropagated) return current < lastSupPropagated;
        return (current + 1 < currentInfPropagated);
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
        current++;
        if (current == currentInfPropagated) {
            current = currentSupPropagated + 1;
        }
        return current;
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