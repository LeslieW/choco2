// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.branch;

import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.ContradictionException;

/**
 * an interface for objects controlling the selection of a variable (for heuristic purposes)
 */
public interface VarSelector {
  /**
   * each VarSelector is associated to a branching strategy
   *
   * @return the associated branching strategy
   */
  public IntBranching getBranching();

  /**
   * the VarSelector can be asked to return a variable
   *
   * @return a variable on whose domain an alternative can be set (such as a non instantiated search variable)
   */
  public AbstractVar selectVar() throws ContradictionException;
}
