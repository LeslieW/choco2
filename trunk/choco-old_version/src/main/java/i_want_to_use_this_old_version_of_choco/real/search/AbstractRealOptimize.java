package i_want_to_use_this_old_version_of_choco.real.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.real.RealMath;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.exp.RealIntervalConstant;
import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchSolver;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 20 juil. 2004
 */
public abstract class AbstractRealOptimize extends AbstractGlobalSearchSolver {
  /**
   * a boolean indicating whether we want to maximize (true) or minize (false) the objective variable
   */
  public boolean doMaximize;
  /**
   * the variable modelling the objective value
   */
  public RealVar objective;
  /**
   * the lower bound of the objective value.
   * This value comes from the problem definition; it is strengthened by the search history (solutions found & no-goods)
   */
  public double lowerBound = Double.NEGATIVE_INFINITY;
  /**
   * the upper bound of the objective value
   * This value comes from the problem definition; it is strengthened by the search history (solutions found & no-goods)
   */
  public double upperBound = Double.POSITIVE_INFINITY;

  /**
   * a tentative upper bound
   */
  public double targetUpperBound = Double.POSITIVE_INFINITY;

  /**
   * a tentative lower bound
   */
  public double targetLowerBound = Double.NEGATIVE_INFINITY;

  /**
   * constructor
   *
   * @param obj      the objective variable
   * @param maximize maximization or minimization ?
   */
  protected AbstractRealOptimize(RealVar obj, boolean maximize) {
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
  public double getObjectiveValue() {
    if (doMaximize) {
      return objective.getSup();
    } else {
      return objective.getInf();
    }
  }

  public double getBestObjectiveValue() {
    if (doMaximize) {
      return lowerBound;
    } else {
      return upperBound;
    }
  }

  /**
   * the target for the objective function: we are searching for a solution at least as good as this (tentative bound)
   */
  public double getObjectiveTarget() {
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
    logger.info("... solution with cost " + objective + ":" + objective.getValue());
    setBound();
    setTargetBound();
    super.recordSolution();
  }

  /**
   * resetting the optimization bounds
   */
  public void setBound() {
    double objval = getObjectiveValue();
    if (doMaximize) {
      lowerBound = Math.max(lowerBound, objval);
    } else {
      upperBound = Math.min(upperBound, objval);
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
    double newBound = RealMath.nextFloat(lowerBound);
    if (problem.feasible != Boolean.TRUE) {
      // trace(STALK,"search first sol ...")
    } else {
      // trace(STALK,"search target: ~A >= ~S ... ",a.objective.name,newBound))
      targetLowerBound = newBound;
    }
  }

  protected void setTargetUpperBound() {
    double newBound = RealMath.prevFloat(upperBound);
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
    objective.intersect(new RealIntervalConstant(targetLowerBound, Double.POSITIVE_INFINITY));
    //objective.setInf(targetLowerBound);
  }

  public void postTargetUpperBound() throws ContradictionException {
    objective.intersect(new RealIntervalConstant(Double.NEGATIVE_INFINITY, targetUpperBound));
    //objective.setSup(targetUpperBound);
  }

  /**
   * we use  targetBound data structures for the optimization cuts
   */
  public void postDynamicCut() throws ContradictionException {
    postTargetBound();
    //problem.propagate();
  }
}
