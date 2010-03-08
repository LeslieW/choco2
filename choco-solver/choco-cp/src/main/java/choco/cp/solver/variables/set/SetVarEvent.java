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
package choco.cp.solver.variables.set;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.structure.Couple;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.propagation.listener.SetPropagator;

/*
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 6 juin 2004
 * Since : Choco 2.0.0
 *
 */
@SuppressWarnings({"unchecked"})
public class SetVarEvent <C extends AbstractSConstraint & SetPropagator> extends VarEvent<SetVarImpl> {

	/**
	 * Constants for the <i>eventType</i> bitvector: index of bit for events on SetVars
	 */
	public static final int REMENV = 0;
	public static final int ADDKER = 1;
	public static final int INSTSET = 2;

	public static final int ENVEVENT = 1;
	public static final int KEREVENT = 2;
	public static final int BOUNDSEVENT = 3;
	public static final int INSTSETEVENT = 4;

	public SetVarEvent(SetVarImpl var) {
		super(var);
		eventType = EMPTYEVENT;
	}

	/**
	 * useful for debugging
	 */
	public String toString() {
		return ("VarEvt(" + modifiedVar.toString() + ")[" + eventType + ":"
				+ ((eventType & ENVEVENT) != 0 ? "E" : "")
				+ ((eventType & KEREVENT) != 0 ? "K" : "")
				+ ((eventType & INSTSETEVENT) != 0 ? "X" : "")
				+ "]");
	}

	/**
	 * Clears the var: delegates to the basic events.
	 */
	public void clear() {
		this.eventType = EMPTYEVENT;
        cause = null;
		(modifiedVar.getDomain()).getEnveloppeDomain().clearDeltaDomain();
		(modifiedVar.getDomain()).getKernelDomain().clearDeltaDomain();
	}


	protected void freeze() {
		(modifiedVar.getDomain()).getEnveloppeDomain().freezeDeltaDomain();
		(modifiedVar.getDomain()).getKernelDomain().freezeDeltaDomain();
		cause = null;
		eventType = 0;
	}

	protected boolean release() {
		return modifiedVar.getDomain().getEnveloppeDomain().releaseDeltaDomain() &&
		modifiedVar.getDomain().getKernelDomain().releaseDeltaDomain();
	}

	public boolean getReleased() {
		return (modifiedVar.getDomain()).getEnveloppeDomain().getReleasedDeltaDomain() &&
		(modifiedVar.getDomain()).getKernelDomain().getReleasedDeltaDomain();
	}

	public DisposableIntIterator getEnvEventIterator() {
		return ( modifiedVar.getDomain()).getEnveloppeDomain().getDeltaIterator();
	}

	public DisposableIntIterator getKerEventIterator() {
		return (modifiedVar.getDomain()).getKernelDomain().getDeltaIterator();
	}

	/**
	 * Propagates the event through calls to the propagation engine.
	 *
	 * @return true if the event has been fully propagated (and can thus be discarded), false otherwise
	 * @throws choco.kernel.solver.ContradictionException
	 */
	@Override
	public boolean propagateEvent() throws ContradictionException {
		// /!\  Logging statements really decrease performance
		//if(LOGGER.isLoggable(Level.FINER)) {LOGGER.log(Level.FINER, "propagate {0}", this);}
		// first, mark event
		int evtType = eventType;
		C evtCause = (C)cause;
		freeze();

        if ((propagatedEvents & INSTSETEVENT) != 0 && (evtType & INSTSETEVENT) != 0)
            propagateInstEvent(evtCause);
        if ((propagatedEvents & ENVEVENT) != 0 && (evtType & ENVEVENT) != 0)
            propagateEnveloppeEvents(evtCause);
        if ((propagatedEvents & KEREVENT) != 0 && (evtType & KEREVENT) != 0)
            propagateKernelEvents(evtCause);

//		if (evtType >= INSTSETEVENT)
//			propagateInstEvent(evtCause);
//		else if (evtType <= BOUNDSEVENT) {
//			if (evtType == ENVEVENT)
//				propagateEnveloppeEvents(evtCause);
//			else if (evtType == KEREVENT)
//				propagateKernelEvents(evtCause);
//			else if (evtType == BOUNDSEVENT) {
//				propagateKernelEvents(evtCause);
//				propagateEnveloppeEvents(evtCause);
//			}
//		}

		// last, release event
		return release();
	}

	/**
	 * Propagates the instantiation event
	 */
	public void propagateInstEvent(C evtCause) throws ContradictionException {
		SetVarImpl v = getModifiedVar();
        DisposableIterator<Couple<C>> cit = v.getActiveConstraints(evtCause);

        try{
            while(cit.hasNext()){
                Couple<C> cc = cit.next();
                cc.c.awakeOnInst(cc.i);
            }
        }finally{
            cit.dispose();
        }
	}

	/**
	 * Propagates a set of value removals
	 */
	public void propagateKernelEvents(C evtCause) throws ContradictionException {
		SetVarImpl v = getModifiedVar();
        DisposableIterator<Couple<C>> cit = v.getActiveConstraints(evtCause);

        try{
            while(cit.hasNext()){
                Couple<C> cc = cit.next();
                cc.c.awakeOnkerAdditions(cc.i, this.getKerEventIterator());
            }
        }finally{
            cit.dispose();
        }
	}

	/**
	 * Propagates a set of value removals
	 */
	public void propagateEnveloppeEvents(C evtCause) throws ContradictionException {
		SetVarImpl v = getModifiedVar();
        DisposableIterator<Couple<C>> cit = v.getActiveConstraints(evtCause);

        try{
            while(cit.hasNext()){
                Couple<C> cc = cit.next();
                cc.c.awakeOnEnvRemovals(cc.i, this.getEnvEventIterator());

            }
        }finally {
            cit.dispose();
        }
	}

}
