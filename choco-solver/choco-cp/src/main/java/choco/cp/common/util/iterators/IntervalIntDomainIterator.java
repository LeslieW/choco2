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

import choco.cp.solver.variables.integer.IntervalIntDomain;
import choco.kernel.common.util.iterators.DisposableIntIterator;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public final class IntervalIntDomainIterator extends DisposableIntIterator {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static IntervalIntDomainIterator instance = IntervalIntDomainIterator.build();

        private static void set(final IntervalIntDomainIterator iterator) {
            instance = iterator;
        }
    }

    private IntervalIntDomain domain;
    private int value;
    
    private IntervalIntDomainIterator() {
    }

    private static IntervalIntDomainIterator build() {
        return new IntervalIntDomainIterator();
    }

    @SuppressWarnings({"unchecked"})
    public synchronized static IntervalIntDomainIterator getIterator(final IntervalIntDomain aDomain) {
        IntervalIntDomainIterator it = Holder.instance;
        if (!it.isReusable()) {
            it = build();
        }
        it.init(aDomain);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final IntervalIntDomain aDomain) {
        super.init();
        this.domain = aDomain;
        value = aDomain.getInf() - 1;
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
        return value < domain.getSup();
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
        return ++value;
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
