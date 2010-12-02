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

package choco.kernel.solver.propagation;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.listener.PropagationEngineListener;
import choco.kernel.solver.propagation.queue.EventQueue;
import choco.kernel.solver.search.measure.FailMeasure;
import choco.kernel.solver.variables.Var;

/**
 * An abstract class for all implementations of propagation engines.
 */
public abstract class AbstractPropagationEngine implements PropagationEngine {

    public final Solver solver;

    private final FailMeasure failMeasure;


    /**
     * List of all listeners of events occuring in this engine.
     */
    protected PropagationEngineListener[] propagationEngineListeners = new PropagationEngineListener[8];
    protected int pelIdx = 0;

    /**
     * Storing the last contradiction (reusable).
     */
    protected final ContradictionException reuseException;


    public AbstractPropagationEngine(Solver solver) {
        this.solver = solver;
        reuseException = ContradictionException.build();
        failMeasure = new FailMeasure(this);
    }

    public final Solver getSolver() {
        return solver;
    }

    public final FailMeasure getFailMeasure() {
        return failMeasure;
    }

    /**
     * Throws a contradiction with the specified cause.
     *
     * @throws choco.kernel.solver.ContradictionException
     *
     */

    public final void raiseContradiction(final Object cause) throws ContradictionException {
        reuseException.set(cause);
        for (int i = 0; i < pelIdx; i++) {
            propagationEngineListeners[i].contradictionOccured(reuseException);
        }
        throw (reuseException);
    }

    public final void raiseContradiction(final Object cause, final int move) throws ContradictionException {
        reuseException.set(cause, move);
        for (int i = 0; i < pelIdx; i++) {
            propagationEngineListeners[i].contradictionOccured(reuseException);
        }
        throw (reuseException);
    }

    @Deprecated
    public final void raiseContradiction(int cidx, Var variable, final SConstraint cause) throws ContradictionException {
        if (cidx >= 0) {
            reuseException.set(variable.getConstraintVector().get(cidx)
            );
        } else {
            reuseException.set(variable);
        }
        for (int i = 0; i < pelIdx; i++) {
            propagationEngineListeners[i].contradictionOccured(reuseException);
        }
        throw (reuseException);
    }

    public final void addPropagationEngineListener(PropagationEngineListener listener) {
        if (pelIdx == propagationEngineListeners.length) {
            PropagationEngineListener[] tmp = propagationEngineListeners;
            propagationEngineListeners = new PropagationEngineListener[tmp.length * 3 / 2 + 1];
            System.arraycopy(tmp, 0, propagationEngineListeners, 0, pelIdx);
        }
        propagationEngineListeners[pelIdx++] = listener;
    }

    /**
     * Removes a old listener from the propagation engine
     *
     * @param listener removal listener
     */
    @Override
    public final void removePropagationEngineListener(PropagationEngineListener listener) {
        int i = 0;
        while (i < pelIdx && propagationEngineListeners[i] != listener) {
            i++;
        }
        if (i < pelIdx) {
            System.arraycopy(propagationEngineListeners, i + 1, propagationEngineListeners, i, pelIdx - i);
            pelIdx--;
        }
    }

    @Override
    public boolean containsPropagationListener(PropagationEngineListener listener) {
        int i = 0;
        while (i < pelIdx && propagationEngineListeners[i] != listener) {
            i++;
        }
        return i < pelIdx;
    }

    /**
     * Retrieving the cause of the last contradiction.
     */
    public final Object getContradictionCause() {
        return reuseException.getContradictionCause();
    }

    /**
     * Gets the next queue from which a var will be propagated.
     */

    public EventQueue getNextActiveEventQueue() {
        return null;
	}
}
