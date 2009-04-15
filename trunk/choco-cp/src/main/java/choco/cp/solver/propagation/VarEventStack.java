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
package choco.cp.solver.propagation;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.EventQueue;
import choco.kernel.solver.propagation.PropagationEvent;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: GROCHART Date: 3 juin 2008 Time: 19:54:13 To
 * change this template use File | Settings | File Templates.
 */
public class VarEventStack implements EventQueue {

	/**
	 * A priority queue with all the var. Default number of priority levels: 5
	 * from 0 (most important) to 4.
	 */
	// protected PriorityQueue queue = new PriorityQueue();
	protected Stack<PropagationEvent> queue = new Stack<PropagationEvent>();

	/**
	 * The last popped var (may be useful for flushing popping events).
	 */
	protected PropagationEvent lastPopped = null;


	/**
	 * Checks if the queue is empty.
	 */
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	/**
	 * Propagates some events: in fact all the events of the queue, since there
	 * are the most important events.
	 * 
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public void propagateSomeEvents() throws ContradictionException {
		while (queue.size() != 0) {
			PropagationEvent evt = popEvent();
			evt.propagateEvent();
			// in case the propagation of the event is not complete
			// the event will be pushed right back onto the queue
			/*
			 * if (!evt.propagateEvent()) { pushEvent(evt); }
			 */
		}
	}

	/**
	 * Propagates one single event from the queue (usefull for tracing)
	 * 
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public void propagateOneEvent() throws ContradictionException {
		if (queue.size() != 0) {
			//PropagationEvent evt = 
				popEvent();
			// in case the propagation of the event is not complete
			// the event will be pushed right back onto the queue
			/*
			 * if (!evt.propagateEvent()) { pushEvent(evt); }
			 */
		}
	}

	/**
	 * Pops an event to propagate.
	 */
	public PropagationEvent popEvent() {
		PropagationEvent event = queue.pop();
		lastPopped = event;
		LOGGER.log(Level.FINEST, "just popped {0}", event);
		return event;
	}

	/**
	 * Adds an event to the queue.
	 */

	public boolean pushEvent(PropagationEvent event) {
		queue.push(event);
		return true;
	}

	/**
	 * Updates the priority level of an event (after adding a basic var).
	 */

	/*
	 * public void updatePriority(PropagationEvent event) {
	 * queue.updatePriority(event); }
	 */

	/**
	 * Removes all the events (including the popping one).
	 */

	public void flushEventQueue() {
		if (null != lastPopped) {
			lastPopped.clear();
		}

		for (PropagationEvent event : queue) {
			event.clear();
		}

		queue.clear();
	}

	/**
	 * Removes an event. This method should not be useful for variable events.
	 */

	public void remove(PropagationEvent event) {
		queue.remove(event);
	}

	public int size() {
		return queue.size();
	}

	public PropagationEvent get(int idx) {
		for (PropagationEvent event : queue) {
			if (idx == 0) {
				return event;
			} else {
				idx--;
			}
		}
		return null;
	}

}
