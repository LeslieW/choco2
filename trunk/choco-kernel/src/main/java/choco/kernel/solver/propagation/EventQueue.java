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

/**
 * An interface for all the implementations of var queues (Constraint awake, on
 * Variable events for instance).
 */
public interface EventQueue {

  /**
   * Checks if the queue is empty.
   */

  public boolean isEmpty();


  /**
   * Pops the next var to propagate.
   */

  public PropagationEvent popEvent();


  /**
   * Adds an event to the queue.
   *
   * @param event
   */

  public boolean pushEvent(PropagationEvent event);


  /**
   * Removes all the events and clears all the events if needed.
   */

  public void flushEventQueue();


  /**
   * Removes an event.
   */

  public void remove(PropagationEvent event);


  /**
   * Propagate some events (one or several depending on the queue).
   *
   * @throws choco.kernel.solver.ContradictionException
   */

  public void propagateSomeEvents() throws ContradictionException;

  /**
   * Propagate one single event from the queue).
   *
   * @throws choco.kernel.solver.ContradictionException
   */

  public void propagateOneEvent() throws ContradictionException;

  /**
   * returns the number of pending events in the queue
   *
   * @return the number of pending events in the queue
   */
  public int size();

  /**
   * returns the i-th pending event in the queue
   *
   * @param idx the index of the event
   * @return null if the index is inproper (idx<0 or idx>=size())
   */
  public PropagationEvent get(int idx);
}
