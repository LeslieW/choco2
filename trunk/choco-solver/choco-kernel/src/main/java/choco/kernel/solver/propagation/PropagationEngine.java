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

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.event.ConstraintEvent;
import choco.kernel.solver.propagation.listener.PropagationEngineListener;
import choco.kernel.solver.propagation.queue.AbstractConstraintEventQueue;
import choco.kernel.solver.propagation.queue.EventQueue;
import choco.kernel.solver.propagation.queue.VarEventQueue;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.set.SetVar;

import java.util.logging.Logger;

/**
 * An interface for all implementations of propagation engines.
 */
public interface PropagationEngine {

	public final static Logger LOGGER = ChocoLogging.getEngineLogger();

	/**
	 * Raising a contradiction with a cause.
	 */
	public void raiseContradiction(Object cause) throws ContradictionException;

    /**
	 * Raising a contradiction with a cause and a movement
	 */
	public void raiseContradiction(Object cause, int move) throws ContradictionException;

    /**
	 * Raising a contradiction with a variable.
	 */
    @Deprecated
    public void raiseContradiction(int cidx, Var variable, final SConstraint cause) throws ContradictionException;

	/**
	 * Retrieving the cause of the last contradiction.
	 */

	public Object getContradictionCause();


	/**
	 * Returns the next active var queue to propagate some events.
	 * If it returns null, the propagation is finished.
	 */

	public EventQueue getNextActiveEventQueue();

	/**
	 * Removes all pending events (used when interrupting a propagation because
	 * a contradiction has been raised)
	 */
	public void flushEvents();

	/**
	 * checking that the propagation engine remains in a proper state
	 */
	public boolean checkCleanState();

	void postUpdateInf(IntDomainVar v, final SConstraint constraint, final boolean forceAwake);

	void postUpdateSup(IntDomainVar v, final SConstraint constraint, final boolean forceAwake);

	void postInstInt(IntDomainVar v, final SConstraint constraint, final boolean forceAwake);

	void postRemoveVal(IntDomainVar v, int x, final SConstraint constraint, final boolean forceAwake);

	void postUpdateInf(RealVar v, final SConstraint constraint, final boolean forceAwake);

	void postUpdateSup(RealVar v, final SConstraint constraint, final boolean forceAwake);

	void postRemEnv(SetVar v, final SConstraint constraint, final boolean forceAwake);

	void postAddKer(SetVar v, final SConstraint constraint, final boolean forceAwake);

	void postInstSet(SetVar v, final SConstraint constraint, final boolean forceAwake);

	/**
	 * Generic method to post events. The caller is reponsible of basic event
	 * type field: it should be meaningful for the the associate kind of event.
	 * @param v The modified variable.
     * @param basicEvt A integer specifying mdofication kind for the attached
     * @param constraint
     * @param forceAwake
     */
	void postEvent(Var v, int basicEvt, final SConstraint constraint, final boolean forceAwake);

	boolean postConstAwake(Propagator constraint, boolean init);

	void registerEvent(ConstraintEvent event);

	VarEventQueue[] getVarEventQueues();


	void setVarEventQueues(int eventQueueType);

	void setVarEventQueues(VarEventQueue[] veq);

	AbstractConstraintEventQueue[] getConstraintEventQueues();

	void setConstraintEventQueues(AbstractConstraintEventQueue[] ceq);

	void decPendingInitConstAwakeEvent();

	/**
	 * Adds a new listener to some events occuring in the propagation engine.
	 * @param listener a new listener
	 */
	void addPropagationEngineListener(PropagationEngineListener listener);

    /**
     * Removes a old listener from the propagation engine
     * @param listener removal listener
     */
    void removePropagationEngineListener(PropagationEngineListener listener);

    boolean containsPropagationListener(PropagationEngineListener listener);

	EventQueue getQueue(ConstraintEvent csvt);

}
