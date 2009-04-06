// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.prop;

import i_want_to_use_this_old_version_of_choco.ContradictionException;

import java.util.EventListener;

/**
 * An interface for all the search variable listeners.
 */
public interface VarEventListener extends EventListener {

  /**
   * Reacts to a modification of the variable
   * @param varIndex the index of the variable in the listener
   * @param evt the object representing the change on the variable
   */
  //public void propagateEvent(int varIndex, VarEvent evt)  throws ContradictionException;

  /**
   * propagation on domain revision.
   */
  public void awakeOnVar(int idx) throws ContradictionException;

  /**
   * This function connects a constraint with its variables in several ways.
   * Note that it may only be called once the constraint
   * has been fully created and is being posted to a problem.
   * Note that it should be called only once per constraint.
   * This can be a dynamic addition (undone upon backtracking) or not
   *
   * @param dynamicAddition
   */
  public void addListener(boolean dynamicAddition);

  /**
   * <i>Propagation:</i>
   * A constraint is active if it is connected to the network and if it
   * does propagate.
   */

  public boolean isActive();


  /**
   * <i>Propagation:</i>
   * un-freezes a constraint
   * [a constraint is active if it is connected to the network and if it
   * does propagate]
   */

  public void setActive();


  /**
   * <i>Propagation:</i>
   * freezes a constraint
   * [a constraint is active if it is connected to the network and if it
   * does propagate]
   */

  public void setPassive();


}
