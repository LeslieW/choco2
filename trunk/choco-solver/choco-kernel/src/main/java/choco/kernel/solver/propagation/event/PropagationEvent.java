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

package choco.kernel.solver.propagation.event;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.ContradictionException;

import java.util.logging.Logger;

/**
 * An interface for all implementations of events.
 */
public interface PropagationEvent {


	public final static Logger LOGGER = ChocoLogging.getEngineLogger();

	/**
	 * Value of the state in the queue: -1 means the var is being propagated.
	 * (see VarEvent.qState)
	 */
	public final static int POPPING = -1;

	/**
	 * Returns the object, whose modification is described by the event
	 */

	public Object getModifiedObject();

	/**
	 * Propagates the var through calls to the propagation engine.
	 *
	 * @return true if the event has been fully propagated (and can thus be discarded), false otherwise
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public boolean propagateEvent() throws ContradictionException;

	/**
	 * Tests whether a propagation var is active in the propagation network.
	 */

	public boolean isActive(int idx);


	/**
	 * Clears the var if it not useful anymore.
	 */

	public void clear();
}