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
