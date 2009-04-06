// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.branch;

import i_want_to_use_this_old_version_of_choco.ContradictionException;

/**
 * Branching objects are responsible for controlling the execution of the program at a point where
 * the control flow may be split between different branches
 */
public interface Branching {

  /**
   * selecting the object under scrutiny (that object on which an alternative will be set)
   *
   * @return the object on which an alternative will be set (often  a variable)
   */
  public Object selectBranchingObject() throws ContradictionException;
}
