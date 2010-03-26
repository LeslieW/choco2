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

import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.structure.iterators.PSCLEIterator;
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

    @SuppressWarnings({"unchecked"})
    public DisposableIterator<Couple<C>> getActiveConstraint(C cstrCause){
        return PSCLEIterator.getIterator(events,cstrCause, this.elements, this.indices);
    }
}