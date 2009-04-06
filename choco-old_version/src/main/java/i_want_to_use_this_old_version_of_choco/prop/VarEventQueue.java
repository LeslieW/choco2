// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.prop;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.util.PriorityQueue;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VarEventQueue implements EventQueue {

  /**
   * A priority queue with all the var. Default number of priority levels: 5
   * from 0 (most important) to 4.
   */
  protected PriorityQueue queue = new PriorityQueue();

  /**
   * The last popped var (may be useful for flushing popping events).
   */
  protected PropagationEvent lastPopped = null;

  /**
   * Reference to object for logging trace statements related to propagation events (using the java.util.logging package)
   */

  private static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop");

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
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */
  public void propagateSomeEvents() throws ContradictionException {
    while (queue.size() != 0) {
      PropagationEvent evt = popEvent();
      evt.propagateEvent();
      // in case the propagation of the event is not complete
      // the event will be pushed right back onto the queue
      /* if (!evt.propagateEvent()) {
        pushEvent(evt);
      } */
    }
  }

  /**
   * Propagates one single event from the queue (usefull for tracing)
   *
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */
  public void propagateOneEvent() throws ContradictionException {
    if (queue.size() != 0) {
      PropagationEvent evt = popEvent();
      // in case the propagation of the event is not complete
      // the event will be pushed right back onto the queue
      /* if (!evt.propagateEvent()) {
        pushEvent(evt);
      } */
    }
  }

  /**
   * Pops an event to propagate.
   */
  public PropagationEvent popEvent() {
    PropagationEvent event = (PropagationEvent) queue.popFirst();
    lastPopped = event;
    if (logger.isLoggable(Level.FINEST))
      logger.finest("just popped " + event.toString());
    return event;
  }


  /**
   * Adds an event to the queue.
   */

  public boolean pushEvent(PropagationEvent event) {
    queue.add(event);
    return true;
  }


  /**
   * Updates the priority level of an event (after adding a basic var).
   */

  public void updatePriority(PropagationEvent event) {
    queue.updatePriority(event);
  }


  /**
   * Removes all the events (including the popping one).
   */

  public void flushEventQueue() {
    if (null != lastPopped) {
      lastPopped.clear();
    }

    for (Iterator it = queue.iterator(); it.hasNext();) {
      PropagationEvent event = (PropagationEvent) it.next();
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
    for (Iterator it = queue.iterator(); it.hasNext();) {
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
