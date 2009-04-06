// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.util.Arithm;

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
  }

  /* public void newTreeSearch() throws ContradictionException {
     super.newTreeSearch();
     nbIter = nbIter + 1;
     baseNbSol = nbSolutions;
     postTargetBound();
     problem.propagate();
   }*/

  /**
   * called before a new search tree is explored
   */
/*  public void endTreeSearch() {
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(false);
    }
    if (logger.isLoggable(Level.SEVERE)) {
      if (problem.feasible == Boolean.TRUE) { // <hca> bug quand feasible est � null
        logger.log(Level.SEVERE, "solve => " + new Integer(nbSolutions) + " solutions");
      } else {
        logger.severe("solve => no solution");
      }
      for (int i = 0; i < limits.size(); i++) {
        AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
        logger.severe(lim.pretty());
      }
    }
    popTraceUntil(baseWorld + 1);
    problem.worldPopUntil(baseWorld + 1);
  }*/

  // should we call a fullReset on limits ? (to reset cumulated counter?)
  protected void newLoop() throws ContradictionException {
    //initBounds();
    nbIter = nbIter + 1;
    baseNbSol = nbSolutions;
    postTargetBound();
    problem.propagate();
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
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(false);
    }
    /*if (logger.isLoggable(Level.SEVERE)) {
      if (problem.feasible == Boolean.TRUE) { // <hca> bug quand feasible est � null
        logger.log(Level.SEVERE, "solve => " + new Integer(nbSolutions) + " solutions");
      } else {
        logger.severe("solve => no solution");
      }
      for (int i = 0; i < limits.size(); i++) {
        AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
        logger.severe(lim.pretty());
      }
    }*/
    popTraceUntil(baseWorld + 1);
    problem.worldPopUntil(baseWorld + 1);
  }

  private void recordNoSolution() {
    // (trace(SVIEW,"... no solution with ~A:~S [~S]\n",obj.name,objtgt,a.limits),
    if (doMaximize) {
      upperBound = Arithm.min(upperBound, getObjectiveTarget() - 1);
    } else {
      lowerBound = Arithm.max(lowerBound, getObjectiveTarget() + 1);
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
    int w = problem.getWorldIndex() + 1;
    AbstractProblem pb = problem;
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
      assert(problem.getWorldIndex() == w);
      problem.worldPop();
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
    if (oneMoreLoop() == false) return Boolean.FALSE;
    try {
      newLoop();
      nextMove = INIT_SEARCH;
      bool = super.nextSolution();
    } catch (ContradictionException e) {
      bool = Boolean.FALSE;
    }
    endLoop();
    return bool;
  }
}
