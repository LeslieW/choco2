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
import static choco.kernel.solver.ContradictionException.Type.*;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.measure.FailMeasure;
import choco.kernel.solver.variables.Var;

import java.util.LinkedList;
import java.util.List;

/**
 * An abstract class for all implementations of propagation engines.
 */
public abstract class AbstractPropagationEngine implements PropagationEngine {

	public Solver solver;

	/**
	 * List of all listeners of events occuring in this engine.
	 */
	protected final List<PropagationEngineListener> propagationEngineListeners =
		new LinkedList<PropagationEngineListener>();

	/**
	 * Storing the last contradiction (reusable).
	 */
	protected final ContradictionException reuseException  = new ContradictionException(null, UNKNOWN);
	/**
	 * Retrieves the solver of the entity.
	 */

	protected final FailMeasure reuseFailMeasure;
	
	public final Solver getSolver(){
		return solver;
	}


	public void setSolver(Solver solver){
		this.solver = solver;
	}


	public AbstractPropagationEngine(Solver solver) {
		this.solver = solver;
		reuseFailMeasure = new FailMeasure(this);
	}

	/**
	 * Throws a contradiction with the specified cause.
	 *
	 * @throws choco.kernel.solver.ContradictionException
	 */

	public final void raiseContradiction(final Object cause, final ContradictionException.Type type) throws ContradictionException {
        reuseException.set(cause,type);
		for(PropagationEngineListener listener : propagationEngineListeners) {
			listener.contradictionOccured(reuseException);
		}
		throw(reuseException);
	}


    /**
     * Throws a contradiction with the specified cause.
     *
     * @throws choco.kernel.solver.ContradictionException
     */

    public final void raiseContradiction(final int cidx, final Var variable) throws ContradictionException {
        if (cidx == -1){
            reuseException.set(variable, VARIABLE);
        }
        else if(cidx>=0){
            reuseException.set(variable.getConstraintVector().get(cidx), CONSTRAINT,
                    variable.getConstraintVector().get(cidx));
        }else{
            reuseException.set(variable, VARIABLE,
                    variable.getConstraintVector().get(VarEvent.domOverWDegInitialIdx(cidx)));
        }
        for(PropagationEngineListener listener : propagationEngineListeners) {
            listener.contradictionOccured(reuseException);
        }
        throw(reuseException);
    }

	public final void raiseContradiction(final Object cause, final ContradictionException.Type type, final int move) throws ContradictionException {
		reuseException.set(cause,type, move);
		for(PropagationEngineListener listener : propagationEngineListeners) {
			listener.contradictionOccured(reuseException);
		}
		throw(reuseException);
	}
	
	public final void addPropagationEngineListener(PropagationEngineListener listener) {
		propagationEngineListeners.add(listener);
	}

    /**
     * Removes a old listener from the propagation engine
     *
     * @param listener removal listener
     */
    @Override
    public final void removePropagationEngineListener(PropagationEngineListener listener) {
        propagationEngineListeners.remove(listener);
    }

    @Override
    public boolean containsPropagationListener(PropagationEngineListener listener) {
        return propagationEngineListeners.contains(listener);
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
