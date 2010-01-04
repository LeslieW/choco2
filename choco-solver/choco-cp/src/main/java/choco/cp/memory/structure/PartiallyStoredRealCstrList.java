/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package choco.cp.memory.structure;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.structure.APartiallyStoredCstrList;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.real.RealSConstraint;
import static choco.kernel.solver.propagation.VarEvent.CHECK_ACTIVE;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 21 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public final class PartiallyStoredRealCstrList<C extends RealSConstraint> extends APartiallyStoredCstrList<C> {

    public PartiallyStoredRealCstrList(IEnvironment env) {
        super(env);
    }

    @Override
    public void updateConstraintState(int vidx, int cidx, SConstraint c, boolean state) {}

    private QuickIterator _quickIterator = null;

    public DisposableIterator<Couple> getActiveConstraint(int cstrCause){
        QuickIterator iter = _quickIterator;
        if (iter != null && iter.reusable) {
            iter.init(cstrCause);
            return iter;
        }
        _quickIterator = new QuickIterator(cstrCause);
        return _quickIterator;
    }

    private final class QuickIterator extends DisposableIterator<Couple> {
        boolean reusable;
        int cstrCause;
        DisposableIntIterator cit;
        Couple<RealSConstraint> cc  = new Couple<RealSConstraint>();


        public QuickIterator(int cstrCause) {
             init(cstrCause);
        }

        public void init(int cstrCause){
            super.init();
            cit = elements.getIndexIterator();
            this.cstrCause = cstrCause;
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
                int idx = cit.next();
                if (idx != cstrCause) {
                    if(CHECK_ACTIVE){
                        if (elements.get(idx).isActive()) {
                            cc.init(elements.get(idx), indices.get(idx));
                            return true;
                        }
                    }else{
                        cc.init(elements.get(idx), indices.get(idx));
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
        public Couple next() {
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
}