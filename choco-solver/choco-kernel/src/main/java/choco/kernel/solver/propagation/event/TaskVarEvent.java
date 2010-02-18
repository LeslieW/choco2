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
package choco.kernel.solver.propagation.event;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.structure.PartiallyStoredVector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 18 févr. 2010
 * Since : Choco 2.1.1
 *
 * Use carefully.
 * !! This is required ONLY for transverse constraints!!
 * Normal behaviour of task var modification should be based on IntVarEvent.
 */
public class TaskVarEvent extends VarEvent<TaskVar> {

    public final static int TASK_AWAKE = 1;


    /**
     * Constructs a variable event for the specified variable and with the given
     * basic events.
     */
    public TaskVarEvent(TaskVar var) {
        super(var);
    }


    /**
     * Clears the var: delegates to the basic events.
     */
    @Override
    public void clear() {
        this.eventType = EMPTYEVENT;
        cause = NOEVENT;
    }

    /**
     * Propagates the event through calls to the propagation engine.
     *
     * @return true if the event has been fully propagated (and can thus be discarded), false otherwise
     * @throws choco.kernel.solver.ContradictionException
     *
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public boolean propagateEvent() throws ContradictionException {
        PartiallyStoredVector<SConstraint> cs = modifiedVar.getConstraintVector();
        DisposableIntIterator it = cs.getIndexIterator();
        for(;it.hasNext();){
            int idx = it.next();
            if(idx != cause){
                AbstractSConstraint<TaskVar> c = (AbstractSConstraint<TaskVar>)cs.get(idx);
                c.constAwake(false);
            }
        }
        cause = NOEVENT;
        eventType = EMPTYEVENT;
        return false;
    }
}
