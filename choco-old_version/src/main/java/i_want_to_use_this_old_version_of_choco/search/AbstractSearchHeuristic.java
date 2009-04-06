// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;
import i_want_to_use_this_old_version_of_choco.branch.IntBranching;

/**
 * An abstract class for all heuristics (variable, value, branching heuristics) related to search
 */
public abstract class AbstractSearchHeuristic {
  /**
   * the branching object owning the variable heuristic
   */
  protected AbstractIntBranching branching;

  /**
   * the problem to which the heuristic is related
   */
  protected AbstractProblem problem;

  /**
   * each IVarSelector is associated to a branching strategy
   *
   * @return the associated branching strategy
   */
  public IntBranching getBranching() {
    return branching;
  }
}
