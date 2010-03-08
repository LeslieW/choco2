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

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBool;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.event.ConstraintEvent;
import choco.kernel.solver.propagation.event.PropagationEvent;
import choco.kernel.solver.propagation.queue.EventQueue;

import java.util.EventListener;

/**
 * An interface for all implementations of listeners.
 */
public abstract class Propagator implements EventListener {

    protected PropagationEngine propagationEngine;

    	/**
	 * a field for storing whether the constraint is active or not
	 */
	protected IStateBool active;


    /**
     * The constraint <i>awake</i> var attached to the constraint.
     */

    protected final ConstraintEvent constAwakeEvent;

    /**
	 * The priority of the constraint.
	 */

	protected final int priority;

    protected Propagator() {
        this(0);
    }

    protected Propagator(int priority) {
        this.priority = priority;
        this.constAwakeEvent = new ConstraintEvent(this, false, priority);
    }

    /**
     * This function connects a constraint with its variables in several ways.
     * Note that it may only be called once the constraint
     * has been fully created and is being posted to a model.
     * Note that it should be called only once per constraint.
     * This can be a dynamic addition (undone upon backtracking) or not
     *
     * @param dynamicAddition
     */
    public abstract void addListener(boolean dynamicAddition);

    /**
     * <i>Utility:</i>
     * Testing if all the variables involved in the constraint are instantiated.
     *
     * @return whether all the variables have been completely instantiated
     */

    public abstract boolean isCompletelyInstantiated();


    /**
     * Forces a propagation of the constraint.
     *
     * @param isInitialPropagation indicates if it is the initial propagation or not
     */

    public final void constAwake(boolean isInitialPropagation) {
        propagationEngine.postConstAwake(this, isInitialPropagation);
    }


    /**
     * <i>Propagation:</i>
     * Propagating the constraint for the very first time until local
     * consistency is reached.
     *
     * @throws ContradictionException contradiction exception
     */

    public void awake() throws ContradictionException {
        this.propagate();
    }


    /**
     * <i>Propagation:</i>
     * Propagating the constraint until local consistency is reached.
     *
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */

    public abstract void propagate() throws ContradictionException;


    /**
     * Activate a constraint.
     * @param environment current environment
     */
    public final void activate(IEnvironment environment) {
        this.active = environment.makeBool(false);
    }

    /**
	 * Un-freezing a constraint (this is useful for mimicking dynamic
	 * constraint posts...).
	 */

	public final void setActive() {
		if (!(isActive())) {
			setActiveSilently();
			constAwake(true);
		}
	}

	public final void setActiveSilently() {
		active.set(true);
	}


	/**
	 * Freezing a constraint (this is useful for backtracking when mimicking
	 * dynamic constraint posts...).
	 */

	public final void setPassive() {
		if (active != null) {
			active.set(false);
			ConstraintEvent evt = constAwakeEvent;
			EventQueue q = propagationEngine.getQueue(evt);
			q.remove(evt);
		}
	}


	/**
	 * Checks if the constraint is active (e.g. plays a role in the propagation phase).
	 *
	 * @return true if the constraint is indeed currently active
	 */

	public final boolean isActive() {
		return active.get();
	}

    /**
	 * records that a constraint is now entailed (therefore it is now useless to propagate it again)
	 */
	public final void setEntailed() {
		setPassive();
	}

    /**
     * <i>Propagation:</i>
     * Accessing the priority level of the queue handling the propagation
     * of the constraint. Results range from 1 (most reactive, for listeners
     * with fast propagation algorithms) to 4 (most delayed, for listeners
     * with lengthy propagation algorithms).
     *
     * @return the priority level of the queue handling the propagation of the constraint
     */
	public final int getPriority() {
		return priority;
	}
    /**
     * Returns the constraint awake var associated with this constraint.
     *
     * @return the constraint awake var associated with this constraint.
     */

    public final PropagationEvent getEvent() {
        return constAwakeEvent;
    }

    /**
     * Checks whether the constraint is definitely satisfied, no matter what further restrictions
     * occur to the domain of its variables.
     *
     * @return wether the constraint is entailed
     */
    public abstract Boolean isEntailed();

    /**
     * tests if the constraint is consistent with respect to the current state of domains
     *
     * @return wether the constraint is consistent
     */
    public abstract boolean isConsistent();


    public int getFilteredEventMask(int idx) {
        return 0x0FFFF;
    }


    /**
     * Define the propagation engine within the constraint.
     * Mandatory to throw {@link ContradictionException}.
     *
     * @param propEng the current propagation engine
     */
    public void setPropagationEngine(PropagationEngine propEng) {
        this.propagationEngine = propEng;
    }

    /**
	 * raise a contradiction during propagation when the constraint can definitely not be satisfied given the current domains
	 * @throws ContradictionException contradiction exception
	 */
	public void fail() throws ContradictionException {
		propagationEngine.raiseContradiction(this);
	}

}