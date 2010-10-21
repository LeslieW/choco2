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
package choco.kernel.memory.structure.iterators;

import choco.kernel.common.util.disposable.Disposable;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.structure.Couple;
import choco.kernel.memory.structure.PartiallyStoredIntVector;
import choco.kernel.memory.structure.PartiallyStoredVector;
import choco.kernel.solver.constraints.AbstractSConstraint;

import java.util.Queue;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 26 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public final class PSCLIterator<C extends AbstractSConstraint> extends DisposableIterator<Couple<C>> {

    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static final Queue<PSCLIterator> container = Disposable.createContainer();
    }

    private C cstrCause;

    private DisposableIntIterator cit;

    private PartiallyStoredVector<C> elements;

    private PartiallyStoredIntVector indices;

    private final Couple<C> cc = new Couple<C>();


    private PSCLIterator() {
    }

    private static PSCLIterator build() {
        return new PSCLIterator();
    }

    @SuppressWarnings({"unchecked"})
    public static <C extends AbstractSConstraint> PSCLIterator getIterator(
            final PartiallyStoredVector<C> someElements,
            final PartiallyStoredIntVector someIndices,
            final C aCause, final DisposableIntIterator aCit) {
        PSCLIterator it;
        synchronized (Holder.container) {
            if (Holder.container.isEmpty()) {
                it = build();
            } else {
                it = Holder.container.remove();
            }
        }
        it.init(someElements, someIndices, aCause, aCit);
        return it;
    }

    /**
     * Freeze the iterator, cannot be reused.
     */
    public void init(final PartiallyStoredVector<C> someElements, final PartiallyStoredIntVector someIndices,
                     final C aCause, final DisposableIntIterator aCit) {
        init();
        this.elements = someElements;
        this.indices = someIndices;
        this.cit = aCit;
        this.cstrCause = aCause;
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
        while (cit.hasNext()) {
            final int idx = cit.next();
            final C cstr = elements.get(idx);
            if (cstr != cstrCause && cstr.isActive()) {
                cc.init(cstr, indices.get(idx));
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @throws java.util.NoSuchElementException
     *          iteration has no more elements.
     */
    @Override
    public Couple<C> next() {
        return cc;
    }


    /**
     * This method allows to declare that the iterator is not used anymoure. It
     * can be reused by another object.
     */
    @Override
    public void dispose() {
        cit.dispose();
        super.dispose();
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
