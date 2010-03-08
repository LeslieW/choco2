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
package choco.kernel.memory.structure;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.propagation.Propagator;
import static choco.kernel.solver.propagation.event.TaskVarEvent.HYPDOMMODbitvector;
import choco.kernel.solver.propagation.listener.TaskPropagator;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 21 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public final class PartiallyStoredTaskCstrList<C extends AbstractSConstraint & TaskPropagator> extends APartiallyStoredCstrList<C>{

    private final PartiallyStoredIntVector events;

    private final IStateInt priority;

    public PartiallyStoredTaskCstrList(IEnvironment env) {
        super(env);
        events = env.makePartiallyStoredIntVector();
        priority = env.makeInt(0);
    }


    /**
	 * Adds a new constraints on the stack of constraints
	 * the addition can be dynamic (undone upon backtracking) or not.
	 *
	 * @param c               the constraint to add
	 * @param varIdx          the variable index accrding to the added constraint
	 * @param dynamicAddition states if the addition is definitic (cut) or
	 *                        subject to backtracking (standard constraint)
	 * @return the index affected to the constraint according to this variable
	 */
	public int addConstraint(SConstraint c, int varIdx, boolean dynamicAddition) {
		int constraintIdx = super.addConstraint(c, varIdx, dynamicAddition);
        AbstractSConstraint ic = ((AbstractSConstraint)c);
		computePriority(ic);
		int mask = ic.getFilteredEventMask(varIdx);
        if((mask & HYPDOMMODbitvector) != 0){
            addEvent(dynamicAddition, constraintIdx);
		}
		return constraintIdx;
	}

    /**
	 * Compute the priotity of the variable
	 * @param c the new constraint
	 */
	private void computePriority(SConstraint c) {
		priority.set(Math.max(priority.get(),((Propagator)c).getPriority()));
	}

    /**
	 * Add event to the correct partially stored int vector
	 * @param dynamicAddition static or dynamic constraint
     * @param constraintIdx index of the constraint
     */
	private void addEvent(boolean dynamicAddition, int constraintIdx) {
		if (dynamicAddition) {
			events.add(constraintIdx);
		} else {
			events.staticAdd(constraintIdx);
		}
	}

    /**
	 * Removes (permanently) a constraint from the list of constraints
	 * connected to the variable.
	 *
	 * @param c the constraint that should be removed from the list this variable
	 *          maintains.
	 */
	public int eraseConstraint(SConstraint c) {
		int idx = super.eraseConstraint(c);
		int mask = ((AbstractIntSConstraint)c).getFilteredEventMask(indices.get(idx));
		if((mask & HYPDOMMODbitvector) != 0){
			events.remove(idx);
		}
        return idx;
	}

    public PartiallyStoredIntVector getEventsVector(){
		return events;
	}

	public int getPriority() {
		return priority.get();
	}

    private QuickIterator _quickIterator = null;

    public DisposableIterator<Couple<C>> getActiveConstraint(C cstrCause){
        QuickIterator iter = _quickIterator;
        if (iter != null && iter.reusable) {
            iter.init(events, cstrCause);
            return iter;
        }
        _quickIterator = new QuickIterator(events, cstrCause);
        return _quickIterator;
    }

    private final class QuickIterator extends DisposableIterator<Couple<C>> {
        boolean reusable;
        PartiallyStoredIntVector event;
        C cstrCause;
        DisposableIntIterator cit;
        Couple<C> cc  = new Couple<C>();


        public QuickIterator(PartiallyStoredIntVector event, C cstrCause) {
             init(event, cstrCause);
        }

        public void init(PartiallyStoredIntVector event, C cstrCause){
            super.init();
            this.event = event;
            cit = event.getIndexIterator();
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
}