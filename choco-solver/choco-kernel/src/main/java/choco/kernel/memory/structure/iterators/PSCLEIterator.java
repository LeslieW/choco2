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

import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public final class PSCLEIterator<C extends AbstractSConstraint> extends DisposableIterator<Couple<C>> {


    /**
     * The inner class is referenced no earlier (and therefore loaded no earlier by the class loader)
     * than the moment that getInstance() is called.
     * Thus, this solution is thread-safe without requiring special language constructs.
     * see http://en.wikipedia.org/wiki/Singleton_pattern
     */
    private static final class Holder {
        private Holder() {
        }

        private static final Queue<PSCLEIterator> container = Disposable.createContainer();
    }

    private C cstrCause;

    private DisposableIntIterator cit;

    private PartiallyStoredIntVector event;

    private PartiallyStoredVector<C> elements;

    private PartiallyStoredIntVector indices;

    private final Couple<C> cc = new Couple<C>();

    private PSCLEIterator() {
    }

    private static PSCLEIterator build() {
        return new PSCLEIterator();
    }

    @SuppressWarnings({"unchecked"})
    public synchronized static <C extends AbstractSConstraint> PSCLEIterator getIterator(
            final PartiallyStoredIntVector event, final C cstrCause,
            final PartiallyStoredVector<C> elements,
            final PartiallyStoredIntVector indices
    ) {
        PSCLEIterator it;
        try{
            it = Holder.container.remove();
        }catch (NoSuchElementException e){
            it = build();
        }
        it.init(cstrCause, event, elements, indices);
        return it;
    }

    private void init(final C aCause, final PartiallyStoredIntVector anEvent,
                      final PartiallyStoredVector<C> someElements, final PartiallyStoredIntVector someIndices) {
        init();
        this.event = anEvent;
        this.cit = this.event.getIndexIterator();
        this.cstrCause = aCause;
        this.elements = someElements;
        this.indices = someIndices;
    }

    /**
     * This method allows to declare that the iterator is not usefull anymoure. It
     * can be reused by another object.
     */
    @Override
    public void dispose() {
        cit.dispose();
        super.dispose();
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
            final int idx = event.get(cit.next());
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
        cit.remove();
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
