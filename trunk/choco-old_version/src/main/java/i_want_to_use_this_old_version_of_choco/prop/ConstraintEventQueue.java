// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.prop;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.util.BipartiteSet;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements an {@link i_want_to_use_this_old_version_of_choco.prop.EventQueue} for managing the constraint awake events.
 */
public class ConstraintEventQueue implements EventQueue {

  /**
   * The propagation engine using this queue.
   */

  private PropagationEngine engine;


  /**
   * A private stucture to store all the constraint. The left part of the bipartite
   * set contains the events to propagate.
   */

  private BipartiteSet partition;

  /**
   * Reference to the root Logger, an object for logging trace statements related to propagation events (using the java.util.logging package)
   */

  private static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop");

  /**
   * Constructs a new queue for the specified engine.
   */

  public ConstraintEventQueue(PropagationEngine engine) {
    this.engine = engine;
    this.partition = new BipartiteSet();
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
    PropagationEvent event = (PropagationEvent) this.partition.moveLastLeft();
    if (event == null) {
      if (logger.isLoggable(Level.SEVERE))
        logger.severe("Error: There is no more events in the queue.");
    } else {
      if (!((ConstraintEvent) event).isInitialized()) {
        ((ChocEngine) engine).decPendingInitConstAwakeEvent();
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
        ((ChocEngine) engine).decPendingInitConstAwakeEvent();
      }
      this.partition.moveRight(event);
    }
  }


  /**
   * Propagates one var in the queue.
   *
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */

  public void propagateSomeEvents() throws ContradictionException {
    this.popEvent().propagateEvent();
  }

  /**
   * Propagates one var in the queue.
   *
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */

  public void propagateOneEvent() throws ContradictionException {
    this.popEvent().propagateEvent();
  }

  public int size() {
    return partition.getNbLeft();
  }

  public PropagationEvent get(int idx) {
    for (Iterator it = partition.leftIterator(); it.hasNext();) {
      PropagationEvent event = (PropagationEvent) it.next();
      if (idx == 0) {
        return event;
      } else {
        idx--;
      }
    }
    return null;
  }
}
