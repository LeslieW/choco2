// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;


/**
 * A branch and bound implementation of optimizer solver.
 */
public class BranchAndBound extends AbstractOptimize {

  /**
   * Builds a new optimizing solver with the specified variable.
   * @param obj is the variable that should be optimized
   * @param maximize states if the objective variable should be maximized
   */
  public BranchAndBound(final IntDomainVarImpl obj, 
      final boolean maximize) {
    super(obj, maximize);
  }

  /**
   * Called when a new search tree is built. It initializes the bounds and
   * resets all limits.
   */
  public void newTreeSearch() {
    initBounds();
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(true);
    }
  }

  /**
   * Called when a new search tree has been completely browsed. It
   * resets all limits.
   */  
  public void endTreeSearch() {
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(false);
    }
    if (problem.feasible == Boolean.TRUE) {
      //[SVIEW] solve => ~S sol, best:~S [~S]
      // a.nbSol,(if a.doMaximize a.lowerBound else a.upperBound),a.limits
    } else if (problem.feasible == Boolean.FALSE) {
      //[SVIEW] solve => no sol [~S]// a.limits
    } else {
      //[SVIEW] solve interrupted before any solution was found [~S]// a.limits
    }
  }
}
