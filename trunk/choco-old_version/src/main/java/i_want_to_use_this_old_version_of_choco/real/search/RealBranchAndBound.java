package i_want_to_use_this_old_version_of_choco.real.search;

import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchLimit;

import java.util.logging.Level;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 20 juil. 2004
 */
public class RealBranchAndBound extends AbstractRealOptimize {
  public RealBranchAndBound(RealVar obj, boolean maximize) {
    super(obj, maximize);
  }

// TODO: seeems useless !    
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
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      if (logger.isLoggable(Level.SEVERE))
        logger.severe(lim.pretty());
    }
  }

}
