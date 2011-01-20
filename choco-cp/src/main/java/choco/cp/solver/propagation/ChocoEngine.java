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
import choco.kernel.solver.Configuration;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.propagation.event.ConstraintEvent;
import choco.kernel.solver.propagation.event.PropagationEvent;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.propagation.queue.EventQueue;
import choco.kernel.solver.variables.Var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Implementation of an {@link AbstractPropagationEngine} for Choco.
 */
public class ChocoEngine extends AbstractPropagationEngine {
    /**
     * The different queues for the constraint awake events.
     */

    protected ConstraintEventQueue[] constEventQueues;
    private int c_active;

    /**
     * Number of pending init constraint awake events.
     */
    protected int nbPendingInitConstAwakeEvent;

    /**
     * The queue with all the variable events.
     */

    protected VariableEventQueue[] varEventQueue;
    private int v_active;

    private ArrayList<PropagationEvent> freeze;
    private int nbFrozenVE;

    private final int[] v_order;
    private final int[] v_indice;
    private final int[] c_order;
    private final int[] c_indice;

    /**
     * Constructs a new engine by initializing the var queues.
     *
     * @param solver Solver master
     */
    public ChocoEngine(Solver solver) {
        super(solver);
        v_order = toInt(solver.getConfiguration().readString(Configuration.VEQ_ORDER));
        v_indice = toIndice(v_order);
        c_order = toInt(solver.getConfiguration().readString(Configuration.CEQ_ORDER));
        c_indice = toIndice(c_order);

        assert Arrays.equals(c_indice, v_indice);

        constEventQueues = new ConstraintEventQueue[ConstraintEvent.NB_PRIORITY];
        for (int i = 1; i < ConstraintEvent.NB_PRIORITY; i++) {
            constEventQueues[i] = new ConstraintEventQueue(this);
        }
        varEventQueue = new VariableEventQueue[ConstraintEvent.NB_PRIORITY];
        for (int i = 1; i < ConstraintEvent.NB_PRIORITY; i++) {
            varEventQueue[i] = new VariableEventQueue();
        }
        nbPendingInitConstAwakeEvent = 0;
    }

    private static int[] toInt(String value) {
        int[] values = new int[value.length() + 1];
        char[] value_c = value.toCharArray();
        for (int i = 0; i < value.length(); i++) {
            values[i + 1] = value_c[i] - 48;
        }
        return values;
    }

    private static int[] toIndice(int[] order) {
        int[] indice = new int[1 << order.length];
        indice[0] = -1;
        for (int i = 0; i < order.length; i++) {
            int k = order[i];
            for (int j = (1 << k); j < (1 << k + 1); j++) {
                indice[j] = k;
            }
        }
        return indice;
    }

    //****************************************************************************************************************//
    //****************************************************************************************************************//
    //****************************************************************************************************************//

    /**
     * Clear datastructures for safe reuses
     */
    public void clear() {
        int idx;
        while (v_active > 0) {
            idx = v_indice[v_active];
            this.varEventQueue[idx].clear();
            v_active -= 1 << idx;
        }

        while (c_active > 0) {
            idx = c_indice[c_active];
            this.constEventQueues[idx].clear();
            c_active -= 1 << idx;
        }
        nbPendingInitConstAwakeEvent = 0;
    }

    /**
     * Private method for completing the bound var posting.
     *
     * @param basicEvt   The basic event posted.
     * @param constraint
     * @param forceAwake
     */
    // idee: - si on est "frozen", devenir en plus "redondant" (ie: double).
    //       - par ailleurs, noter le changement (garder la vieille valeur de la borne ou
    //       - devenir enqueued
    public void postEvent(final Var v, final int basicEvt, final SConstraint constraint, final boolean forceAwake) {
        VarEvent<? extends Var> event = v.getEvent();
        boolean alreadyEnqueued = event.isEnqueued();
        event.recordEventTypeAndCause(basicEvt, constraint, forceAwake);
        if (!alreadyEnqueued) {
            int p = v_order[event.getPriority()];
            varEventQueue[p].pushEvent(event);
            v_active |= (1 << p);

        }
    }

    /**
     * Posts a constraint awake var.
     *
     * @param constraint The constraint that must be awaken.
     * @param init       Specifies if the constraint must be initialized
     *                   (awake instead of propagate).
     */

    public boolean postConstAwake(final Propagator constraint, final boolean init) {
        final ConstraintEvent event = (ConstraintEvent) constraint.getEvent();
        if (constEventQueues[c_order[event.getPriority()]].pushEvent(event)) {
            int p = c_order[event.getPriority()];
            c_active |= (1 << p);
            event.setInitialized(!init);
            if (init) {
                this.incPendingInitConstAwakeEvent();
            }
            return true;
        } else
            return false;
    }


    /**
     * Registers an event in the queue. It should be called before using the queue to add
     * the var in the available events of the queue.
     *
     * @param event the event to register
     */

    public void registerEvent(final ConstraintEvent event) {
        constEventQueues[c_order[event.getPriority()]].add(event);
    }


    @Override
    public void propagateEvents() throws ContradictionException {
        do {
            // first empty variable events
            int idx;
            while (v_active > 0) {
                idx = v_indice[v_active];
                this.varEventQueue[idx].propagateAllEvents();
                if (this.varEventQueue[idx].isEmpty()) {
                    v_active -= 1 << idx;
                }
            }

            // then propagate one constraint event
            if (c_active > 0) {
                idx = c_indice[c_active];
                if (this.constEventQueues[idx].size() == 1) {
                    c_active -= 1 << idx;
                }
                this.constEventQueues[idx].propagateOneEvent();
            }
        } while (v_active > 0 || c_active > 0);
//        assert (getNbPendingEvents() == 0);
        assert checkCleanState();
    }

    @Override
    public void removeEvent(PropagationEvent event) {
        if (event instanceof ConstraintEvent) {
            int idx = c_order[event.getPriority()];
            if (constEventQueues[idx].remove(event)) {
                if (this.constEventQueues[idx].isEmpty()) {
                    c_active -= 1 << idx;
                }
            }
        }
    }

    /**
     * Decrements the number of init constraint awake events.
     */

    public void decPendingInitConstAwakeEvent() {
        this.nbPendingInitConstAwakeEvent--;
    }


    /**
     * Increments the number of init constraint awake events.
     */

    public void incPendingInitConstAwakeEvent() {
        this.nbPendingInitConstAwakeEvent++;
    }


    public int getNbPendingEvents() {
        int nbEvts = 0;
        for (int i = 1; i < ConstraintEvent.NB_PRIORITY; i++) {
            nbEvts += varEventQueue[i].size();
        }
        for (int i = 1; i < ConstraintEvent.NB_PRIORITY; i++) {
            nbEvts += constEventQueues[i].size();
        }
        return nbEvts;
    }

    /**
     * getter without side effect:
     * returns the i-ht pending event (without popping any event from the queues)
     *
     * @param idx indice of the event
     * @return a propagation event
     */
    public PropagationEvent getPendingEvent(int idx) {
        int varsSize = 0;
        for (int i = 1; i < varEventQueue.length; i++) {
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
        int qidx = 1;
        do {
            idx -= size;
            q = constEventQueues[qidx++];
            size = q.size();
        } while (idx >= size && qidx < constEventQueues.length);
        if (idx <= size) {
            return q.get(idx);               // return an event from one of the constraint event queues
        } else if (nbPendingInitConstAwakeEvent > 0) {
            // return an event from the variable event queues
            for (int i = 1; i < varEventQueue.length; i++) {
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
    public void flushEvents() {
        for (int i = 1; i < varEventQueue.length; i++) {
            this.varEventQueue[i].flushEventQueue();
        }
        v_active = 0;

        for (int i = 1; i < constEventQueues.length; i++) {
            this.constEventQueues[i].flushEventQueue();
        }
        c_active = 0;

        this.nbPendingInitConstAwakeEvent = 0;
    }

    public boolean checkCleanState() {
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

    @Override
    public void freeze() {
        if (freeze == null) {
            freeze = new ArrayList<PropagationEvent>();
        }
        int idx;
        while (v_active > 0) {
            idx = v_indice[v_active];
            while (!this.varEventQueue[idx].isEmpty()) {
                freeze.add(this.varEventQueue[idx].popEvent());
            }
            this.varEventQueue[idx].clear();
            v_active -= 1 << idx;
        }
        nbFrozenVE = freeze.size();
        while (c_active > 0) {
            idx = c_indice[c_active];
            while (!this.constEventQueues[idx].isEmpty()) {
                freeze.add(this.constEventQueues[idx].popEvent());
            }
            c_active -= 1 << idx;
        }
    }

    @Override
    public void unfreeze() {
        for (int i = freeze.size() - 1; i >= nbFrozenVE; i--) {
            PropagationEvent event = this.freeze.remove(i);
            int p = c_order[event.getPriority()];
            constEventQueues[p].pushEvent(event);
            c_active |= (1 << p);
        }
        for (int i = nbFrozenVE - 1; i >= 0; i--) {
            PropagationEvent event = this.freeze.remove(i);
            int p = v_order[event.getPriority()];
            varEventQueue[p].pushEvent(event);
            v_active |= (1 << p);
        }
        nbFrozenVE = 0;
    }
}
