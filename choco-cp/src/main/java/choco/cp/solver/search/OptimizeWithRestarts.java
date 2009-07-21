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
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractOptimize;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;

public class OptimizeWithRestarts extends AbstractOptimize {
  /**
   * counting the number of iterations
   */
  protected int nbIter = 0;

  /**
   * counting the overall number of solutions
   */
  protected int baseNbSol = 0;

  /**
   * total nb of backtracks (all trees in the optimization process)
   */
  protected int nbBkTot = 0;

  /**
   * total nb of nodes expanded in all trees
   */
  protected int nbNdTot = 0;

  public OptimizeWithRestarts(IntDomainVarImpl obj, boolean maximize) {
    super(obj, maximize);
      setSearchLoop(new SearchLoop(this));
  }

  /* public void newTreeSearch() throws ContradictionException {
     super.newTreeSearch();
     nbIter = nbIter + 1;
     baseNbSol = nbSolutions;
     postTargetBound();
     model.propagate();
   }*/

  /**
   * called before a new search tree is explored
   */
/*  public void endTreeSearch() {
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(false);
    }
    if (LOGGER.isLoggable(Level.SEVERE)) {
      if (model.feasible == Boolean.TRUE) { // <hca> bug quand feasible est � null
        LOGGER.log(Level.SEVERE, "solve => " + new Integer(nbSolutions) + " solutions");
      } else {
        LOGGER.severe("solve => no solution");
      }
      for (int i = 0; i < limits.size(); i++) {
        AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
        LOGGER.severe(lim.pretty());
      }
    }
    popTraceUntil(baseWorld + 1);
    model.worldPopUntil(baseWorld + 1);
  }*/

  // should we call a fullReset on limits ? (to reset cumulated counter?)
  protected void newLoop() throws ContradictionException {
    //initBounds();
    nbIter = nbIter + 1;
    baseNbSol = getSolutionCount();
    postTargetBound();
    solver.propagate();
    // time_set()
  }

  protected void endLoop() {
/*
    -> let t := time_get() in
       trace(SVIEW,"Optimisation over => ~A(~A) = ~S found in ~S iterations, [~S], ~S ms.\n",
              (if a.doMaximize "max" else "min"),a.objective.name,
               getBestObjectiveValue(a),  // v1.013 using the accessor
              a.nbIter,a.limits,t)]
*/
	  limitManager.reset();
    /*if (LOGGER.isLoggable(Level.SEVERE)) {
      if (model.feasible == Boolean.TRUE) { // <hca> bug quand feasible est � null
        LOGGER.log(Level.SEVERE, "solve => " + new Integer(nbSolutions) + " solutions");
      } else {
        LOGGER.severe("solve => no solution");
      }
      for (int i = 0; i < limits.size(); i++) {
        AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
        LOGGER.severe(lim.pretty());
      }
    }*/
    clearTrace();
    solver.worldPopUntil(baseWorld + 1);
  }

  private void recordNoSolution() {
    // (trace(SVIEW,"... no solution with ~A:~S [~S]\n",obj.name,objtgt,a.limits),
    if (doMaximize) {
      upperBound = Math.min(upperBound, getObjectiveTarget() - 1);
    } else {
      lowerBound = Math.max(lowerBound, getObjectiveTarget() + 1);
    }
  }

  /**
   * loop until the lower bound equals the upper bound
   *
   * @return true if one more loop is needed
   */
  protected boolean oneMoreLoop() {
    return (lowerBound < upperBound);
  }

  /*
   * @deprecated replaced by incrementalRun
   */
  /*public void run() {
    int w = model.getWorldIndex() + 1;
    AbstractModel pb = model;
    boolean finished = false;
    //newLoop();
    try {
      pb.propagate();
    } catch (ContradictionException e) {
      finished = true;
      recordNoSolution();
    }
    if (!finished) {
      pb.worldPush();
      while (oneMoreLoop()) {
        boolean foundSolution = false;
        try {
          newTreeSearch();
          if (mainGoal.explore(1)) {
            foundSolution = true;
          }
        } catch (ContradictionException e) {
        }
        endTreeSearch();
        if (!foundSolution) {
          recordNoSolution();
        }
      }
      assert(model.getWorldIndex() == w);
      model.worldPop();
    }
    endLoop();
    if ((maxNbSolutionStored > 0) && existsSolution()) {
      restoreBestSolution();
    }
  } */

  public void incrementalRun() {
    initBounds();
    super.incrementalRun();
  }

  public Boolean nextSolution() {
    Boolean bool;
    if (oneMoreLoop() == false) {
    	LOGGER.warning("it works !?");
    	return Boolean.FALSE;
    }
    try {
      newLoop();
      nextMove = INIT_SEARCH;
      clearTrace();
      bool = super.nextSolution();
    } catch (ContradictionException e) {
      bool = Boolean.FALSE;
    }
    endLoop();
    return bool;
  }
}
