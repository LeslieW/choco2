/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package choco.cp.solver.propagation;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.cp.solver.variables.set.SetVarEvent;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.AbstractPropagationEngine;
import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.propagation.event.ConstraintEvent;
import choco.kernel.solver.propagation.event.PropagationEvent;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.propagation.queue.AbstractConstraintEventQueue;
import choco.kernel.solver.propagation.queue.ConstraintEventQueue;
import choco.kernel.solver.propagation.queue.EventQueue;
import choco.kernel.solver.propagation.queue.VarEventQueue;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.set.SetVar;

import java.util.logging.Level;

/**
 * Implementation of an {@link choco.kernel.solver.propagation.AbstractPropagationEngine} for Choco.
 */
public class ChocoEngine extends AbstractPropagationEngine {


	/**
	 * the number of queues for storing constraint events
	 */
	protected final static int NB_CONST_QUEUES = 1;

	/**
	 * the number of queues for storing variables events
	 */
	protected final static int NB_VAR_QUEUES = 3;

	/**
	 * The different queues for the constraint awake events.
	 */

	private ConstraintEventQueue[] constEventQueues;

	/**
	 * Number of pending init constraint awake events.
	 */

	protected int nbPendingInitConstAwakeEvent;

	/**
	 * The queue with all the variable events.
	 */

	protected VarEventQueue[] varEventQueue;

	


	/**
	 * Constructs a new engine by initializing the var queues.
	 * @param solver Solver master
	 */

	public ChocoEngine(Solver solver) {
		super(solver);
		constEventQueues = new ConstraintEventQueue[NB_CONST_QUEUES];
		for (int i = 0; i < NB_CONST_QUEUES; i++) {
			constEventQueues[i] = new ConstraintEventQueue(this);
		}
		varEventQueue = new VarEventQueue[NB_VAR_QUEUES];
		for (int i = 0; i < NB_VAR_QUEUES; i++) {
			varEventQueue[i] = EventQueueFactory.getVarEventQueue(solver.getEventQueueType());
		}
		nbPendingInitConstAwakeEvent = 0;
	}

    /**
     * Clear datastructures for safe reuses
     */
    public void clear(){
		for (int i = 0; i < NB_CONST_QUEUES; i++) {
			constEventQueues[i].clear();
		}
		for (int i = 0; i < NB_VAR_QUEUES; i++) {
			varEventQueue[i].clear();
		}
		nbPendingInitConstAwakeEvent = 0;
    }

	/**
	 * Posts an IncInf event
	 *
	 * @param v   The variable the bound is modified.
     * @param constraint
     * @param forceAwake
     */

	public final void postUpdateInf(final IntDomainVar v, final SConstraint constraint, final boolean forceAwake) {
		postEvent(v, IntVarEvent.INCINF, constraint, forceAwake);
	}

	/**
	 * Posts a DecSup event
	 *
	 * @param v   The variable the bound is modified.
     * @param constraint
     * @param forceAwake
     */

	public final void postUpdateSup(final IntDomainVar v, final SConstraint constraint, final boolean forceAwake) {
		postEvent(v, IntVarEvent.DECSUP, constraint, forceAwake);
	}

	/**
	 * Private method for completing the bound var posting.
	 *
	 * @param basicEvt The basic event posted.
     * @param constraint
     * @param forceAwake
     */
	// idee: - si on est "frozen", devenir en plus "redondant" (ie: double).
	//       - par ailleurs, noter le changement (garder la vieille valeur de la borne ou
	//       - devenir enqueued
	public final void postEvent(final Var v, final int basicEvt, final SConstraint constraint, final boolean forceAwake) {
		VarEvent<? extends Var> event = v.getEvent();
		boolean alreadyEnqueued = event.isEnqueued();
		event.recordEventTypeAndCause(basicEvt, constraint, forceAwake);
		if (!alreadyEnqueued) {
			varEventQueue[event.getPriority()].pushEvent(event);
		} else {
			// no priority anymore
			//varEventQueue.updatePriority(event);
		}
	}

	/**
	 * Posts an Inst var.
	 *
	 * @param v   The variable that is instantiated.
     * @param constraint
     * @param forceAwake
     */

	public final void postInstInt(final IntDomainVar v, final SConstraint constraint, final boolean forceAwake) {
		postEvent(v, IntVarEvent.INSTINT, constraint, forceAwake);
	}


	/**
	 * Posts an Remove var.
	 *
	 * @param v   The variable the value is removed from.
     * @param constraint
     * @param forceAwake
     */

	public final void postRemoveVal(final IntDomainVar v, int x, final SConstraint constraint, final boolean forceAwake) {
		postEvent(v, IntVarEvent.REMVAL, constraint, forceAwake);
	}

	/**
	 * Posts an lower bound event for a real variable.
	 *
	 * @param v the real variable
     * @param constraint
     * @param forceAwake
     */
	public final void postUpdateInf(final RealVar v, final SConstraint constraint, final boolean forceAwake) {
		postEvent(v, RealVarEvent.INCINF, constraint, forceAwake);
	}

	/**
	 * Posts an upper bound event for a real variable
	 *
	 * @param v real variable
     * @param constraint
     * @param forceAwake
     */
	public final void postUpdateSup(final RealVar v, final SConstraint constraint, final boolean forceAwake) {
		postEvent(v, RealVarEvent.DECSUP, constraint, forceAwake);
	}

	/**
	 * Posts a removal event on a set variable
	 *
	 * @param v   the variable the enveloppe is modified
     * @param constraint
     * @param forceAwake
     */
	public final void postRemEnv(final SetVar v, final SConstraint constraint, final boolean forceAwake) {
		postEvent(v, SetVarEvent.REMENV, constraint, forceAwake);
	}

	/**
	 * Posts a kernel addition event on a set variable
	 *
	 * @param v   the variable the kernel is modified
     * @param constraint
     * @param forceAwake
     */
	public final void postAddKer(final SetVar v, final SConstraint constraint, final boolean forceAwake) {
		postEvent(v, SetVarEvent.ADDKER, constraint, forceAwake);
	}

	/**
	 * Posts an Inst event on a set var.
	 *
	 * @param v   The variable that is instantiated.
     * @param constraint
     * @param forceAwake
     */

	public final void postInstSet(final SetVar v, final SConstraint constraint, final boolean forceAwake) {
		postEvent(v, SetVarEvent.INSTSET, constraint, forceAwake);
	}

	/**
	 * Posts a constraint awake var.
	 *
	 * @param constraint The constraint that must be awaken.
	 * @param init       Specifies if the constraint must be initialized
	 *                   (awake instead of propagate).
	 */

	public final boolean postConstAwake(final Propagator constraint, final boolean init) {
		final ConstraintEvent event = (ConstraintEvent) constraint.getEvent();
		final ConstraintEventQueue queue = this.getQueue(event);
		if (queue.pushEvent(event)) {
			event.setInitialized(!init);
			if (init) this.incPendingInitConstAwakeEvent();
			return true;
		} else
			return false;
	}


	
	private final static int NO_PRIORITY_IMPLEMENTED = 0;
	/**
	 * Gets the queue for a given priority of var.
	 *
	 * @param event The var for which the queue is searched.
	 */

	public final ConstraintEventQueue getQueue(final ConstraintEvent event) {
		// CHOCO_2.0.1: Tests have shown that taking priorities into account is not interesting for the moment...
		// int prio = event.getPriority();
//		int prio = 0;
//		if (prio < NB_CONST_QUEUES) {
//			return constEventQueues[prio];
//		} else {
//			LOGGER.warning("wrong constraint priority. It should be between 0 and 3.");
//			return constEventQueues[3];
//		}
		return constEventQueues[NO_PRIORITY_IMPLEMENTED];
	}


	/**
	 * Registers an event in the queue. It should be called before using the queue to add
	 * the var in the available events of the queue.
	 *
	 * @param event the event to register
	 */

	public final void registerEvent(final ConstraintEvent event) {
		final ConstraintEventQueue queue = this.getQueue(event);
		queue.add(event);
	}


	/**
	 * Returns the variables queues.
	 */

	public final VarEventQueue[] getVarEventQueues() {
		return varEventQueue;
	}

	/**
	 * Set Var Event Queues
	 * @param veqs array of variable event queues
	 */
	public final void setVarEventQueues(VarEventQueue[] veqs) {
		System.arraycopy(veqs, 0, varEventQueue, 0, varEventQueue.length);
	}

	public final void setVarEventQueues(int eventQueueType){
		for(int i = 0; i < varEventQueue.length; i++ ){
			varEventQueue[i] = EventQueueFactory.getVarEventQueue(eventQueueType);
		}
	}

	/**
	 * Returns the constraints queues.
	 */

	public final AbstractConstraintEventQueue[] getConstraintEventQueues() {
		return constEventQueues;
	}

	/**
	 * Set constraint Event Queues
	 * @param ceqs arrays of constraint event queues
	 */
	@SuppressWarnings({"SuspiciousSystemArraycopy"})
    public final void setConstraintEventQueues(final AbstractConstraintEventQueue[] ceqs) {
		System.arraycopy(ceqs, 0, constEventQueues, 0, constEventQueues.length);
	}


	
	/**
	 * Decrements the number of init constraint awake events.
	 */

	public final void decPendingInitConstAwakeEvent() {
		this.nbPendingInitConstAwakeEvent--;
	}


	/**
	 * Increments the number of init constraint awake events.
	 */

	public final void incPendingInitConstAwakeEvent() {
		this.nbPendingInitConstAwakeEvent++;
	}


	/**
	 * Returns the next constraint var queue from which an event should be propagated.
	 * @return Event queue
	 */

	public final EventQueue getNextActiveConstraintEventQueue() {
		for (int i = 0; i < NB_CONST_QUEUES; i++) {
			if (!this.constEventQueues[i].isEmpty()) return this.constEventQueues[i];
		}
		return null;
	}


	/**
	 * Returns the next queue from which an event should be propagated.
	 */

	@Override
	public final EventQueue getNextActiveEventQueue() {
		/*if (this.nbPendingInitConstAwakeEvent > 0) {
      return this.getNextActiveConstraintEventQueue();
    } else */
		for (int i = 0; i < NB_VAR_QUEUES; i++) {
			if (!this.varEventQueue[i].isEmpty()) return this.varEventQueue[i];
		}
		return this.getNextActiveConstraintEventQueue();
	}

	public final int getNbPendingEvents() {
		int nbEvts = 0;
		for (int i = 0; i < NB_VAR_QUEUES; i++) {
			nbEvts += varEventQueue[i].size();
		}
		for (int i = 0; i < NB_CONST_QUEUES; i++) {
			nbEvts += constEventQueues[i].size();
		}
		return nbEvts;
	}

	/**
	 * getter without side effect:
	 * returns the i-ht pending event (without popping any event from the queues)
	 * @param idx indice of the event
	 * @return a propagation event
	 */
	public final PropagationEvent getPendingEvent(int idx) {
		int varsSize = 0;
		for (int i = 0; i < NB_VAR_QUEUES; i++) {
			if (nbPendingInitConstAwakeEvent > 0) {
				idx += varEventQueue[i].size();
			}
			varsSize += varEventQueue[i].size();
			if (idx < varsSize) {
				return varEventQueue[i].get(idx);
			}
		}
		EventQueue q;
		int size = varsSize;
		int qidx = 0;
		do {
			idx = idx - size;
			q = constEventQueues[qidx++];
			size = q.size();
		} while (idx > size && qidx < NB_CONST_QUEUES);
		if (idx <= size) {
			return q.get(idx);               // return an event from one of the constraint event queues
		} else if (nbPendingInitConstAwakeEvent > 0) {
			// return an event from the variable event queues
			for (int i = 0; i < NB_VAR_QUEUES; i++) {
				varsSize += varEventQueue[i].size();
				if (idx < varsSize) {
					return varEventQueue[i].get(idx);
				}
			}
		}
		return null;              // return no event, as the index is greater than the total number of pending events
	}

	/**
	 * Removes all pending events (used when interrupting a propagation because
	 * a contradiction has been raised)
	 */
	public final void flushEvents() {
		for (int i = 0; i < NB_CONST_QUEUES; i++) {
			this.constEventQueues[i].flushEventQueue();
		}
		this.nbPendingInitConstAwakeEvent = 0;
		//    varEventQueue.flushEventQueue();
		for (int i = 0; i < NB_VAR_QUEUES; i++) {
			this.varEventQueue[i].flushEventQueue();
		}
	}

	public final boolean checkCleanState() {
		boolean ok = true;
		final int nbiv = solver.getNbIntVars();
		for (int i = 0; i < nbiv; i++) {
			final IntVarEvent evt = (IntVarEvent) solver.getIntVar(i).getEvent();
			if (!(evt.getReleased())) {
				LOGGER.log(Level.SEVERE, "var event non released {0}", evt);
				//        new Exception().printStackTrace();
				ok = false;
			}
		}
		final int nbsv = solver.getNbSetVars();
		for (int i = 0; i < nbsv; i++) {
			final SetVarEvent evt = (SetVarEvent) solver.getSetVar(i).getEvent();
			if (!(evt.getReleased())) {
				LOGGER.log(Level.SEVERE, "var event non released {0}", evt);
				//        new Exception().printStackTrace();
				ok = false;
			}
		}
		return ok;
	}


}
