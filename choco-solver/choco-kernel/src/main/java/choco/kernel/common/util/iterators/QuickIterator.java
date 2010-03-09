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
package choco.kernel.common.util.iterators;

import choco.kernel.memory.structure.Couple;
import choco.kernel.memory.structure.PartiallyStoredIntVector;
import choco.kernel.memory.structure.PartiallyStoredVector;
import choco.kernel.solver.constraints.AbstractSConstraint;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class QuickIterator<C extends AbstractSConstraint> extends DisposableIterator<Couple<C>> {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// STATIC ///////////////////////////////////////////////////////////////
    static QuickIterator _quickIterator = null;

    @SuppressWarnings({"unchecked"})
    public static <C extends AbstractSConstraint> QuickIterator getIterator(final PartiallyStoredIntVector event, final C cstrCause,
                                         final PartiallyStoredVector<C> elements, final PartiallyStoredIntVector indices) {
        QuickIterator iter = _quickIterator;
        if (iter != null && iter.reusable) {
            iter.init(cstrCause, event, elements, indices);
            return iter;
        }
        _quickIterator = new QuickIterator(cstrCause, event, elements, indices);
        return _quickIterator;
    }
    ////////////////////////////////////////////\ STATIC ///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    C cstrCause;
    DisposableIntIterator cit;
    PartiallyStoredIntVector event;
    Couple<C> cc = new Couple<C>();
    PartiallyStoredVector<C> elements;
    PartiallyStoredIntVector indices;


    private QuickIterator(final C cstrCause, final PartiallyStoredIntVector event,
                          final PartiallyStoredVector<C> elements, final PartiallyStoredIntVector indices) {
        init(cstrCause, event, elements, indices);
    }

    private void init(C cstrCause, final PartiallyStoredIntVector event,
                      final PartiallyStoredVector<C> elements, final PartiallyStoredIntVector indices) {
        super.init();
        this.event = event;
        cit = this.event.getIndexIterator();
        this.cstrCause = cstrCause;
        this.elements = elements;
        this.indices = indices;
    }

    /**
     * This method allows to declare that the iterator is not usefull anymoure. It
     * can be reused by another object.
     */
    @Override
    public void dispose() {
        super.dispose();
        cit.dispose();
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
            int idx = event.get(cit.next());
            final C cstr = elements.get(idx);
            if (cstr != cstrCause) {
                if (cstr.isActive()) {
                    cc.init(cstr, indices.get(idx));
                    return true;
                }
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
}
