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
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.propagation;

import choco.cp.memory.structure.Couple;
import choco.cp.solver.variables.real.RealVarImpl;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.propagation.listener.RealPropagator;

/**
 * An event for real interval variable modifications.
 */
public class RealVarEvent extends VarEvent<RealVarImpl> {
    public static final int INCINF = 0;
    public static final int DECSUP = 1;

    public final static int EMPTYEVENT = 0;
    public final static int INFEVENT = 1;
    public final static int SUPEVENT = 2;
    public final static int BOUNDSEVENT = 3;

    public RealVarEvent(RealVarImpl var) {
        super(var);
    }

    public String toString() {
        return ("VarEvt(" + modifiedVar.toString() + ")[" + eventType + ":"
                + ((eventType & INFEVENT) != 0 ? "I" : "")
                + ((eventType & SUPEVENT) != 0 ? "S" : "")
                + "]");
    }

    public void clear() {
        this.eventType = EMPTYEVENT;
        modifiedVar.getDomain().clearDeltaDomain();
    }

    protected boolean release() {
        return modifiedVar.getDomain().releaseDeltaDomain();
    }

    protected void freeze() {
        modifiedVar.getDomain().freezeDeltaDomain();
        cause = NOEVENT;
        eventType = 0;
    }

    public boolean getReleased() {
        return modifiedVar.getDomain().getReleasedDeltaDomain();
    }

    public boolean propagateEvent() throws ContradictionException {
        //Logging statements really decrease performance
        //if(LOGGER.isLoggable(Level.FINER)) LOGGER.log(Level.FINER,"propagate {0}", this);
        // first, mark event
        int evtType = eventType;
        int evtCause = cause;
        freeze();

        if ((propagatedEvents & INFEVENT) != 0 && (evtType & INFEVENT) != 0)
            propagateInfEvent(evtCause);
        if ((propagatedEvents & SUPEVENT) != 0 && (evtType & SUPEVENT) != 0)
            propagateSupEvent(evtCause);

//        if (evtType <= BOUNDSEVENT) {     // only two first bits (bounds) are on
//            if (evtType == INFEVENT)
//                propagateInfEvent(evtCause);
//            else if (evtType == SUPEVENT)
//                propagateSupEvent(evtCause);
//            else if (evtType == BOUNDSEVENT) {
//                propagateBoundsEvent(evtCause);
//            }
//        }
        // last, release event
        return release();
    }

    /**
     * Propagates the update to the upper bound
     */
    public void propagateSupEvent(int evtCause) throws ContradictionException {
        RealVarImpl v = getModifiedVar();
        DisposableIterator<Couple<? extends RealPropagator>> cit = v.getActiveConstraints(evtCause);

        try {
            while (cit.hasNext()) {
                Couple<? extends RealPropagator> cc = cit.next();
                cc.c.awakeOnSup(cc.i);
            }
        } finally {
            cit.dispose();
        }
    }

    /**
     * Propagates the update to the lower bound
     */
    public void propagateInfEvent(int evtCause) throws ContradictionException {
        RealVarImpl v = getModifiedVar();
        DisposableIterator<Couple<? extends RealPropagator>> cit = v.getActiveConstraints(evtCause);

        try {
            while (cit.hasNext()) {
                Couple<? extends RealPropagator> cc = cit.next();
                cc.c.awakeOnInf(cc.i);
            }
        } finally {
            cit.dispose();
        }

    }
}
