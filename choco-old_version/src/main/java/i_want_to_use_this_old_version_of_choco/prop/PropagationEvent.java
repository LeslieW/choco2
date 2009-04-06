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
 * An interface for all implementations of events.
 */
public interface PropagationEvent {
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
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
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