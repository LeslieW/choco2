/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.search.real;

import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.variables.real.RealVar;

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
    if (solver.getFeasible()==Boolean.TRUE) {
      //[SVIEW] solve => ~S sol, best:~S [~S]// a.nbSol,(if a.doMaximize a.lowerBound else a.upperBound),a.limits
    } else if (solver.getFeasible() == Boolean.FALSE) {
      //[SVIEW] solve => no sol [~S]// a.limits
    } else {
      //[SVIEW] solve interrupted before any solution was found [~S]// a.limits
    }
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      if (LOGGER.isLoggable(Level.SEVERE))
        LOGGER.severe(lim.pretty());
    }
  }

}
