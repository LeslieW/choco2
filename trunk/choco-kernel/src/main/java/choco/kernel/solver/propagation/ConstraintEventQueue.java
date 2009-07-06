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

import choco.kernel.common.util.objects.BipartiteSet;
import choco.kernel.solver.ContradictionException;

import java.util.Iterator;

/**
 * Implements an {@link choco.kernel.solver.propagation.EventQueue} for managing the constraint awake events.
 */
public class ConstraintEventQueue implements EventQueue {

  /**
   * The propagation engine using this queue.
   */

  private PropagationEngine engine;


  /**
   * A private structure to store all the constraint. The left part of the bipartite
   * set contains the events to propagate.
   */

  private BipartiteSet<PropagationEvent> partition;


  /**
   * Constructs a new queue for the specified engine.
   */

  public ConstraintEventQueue(PropagationEngine engine) {
    this.engine = engine;
    this.partition = new BipartiteSet<PropagationEvent>();
  }


  /**
   * Checks if the queue is empty.
   */

  public boolean isEmpty() {
    return this.partition.getNbLeft() == 0;
  }


  /**
   * Pops the next var to propagate.
   */

  public PropagationEvent popEvent() {
    PropagationEvent event = this.partition.moveLastLeft();
    if (event == null) {
    	LOGGER.severe("Error: There is no more events in the queue.");
    } else {
      if (!((ConstraintEvent) event).isInitialized()) {
        engine.decPendingInitConstAwakeEvent();
      }
    }
    return event;
  }


  /**
   * Adds a new var in the queue.
   *
   * @return True if the var had to be added.
   */

  public boolean pushEvent(PropagationEvent event) {
    if (!this.partition.isLeft(event)) {
      this.partition.moveLeft(event);
      return true;
    }
    return false;
  }


  /**
   * Removes all the events from the queue.
   */

  public void flushEventQueue() {
    this.partition.moveAllRight();
  }


  /**
   * Adds a new constraint in the right part of the set (will not be propagated).
   * It should be done just after creating the constraint.
   */

  public void add(PropagationEvent event) {
    if (this.partition.isIn(event)) {
      /*if (logger.isLoggable(Level.SEVERE))
        logger.severe("Event added is already attached to engine !");*/
      this.partition.moveRight(event);
    } else {
      this.partition.addRight(event);
    }
  }


  /**
   * Removes the var from the left part.
   */

  public void remove(PropagationEvent event) {
    if (this.partition.isLeft(event)) {
      if (!((ConstraintEvent) event).isInitialized()) {
        engine.decPendingInitConstAwakeEvent();
      }
      this.partition.moveRight(event);
    }
  }


  /**
   * Propagates one var in the queue.
   *
   * @throws choco.kernel.solver.ContradictionException
   */

  public void propagateSomeEvents() throws ContradictionException {
      PropagationEvent evt =this.popEvent();
        evt.propagateEvent();
  }

  /**
   * Propagates one var in the queue.
   *
   * @throws choco.kernel.solver.ContradictionException
   */

  public void propagateOneEvent() throws ContradictionException {
    this.popEvent().propagateEvent();
  }

  public int size() {
    return partition.getNbLeft();
  }

  public PropagationEvent get(int idx) {
    for (Iterator<PropagationEvent> it = partition.leftIterator(); it.hasNext();) {
      PropagationEvent event = it.next();
      if (idx == 0) {
        return event;
      } else {
        idx--;
      }
    }
    return null;
  }
}
