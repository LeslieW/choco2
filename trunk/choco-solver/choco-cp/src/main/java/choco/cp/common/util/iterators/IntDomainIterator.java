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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.common.util.iterators;

import choco.cp.solver.variables.integer.AbstractIntDomain;
import choco.kernel.common.util.disposable.Disposable;
import choco.kernel.common.util.iterators.DisposableIntIterator;

import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public final class IntDomainIterator extends DisposableIntIterator {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static final Queue<IntDomainIterator> container = Disposable.createContainer();
    }

    private AbstractIntDomain domain;
    private int nextValue;
    private int supBound = -1;

    private IntDomainIterator() {
    }

    private static IntDomainIterator build() {
        return new IntDomainIterator();
    }

    @SuppressWarnings({"unchecked"})
    public synchronized static IntDomainIterator getIterator(final AbstractIntDomain aDomain) {
        IntDomainIterator it;
        try{
            it = Holder.container.remove();
        }catch (NoSuchElementException e){
            it = build();
        }
        it.init(aDomain);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final AbstractIntDomain dom) {
        init();
        domain = dom;
        if (domain.getSize() >= 1) {
            nextValue = domain.getInf();
        } else {
            throw new UnsupportedOperationException();
        }
        supBound = domain.getSup();
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
        return /*(Integer.MIN_VALUE == currentValue) ||*/ (nextValue <= supBound);
        // if currentValue equals MIN_VALUE it will be less than the upper bound => only one test is needed ! Moreover
        // MIN_VALUE is a special case, should not be tested if useless !
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
        nextValue = domain.getNextValue(nextValue);
        return v;
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
