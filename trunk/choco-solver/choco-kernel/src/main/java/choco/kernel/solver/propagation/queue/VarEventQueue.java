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

package choco.kernel.solver.propagation.queue;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.event.PropagationEvent;

public interface VarEventQueue extends EventQueue {

    /**
     * Clear datastructures for safe reuses
     */
    public void clear();

  /**
   * Checks if the queue is empty.
   */
  public boolean isEmpty();


  /**
   * Propagates some events: in fact all the events of the queue, since there
   * are the most important events.
   *
   * @throws choco.kernel.solver.ContradictionException
   */
  public void propagateSomeEvents() throws ContradictionException;

  /**
   * Propagates one single event from the queue (usefull for tracing)
   *
   * @throws choco.kernel.solver.ContradictionException
   */
  public void propagateOneEvent() throws ContradictionException;

  /**
   * Pops an event to propagate.
   */
  public PropagationEvent popEvent();


  /**
   * Adds an event to the queue.
   */

  public boolean pushEvent(PropagationEvent event);

  /**
   * Removes all the events (including the popping one).
   */

  public void flushEventQueue();

  /**
   * Removes an event. This method should not be useful for variable events.
   */

  public void remove(PropagationEvent event);

  public int size();

  public PropagationEvent get(int idx);

}
