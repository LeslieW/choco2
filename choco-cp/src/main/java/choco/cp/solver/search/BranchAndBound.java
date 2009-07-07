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
package choco.cp.solver.search;

import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.AbstractOptimize;


/**
 * A branch and bound implementation of optimizer strategy.
 */
public class BranchAndBound extends AbstractOptimize {

  /**
   * Builds a new optimizing strategy with the specified variable.
   * @param obj is the variable that should be optimized
   * @param maximize states if the objective variable should be maximized
   */
  public BranchAndBound(final IntDomainVarImpl obj,
      final boolean maximize) {
    super(obj, maximize);
      setSearchLoop(new SearchLoop(this));
  }

  /**
   * Called when a new search tree is built. It initializes the bounds and
   * resets all limits.
   */
  public void newTreeSearch() {
    initBounds();
    resetLimits(true);
  }

//  /**
//   * Called when a new search tree has been completely browsed. It
//   * resets all limits.
//   */  
//  public void endTreeSearch() {
//	  super.endTreeSearch();
//	  //FIXME why did we not reset limits in the superclass if it is useful ?
//	 
//    
//    if (solver.getFeasible() == Boolean.TRUE) {
//      //[SVIEW] solve => ~S sol, best:~S [~S]
//      // a.nbSol,(if a.doMaximize a.lowerBound else a.upperBound),a.limits
//    } else if (solver.getFeasible() == Boolean.FALSE) {
//      //[SVIEW] solve => no sol [~S]// a.limits
//    } else {
//      //[SVIEW] solve interrupted before any solution was found [~S]// a.limits
//    }
//  }
}
