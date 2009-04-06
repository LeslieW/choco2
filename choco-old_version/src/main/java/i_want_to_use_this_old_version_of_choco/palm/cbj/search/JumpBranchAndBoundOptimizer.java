package i_want_to_use_this_old_version_of_choco.palm.cbj.search;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchLimit;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class JumpBranchAndBoundOptimizer extends JumpAbstractOptimizer {
  public JumpBranchAndBoundOptimizer(IntDomainVar obj, boolean maximize) {
    super(obj, maximize);
  }

  public void newTreeSearch() {
    initBounds();
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(true);
    }
  }

  public void endTreeSearch() {
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(false);
    }
    if (problem.feasible == Boolean.TRUE) {
      //[SVIEW] solve => ~S sol, best:~S [~S]// a.nbSol,(if a.doMaximize a.lowerBound else a.upperBound),a.limits
    } else if (problem.feasible == Boolean.FALSE) {
      //[SVIEW] solve => no sol [~S]// a.limits
    } else {
      //[SVIEW] solve interrupted before any solution was found [~S]// a.limits
    }
  }

}
