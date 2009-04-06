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

public abstract class AbstractOptimize extends AbstractGlobalSearchSolver {
  /**
   * a boolean indicating whether we want to maximize (true) or minize (false) the objective variable
   */
  public boolean doMaximize;
  /**
   * the variable modelling the objective value
   */
  public IntDomainVarImpl objective;
  /**
   * the lower bound of the objective value.
   * This value comes from the problem definition; it is strengthened by the search history (solutions found & no-goods)
   */
  public int lowerBound = Integer.MIN_VALUE;
  /**
   * the upper bound of the objective value
   * This value comes from the problem definition; it is strengthened by the search history (solutions found & no-goods)
   */
  public int upperBound = Integer.MAX_VALUE;

  /**
   * a tentative upper bound
   */
  public int targetUpperBound = Integer.MAX_VALUE;

  /**
   * a tentative lower bound
   */
  public int targetLowerBound = Integer.MIN_VALUE;

  /**
   * constructor
   *
   * @param obj      the objective variable
   * @param maximize maximization or minimization ?
   */
  protected AbstractOptimize(IntDomainVarImpl obj, boolean maximize) {
    super(obj.getProblem());
    objective = obj;
    doMaximize = maximize;
  }

  /**
   * v1.0 accessing the objective value of an optimization problem
   * (note that the objective value may not be instantiated, while all other variables are)
   *
   * @return the current objective value
   */
  public int getObjectiveValue() {
    if (doMaximize) {
      return objective.getSup();
    } else {
      return objective.getInf();
    }
  }

  public int getBestObjectiveValue() {
    if (doMaximize) {
      return lowerBound;
    } else {
      return upperBound;
    }
  }

  /**
   * the target for the objective function: we are searching for a solution at least as good as this (tentative bound)
   */
  public int getObjectiveTarget() {
    if (doMaximize) {
      return targetLowerBound;
    } else {
      return targetUpperBound;
    }
  }

  /**
   * initialization of the optimization bound data structure
   */
  public void initBounds() {
    lowerBound = objective.getInf();
    upperBound = objective.getSup();
    targetLowerBound = objective.getInf();
    targetUpperBound = objective.getSup();

  }

  /**
   * TODO
   */
  public void recordSolution() {
    problem.feasible = Boolean.TRUE;
    //nbSolutions = nbSolutions + 1;
    // trace(SVIEW,"... solution with ~A:~S [~S]\n",obj.name,objval,a.limits),  // v1.011 <thb>
    StringBuffer b = new StringBuffer();
    b.append("... solution with cost ").append(objective).append(": ")
        .append(objective.getVal()).append("   ");
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      b.append(lim.pretty()).append(" ");
    }
    logger.info(b.toString());
    setBound();
    setTargetBound();
    super.recordSolution();
  }

  /**
   * resetting the optimization bounds
   */
  public void setBound() {
    int objval = getObjectiveValue();
    if (doMaximize) {
      lowerBound = Arithm.max(lowerBound, objval);
    } else {
      upperBound = Arithm.min(upperBound, objval);
    }
  }

  /**
   * resetting the values of the target bounds (bounds for the remaining search)
   */
  public void setTargetBound() {
    if (doMaximize) {
      setTargetLowerBound();
    } else {
      setTargetUpperBound();
    }
  }

  protected void setTargetLowerBound() {
    int newBound = lowerBound + 1;
    if (problem.feasible != Boolean.TRUE) {
      // trace(STALK,"search first sol ...")
    } else {
      // trace(STALK,"search target: ~A >= ~S ... ",a.objective.name,newBound))
      targetLowerBound = newBound;
    }
  }

  protected void setTargetUpperBound() {
    int newBound = upperBound - 1;
    if (problem.feasible != Boolean.TRUE) {
      // trace(STALK,"search first sol ...")
    } else {
      // trace(STALK,"search target: ~A <= ~S ... ",a.objective.name,newBound))
      targetUpperBound = newBound;
    }
  }

  /**
   * propagating the optimization cuts from the new target bounds
   */
  public void postTargetBound() throws ContradictionException {
    if (doMaximize) {
      postTargetLowerBound();
    } else {
      postTargetUpperBound();
    }
  }

  public void postTargetLowerBound() throws ContradictionException {
    objective.setInf(targetLowerBound);
  }

  public void postTargetUpperBound() throws ContradictionException {
    objective.setSup(targetUpperBound);
  }

  /**
   * we use  targetBound data structures for the optimization cuts
   */
  public void postDynamicCut() throws ContradictionException {
    postTargetBound();
    //problem.propagate();
  }
}
