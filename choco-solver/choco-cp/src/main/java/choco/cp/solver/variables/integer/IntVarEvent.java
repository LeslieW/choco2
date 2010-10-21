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
package choco.cp.solver.variables.integer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.structure.Couple;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.propagation.listener.IntPropagator;

@SuppressWarnings({"unchecked"})
public class IntVarEvent<C extends AbstractSConstraint & IntPropagator> extends VarEvent<IntDomainVarImpl> {

    public static int add, pop;

    /**
     * Constants for the <i>eventType</i> bitvector: index of bit for updates to lower bound of IntVars
     */
    public static final int INCINF = 0;

    /**
     * Constants for the <i>eventType</i> bitvector: index of bit for updates to upper bound of IntVars
     */
    public static final int DECSUP = 1;

    /**
     * Constants for the <i>eventType</i> bitvector: index of bit for holes in the domain of IntVars
     */
    public static final int REMVAL = 2;

    /**
     * Constants for the <i>eventType</i> bitvector: index of bit for instantiations of IntVars
     */
    public static final int INSTINT = 3;

    /**
     * Constants for the <i>eventType</i> bitvector: value of bitvector no eventtype
     */
    public static final int NO_MASK = 0;

    /**
     * Constants for the <i>eventType</i> bitvector: value of bitvector for updates to lower bound of IntVars
     */
    public static final int INCINF_MASK = 1;
    @Deprecated
    public static final int INCINFbitvector = INCINF_MASK;

    /**
     * Constants for the <i>eventType</i> bitvector: value of bitvector for updates to upper bound of IntVars
     */
    public static final int DECSUP_MASK = 2;
    @Deprecated
    public static final int DECSUPbitvector = DECSUP_MASK;

    /**
     * Constants for the <i>eventType</i> bitvector: value of bitvector for updates to both bound of IntVars
     */
    public static final int BOUNDS_MASK = 3;
    @Deprecated
    public static final int BOUNDSbitvector = BOUNDS_MASK;

    /**
     * Constants for the <i>eventType</i> bitvector: value of bitvector for holes in the domain of IntVars
     */
    public static final int REMVAL_MASK = 4;
    @Deprecated
    public static final int REMVALbitvector = REMVAL_MASK;

    /**
     * Constants for the <i>eventType</i> bitvector: value of bitvector for instantiations of IntVars
     */
    public static final int INSTINT_MASK = 8;
    @Deprecated
    public static final int INSTINTbitvector = INSTINT_MASK;


    public static final int[] EVENTS = new int[]{INCINF_MASK, DECSUP_MASK, REMVAL_MASK, INSTINT_MASK};

    public IntVarEvent(IntDomainVarImpl var) {
        super(var);
        eventType = EMPTYEVENT;
    }

    /**
     * useful for debugging
     */
    public String toString() {
        return ("VarEvt(" + modifiedVar + ")[" + eventType + ":"
                + ((eventType & INCINF_MASK) != 0 ? "I" : "")
                + ((eventType & DECSUP_MASK) != 0 ? "S" : "")
                + ((eventType & REMVAL_MASK) != 0 ? "r" : "")
                + ((eventType & INSTINT_MASK) != 0 ? "X" : "")
                + "]");
    }
    
    @Override
    public int getPriority() {
        return modifiedVar.getPriority();
    }

    /**
     * Clears the var: delegates to the basic events.
     */
    public void clear() {
        this.eventType = EMPTYEVENT;
//        oldCause = NOEVENT;
        cause = null;
        modifiedVar.getDomain().clearDeltaDomain();
    }

    /**
     * the event had been "frozen", (since the call to freeze), while it was handled by the propagation engine:
     * This meant that the meaning of the event could not be changed: it represented
     * a static set of value removals, during propagation.
     * Now, the event becomes "open" again: new value removals can be hosted, the delta domain can
     * accept that further values are removed.
     * In case value removals happened while the event was frozen, the release method returns false
     * (the event cannot be released, it must be handled once more). Otherwise (the standard behavior),
     * the method returns true
     */
    protected boolean release() {
        // we no longer use the shortcut (if eventType == EMPTYEVENT => nothing to do) because of event transformation
        // (in case a removal turned out to be an instantiation, we need to release the delta domain associated to the removal)
        // boolean anyUpdateSinceFreeze = ((eventType != EMPTYEVENT) || (cause != NOEVENT));  // note: these two tests should be equivalent
        // anyUpdateSinceFreeze = (anyUpdateSinceFreeze || !(getIntVar().getDomain().releaseDeltaDomain()));
        // return !anyUpdateSinceFreeze;
        return modifiedVar.getDomain().releaseDeltaDomain();
    }

    protected void freeze() {
        modifiedVar.getDomain().freezeDeltaDomain();
//        oldCause = NOEVENT;
        cause = null;
        eventType = EMPTYEVENT;
    }

    public boolean getReleased() {
        return modifiedVar.getDomain().getReleasedDeltaDomain();
    }

    /**
     * Returns an iterator over the set of removed values
     *
     * @return an iterator over the set of values that have been removed from the domain
     */
    public DisposableIntIterator getEventIterator() {
        return modifiedVar.getDomain().getDeltaIterator();
    }

    /**
     * Propagates the event through calls to the propagation engine.
     *
     * @return true if the event has been fully propagated (and can thus be discarded), false otherwise
     * @throws ContradictionException
     */
    public boolean propagateEvent() throws ContradictionException {
        pop++;
        // /!\ Logging statements really decrease performance
        //if(LOGGER.isLoggable(Level.FINER)) {LOGGER.log(Level.FINER, "propagate {0}", this);}
        // first, mark event
        int evtType = eventType;
//        int evtCause = oldCause;
        C evtCause = (C)cause;
        freeze();

        if ((propagatedEvents & INSTINT_MASK) != 0 && (evtType & INSTINT_MASK) != 0)
            propagateInstEvent(evtCause);
        if ((propagatedEvents & INCINF_MASK) != 0 && (evtType & INCINF_MASK) != 0)
            propagateInfEvent(evtCause);
        if ((propagatedEvents & DECSUP_MASK) != 0 && (evtType & DECSUP_MASK) != 0)
            propagateSupEvent(evtCause);
        if ((propagatedEvents & REMVAL_MASK) != 0 && (evtType & REMVAL_MASK) != 0)
            propagateRemovalsEvent(evtCause);

        // last, release event
        return release();
    }

    /**
     * Propagates the instantiation event
     */
    public void propagateInstEvent(C evtCause) throws ContradictionException {
        IntDomainVarImpl v = modifiedVar;
        DisposableIterator<Couple<C>> cit = v.getActiveConstraints(INSTINT_MASK, evtCause);
        try {
            while (cit.hasNext()) {
                Couple<C> cc = cit.next();
                cc.c.awakeOnInst(cc.i);
            }
        } finally {
            cit.dispose();
        }
    }


    /**
     * Propagates the update to the lower bound
     */
    public void propagateInfEvent(C evtCause) throws ContradictionException {
        IntDomainVarImpl v = modifiedVar;
        DisposableIterator<Couple<C>> cit = v.getActiveConstraints(INCINF_MASK, evtCause);
        try {
            while (cit.hasNext()) {
                Couple<C> cc = cit.next();
                cc.c.awakeOnInf(cc.i);
            }
        } finally {
            cit.dispose();
        }
    }

    /**
     * Propagates the update to the upper bound
     */
    public void propagateSupEvent(C evtCause) throws ContradictionException {
        IntDomainVarImpl v = modifiedVar;
        DisposableIterator<Couple<C>> cit = v.getActiveConstraints(DECSUP_MASK, evtCause);
        try {
            while (cit.hasNext()) {
                Couple<C> cc = cit.next();
                cc.c.awakeOnSup(cc.i);
            }
        } finally {
            cit.dispose();
        }
    }

    /**
     * Propagates a set of value removals
     */
    public void propagateRemovalsEvent(C evtCause) throws ContradictionException {
        IntDomainVarImpl v = modifiedVar;
        DisposableIterator<Couple<C>> cit = v.getActiveConstraints(REMVAL_MASK, evtCause);
        try {
            while (cit.hasNext()) {
                Couple<C> cc = cit.next();
                DisposableIntIterator iter = this.getEventIterator();
                try {
                    cc.c.awakeOnRemovals(cc.i, iter);
                } finally {
                    iter.dispose();
                }
            }
        } finally {
            cit.dispose();
        }
    }

    private int promoteEvent(int basicEvt) {
        switch (basicEvt) {
            case INSTINT:
                return INSTINT_MASK + INCINF_MASK + DECSUP_MASK + REMVAL_MASK;

            case INCINF:
                return INCINF_MASK + REMVAL_MASK;

            case DECSUP:
                return DECSUP_MASK + REMVAL_MASK;

            case REMVAL:
                return REMVAL_MASK;

            default:
                return 1 << basicEvt;
        }
    }

    public void recordEventTypeAndCause(int basicEvt, final SConstraint constraint, final boolean forceAwake) {
        add++;
        // if no such event was active on the same variable
//        if ((oldCause == NOEVENT) || (eventType == EMPTYEVENT)) {  // note: these two tests should be equivalent
        if (eventType == EMPTYEVENT) {
//            assert((cause == null));
            // the varevent is reduced to basicEvt, and the cause is recorded
            eventType = promoteEvent(basicEvt);
            if(!forceAwake){
                cause = constraint;
            }
//            cause = constraint;
        } else {
            // otherwise, this basic event is added to all previous updates that are possibly mending on the same variable
            eventType = (eventType | promoteEvent(basicEvt));
            // in case the cause of this update is different from the previous cause, all causes are forgotten
            // (so that the constraints that caused the event will be reawaken)
            if (cause != constraint) {
                cause = null;
            }
        }
    }
}