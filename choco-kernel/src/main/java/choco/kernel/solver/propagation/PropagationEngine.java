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

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.event.ConstraintEvent;
import choco.kernel.solver.propagation.event.PropagationEvent;
import choco.kernel.solver.propagation.listener.PropagationEngineListener;
import choco.kernel.solver.search.measure.FailMeasure;
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

	FailMeasure getFailMeasure();

    //****************************************************************************************************************//
    //*************************************** CONTRADICTION **********************************************************//
    //****************************************************************************************************************//

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


    //****************************************************************************************************************//
    //************************************ EVENTS ********************************************************************//
    //****************************************************************************************************************//

	/**
	 * Removes all pending events (used when interrupting a propagation because
	 * a contradiction has been raised)
	 */
	public void flushEvents();

	/**
	 * checking that the propagation engine remains in a proper state
	 */
	public boolean checkCleanState();
	/**
	 * Generic method to post events. The caller is reponsible of basic event
	 * type field: it should be meaningful for the the associate kind of event.
	 * @param v The modified variable.
     * @param basicEvt A integer specifying mdofication kind for the attached
     * @param constraint
     * @param forceAwake
     */
	void postEvent(Var v, int basicEvt, final SConstraint constraint, final boolean forceAwake);

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
     *
     * @param constraint
     * @param init
     * @return
     */
	boolean postConstAwake(Propagator constraint, boolean init);

    /**
     *
     * @param event
     */
	void registerEvent(ConstraintEvent event);

    /**
     * Propagate one by one events registered
     *
     * @throws choco.kernel.solver.ContradictionException
     */
    void propagateEvents() throws ContradictionException;

    void removeEvent(PropagationEvent event);

    /**
     * Decrements the number of init constraint awake events.
     */

    void decPendingInitConstAwakeEvent();


    /**
     * Increments the number of init constraint awake events.
     */

    void incPendingInitConstAwakeEvent();

    void freeze();

    void unfreeze();

    //****************************************************************************************************************//
    //********************************** LISTENERS *******************************************************************//
    //****************************************************************************************************************//

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

    /**
     * Check wether <code>this</code> contains <code>listener</code> in its list of listeners
     * @param listener
     * @return
     */
    boolean containsPropagationListener(PropagationEngineListener listener);

}
