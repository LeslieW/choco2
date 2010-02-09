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

import choco.cp.solver.variables.integer.IntVarEvent;
import static choco.cp.solver.variables.integer.IntVarEvent.*;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.structure.APartiallyStoredCstrList;
import choco.kernel.memory.structure.PartiallyStoredActiveIntVector;
import choco.kernel.memory.structure.PartiallyStoredIntVector;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.IntSConstraint;
import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.propagation.event.VarEvent;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 21 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public final class PartiallyStoredIntCstrList <C extends IntSConstraint> extends APartiallyStoredCstrList<C>{

    private final PartiallyStoredIntVector[] events;

    private final IStateInt priority;

    public PartiallyStoredIntCstrList(IEnvironment env) {
        super(env);
        events = new PartiallyStoredIntVector[4];
		for(int i = 0; i < 4; i++){
			if(VarEvent.CHECK_ACTIVE){
                events[i] = env.makePartiallyStoredIntVector();
            }else{
                events[i] = new PartiallyStoredActiveIntVector(env);
            }
		}
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
        IntSConstraint ic = ((IntSConstraint)c);
		computePriority(ic);
		int mask = ic.getFilteredEventMask(varIdx);
		if((mask & INSTINTbitvector) != 0){
			if(CHECK_ACTIVE){
                addEvent(dynamicAddition, 0, constraintIdx);
            }else{
                addEvent(dynamicAddition, 0, constraintIdx, ic.isActive());
            }
		}
		if((mask & INCINFbitvector) != 0){
			if(CHECK_ACTIVE){
                addEvent(dynamicAddition, 1, constraintIdx);
            }else{
                addEvent(dynamicAddition, 1, constraintIdx, ic.isActive());
            }
		}
		if((mask & DECSUPbitvector) != 0){
			if(CHECK_ACTIVE){
                addEvent(dynamicAddition, 2, constraintIdx);
            }else{
                addEvent(dynamicAddition, 2, constraintIdx, ic.isActive());
            }
		}
		if((mask & REMVALbitvector) != 0){
			if(CHECK_ACTIVE){
                addEvent(dynamicAddition, 3, constraintIdx);
            }else{
                addEvent(dynamicAddition, 3, constraintIdx, ic.isActive());
            }
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
	 * @param indice indice of teh event type
	 * @param constraintIdx index of the constraint
	 */
	private void addEvent(boolean dynamicAddition, int indice, int constraintIdx) {
		if (dynamicAddition) {
			events[indice].add(constraintIdx);
		} else {
			events[indice].staticAdd(constraintIdx);
		}
	}

    /**
	 * Add event to the correct partially stored int vector
	 * @param dynamicAddition static or dynamic constraint
	 * @param indice indice of teh event type
	 * @param constraintIdx index of the constraint
     * @param active is the constraint active?
	 */
	private void addEvent(boolean dynamicAddition, int indice, int constraintIdx, boolean active) {
		if (dynamicAddition) {
			((PartiallyStoredActiveIntVector)events[indice]).add(constraintIdx, active);
		} else {
			((PartiallyStoredActiveIntVector)events[indice]).staticAdd(constraintIdx, active);
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
		int mask = ((IntSConstraint)c).getFilteredEventMask(indices.get(idx));
		if((mask & INSTINTbitvector) != 0){
			events[0].remove(idx);
		}
		if((mask & INCINFbitvector) != 0){
			events[1].remove(idx);
		}
		if((mask & DECSUPbitvector) != 0){
			events[2].remove(idx);
		}
		if((mask & REMVALbitvector) != 0){
			events[3].remove(idx);
		}
        return idx;
	}

    public PartiallyStoredIntVector[] getEventsVector(){
		return events;
	}

	public int getPriority() {
		return priority.get();
	}

    /**
     * Update the constraint state
     *
     * @param vidx  index of the variable in the constraint
     * @param cidx  constraint idx
     * @param c     the constraint
     * @param state new state (active/passive)
     */
    public void updateConstraintState(int vidx, int cidx, SConstraint c, boolean state) {
        if(!IntVarEvent.CHECK_ACTIVE){
            int mask = ((IntSConstraint)c).getFilteredEventMask(vidx);
            if((mask & INSTINTbitvector) != 0){
                ((PartiallyStoredActiveIntVector)events[0]).set(cidx, state);
            }
            if((mask & INCINFbitvector) != 0){
                ((PartiallyStoredActiveIntVector)events[1]).set(cidx, state);
            }
            if((mask & DECSUPbitvector) != 0){
                ((PartiallyStoredActiveIntVector)events[2]).set(cidx, state);
            }
            if((mask & REMVALbitvector) != 0){
                ((PartiallyStoredActiveIntVector)events[3]).set(cidx, state);
            }
        }
    }

    private QuickIterator _quickIterator = null;

    public DisposableIterator<Couple> getActiveConstraint(int event, int cstrCause){
        QuickIterator iter = _quickIterator;
        if (iter != null && iter.reusable) {
            iter.init(events[event], cstrCause);
            return iter;
        }
        _quickIterator = new QuickIterator(events[event], cstrCause);
        return _quickIterator;
    }

    private final class QuickIterator extends DisposableIterator<Couple> {
        boolean reusable;
        PartiallyStoredIntVector event;
        int cstrCause;
        DisposableIntIterator cit;
        Couple<IntSConstraint> cc  = new Couple<IntSConstraint>();


        public QuickIterator(PartiallyStoredIntVector event, int cstrCause) {
             init(event, cstrCause);
        }

        public void init(PartiallyStoredIntVector event, int cstrCause){
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
                int idx = CHECK_ACTIVE?event.get(cit.next()):cit.next();
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
