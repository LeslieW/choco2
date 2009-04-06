// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.prop;

import i_want_to_use_this_old_version_of_choco.ContradictionException;

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
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */

  public void propagateSomeEvents() throws ContradictionException;

  /**
   * Propagate one single event from the queue).
   *
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
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
