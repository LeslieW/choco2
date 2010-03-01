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

import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.structure.Couple;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.listener.TaskPropagator;
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

    public final static int HYPDOMMOD = 0;

    public final static int HYPDOMMODbitvector = 1;


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
        int evtType = eventType;
		int evtCause = cause;

        if ((propagatedEvents & HYPDOMMODbitvector) != 0 && (evtType & HYPDOMMODbitvector) != 0)
            propagateHypDomModEvent(evtCause);
        
        cause = NOEVENT;
        eventType = EMPTYEVENT;
        return false;
    }

    /**
	 * Propagates the instantiation event
	 */
	public void propagateHypDomModEvent(int evtCause) throws ContradictionException {
		TaskVar v = getModifiedVar();
        DisposableIterator<Couple<? extends TaskPropagator>> cit = v.getActiveConstraints(evtCause);
        try{
            while(cit.hasNext()){
                Couple<? extends TaskPropagator> cc = cit.next();
                cc.c.awakeOnHypDomMod(cc.i);
            }
        }finally{
            cit.dispose();
        }
	}
}
