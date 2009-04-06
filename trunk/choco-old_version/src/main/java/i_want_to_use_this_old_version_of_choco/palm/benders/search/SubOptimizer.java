package i_want_to_use_this_old_version_of_choco.palm.benders.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.BendersProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.JumpProblem;
import i_want_to_use_this_old_version_of_choco.palm.cbj.explain.JumpExplanation;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;
import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchLimit;
import i_want_to_use_this_old_version_of_choco.search.IntBranchingTrace;
import i_want_to_use_this_old_version_of_choco.util.Arithm;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * A searchsolver for optimization
 */
public class SubOptimizer extends SubSearchSolver {

  /**
   * a boolean indicating whether we want to maximize (true) or minize (false) the objective variable
   */
  public boolean doMaximize;
  /**
   * the variable modelling the objective value
   */
  public ExplainedIntVar objective;
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
   * Master solution
   */
  protected int[] msol;


  /**
   * constructor
   *
   * @param obj      the objective variable
   * @param maximize maximization or minimization ?
   */
  protected SubOptimizer(IntDomainVar obj, boolean maximize, boolean slave) {
    super(obj.getProblem(), slave);
    if (!slave)
      msol = new int[((BendersProblem) obj.getProblem()).getMasterVariablesList().size()];
    objective = (ExplainedIntVar) obj;
    doMaximize = maximize;
    stopAtFirstSol = false;
  }

  protected void changeGoal(AbstractIntBranching branching, IntDomainVar newObjective) {
    super.changeGoal(branching);
    objective = (ExplainedIntVar) newObjective;
    solutions.clear();
    initBounds();
  }

  public Boolean nextOptimalSolution(int masterWorld) {
    while (nextSolution() == Boolean.TRUE) {
      ;
    }
    if ((maxNbSolutionStored > 0) && existsSolution()) {
      if (!slave) {
        problem.worldPopUntil(baseWorld);
        restoreBestSolutionBySearch();
      } else {
        problem.worldPopUntil(masterWorld);
        restoreBestSolution();
      }
      return Boolean.TRUE;
    } else if (isEncounteredLimit())
      return null;
    else
      return Boolean.FALSE;
  }


  public void solutionFound(IntBranchingTrace ctx) {
    recordSolution();
    currentFail = ((BendersProblem) problem).makeExplanation();
    ((JumpExplanation) currentFail).add(1, problem.getWorldIndex());
    if (!slave) {
      storeMasterSolution();
    }
    nextMove = UP_BRANCH;
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

  public void recordSolution() {
    logIntermediateSol();
    problem.feasible = Boolean.TRUE;
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
      postLowerBound(targetLowerBound);
    } else {
      postUpperBound(targetUpperBound);
    }
  }

  public void postKnownBound() throws ContradictionException {
    if (doMaximize) {
      Explanation expl = new JumpExplanation(this.problem);
      objective.updateInf(lowerBound, -1, expl);
    } else {
      Explanation expl = new JumpExplanation(this.problem);
      objective.updateSup(upperBound, -1, expl);
    }
  }

  public void postLowerBound(int lb) throws ContradictionException {
    Explanation expl = new JumpExplanation(this.problem);
    if (problem.getWorldIndex() >= 1)
      ((JumpExplanation) expl).add(1, problem.getWorldIndex());
    objective.updateInf(lb, -1, expl);
  }

  public void postUpperBound(int ub) throws ContradictionException {
    Explanation expl = new JumpExplanation(this.problem);
    if (problem.getWorldIndex() >= 1)
      ((JumpExplanation) expl).add(1, problem.getWorldIndex());
    objective.updateSup(ub, -1, expl);
  }

  /**
   * we use  targetBound data structures for the optimization cuts
   */
  public void postDynamicCut() throws ContradictionException {
    postTargetBound();
    //problem.propagate();
  }

  public void storeMasterSolution() {
    for (int i = 0; i < msol.length; i++) {
      msol[i] = ((IntDomainVar) ((BendersProblem) problem).getMasterVariablesList().get(i)).getVal();
    }
  }

  public void restoreBestSolutionBySearch() {
    try {
      traceStack.clear();
      ArrayList mvs = ((BendersProblem) problem).getMasterVariablesList();
      for (int i = 0; i < msol.length; i++) {
        if (!((IntDomainVar) mvs.get(i)).isInstantiated() && mvs.get(i) != objective) {
          problem.worldPush();
          ExplainedIntVar y = (ExplainedIntVar) mvs.get(i);
          Explanation exp = ((JumpProblem) problem).makeExplanation(problem.getWorldIndex());
          // new JumpExplanation(manager.problem.getWorldIndex() - 1, manager.problem);
          y.instantiate(msol[i], -1, exp);
          problem.propagate();
          IntBranchingTrace ctx = new IntBranchingTrace();
          ctx.setBranching(mainGoal);
          ctx.setBranchingObject(y);
          ctx.setBranchIndex(msol[i]);
          traceStack.add(ctx);
        }
      }
      //TODO : set and real
      problem.propagate();
      if (!objective.isInstantiated()) {
        postKnownBound();
        problem.propagate();
      }
    } catch (ContradictionException e) {
      logger.severe("BUG in restoring solution !!!!!!!!!!!!!!!!");
      throw(new Error("Restored solution not consistent !!"));
    }
  }

  public void logIntermediateSol() {
    if (Logger.getLogger("choco").isLoggable(Level.FINE)) {
      String msg = "... solution with cost " + objective + ": " + objective.getVal() + "   ";
      for (int i = 0; i < limits.size(); i++) {
        AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
        msg += lim.pretty() + " ";
      }
      Logger.getLogger("choco").fine(msg);
    }
  }

}
