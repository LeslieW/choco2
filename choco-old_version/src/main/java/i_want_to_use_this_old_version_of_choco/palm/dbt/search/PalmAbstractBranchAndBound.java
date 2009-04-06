package i_want_to_use_this_old_version_of_choco.palm.dbt.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 20 juil. 2004
 */

public abstract class PalmAbstractBranchAndBound extends PalmGlobalSearchSolver {
  /**
   * States if the solver is maximizing (or minimizing).
   */

  protected boolean maximizing = false;

  /**
   * The variable that should be maximized (or minimized).
   */

  protected Var objective;

  /**
   * Dynamic cut constraints.
   */

  protected LinkedList dynamicCuts;


  public PalmAbstractBranchAndBound(AbstractProblem pb, Var obj, boolean max) {
    super(pb);
    objective = obj;
    maximizing = max;
    dynamicCuts = new LinkedList();
  }

  public void incrementalRun() {
    try {
      //problem.post(problem.geq(objective,lowerBound));
      //problem.post(problem.leq(objective,upperBound));
      if (Logger.getLogger("choco").isLoggable(Level.INFO))
        Logger.getLogger("choco").info("Initial Propagation");
      problem.propagate();
    } catch (ContradictionException e) {
      if (Logger.getLogger("choco").isLoggable(Level.INFO))
        Logger.getLogger("choco").info("Optimality proven");
      this.finished = true;
      this.problem.feasible = Boolean.FALSE;
    }
    this.problem.feasible = Boolean.TRUE;
    while (this.problem.feasible.booleanValue()) {
      if (Logger.getLogger("choco").isLoggable(Level.INFO))
        Logger.getLogger("choco").info("Searching for one solution");
      runonce();
      recordSolution();
      if (this.problem.feasible.booleanValue()) {
        logger.info("... solution with cost " + objective + ":" +
            (objective instanceof RealVar ? "" + ((RealVar) objective).getValue() : "") +
            (objective instanceof IntDomainVar ? "" + ((IntDomainVar) objective).getVal() : ""));
        postDynamicCut();
      }
    }
  }

  /**
   * solving the feasibility problem
   * same code as in run(PalmGlobalSearchSolver) in order to be able
   * to store consecutive solutions ...
   */
  public void runonce() {
    //long time = System.currentTimeMillis();
    try {
      while (!this.finished) {
        try {
          if (Logger.getLogger("choco").isLoggable(Level.FINE))
            Logger.getLogger("choco").fine("New extension launched.");
          this.extend();
          this.problem.propagate();
        } catch (PalmContradiction e) {
          this.repair();
        }
      }
      this.problem.feasible = Boolean.TRUE;
    } catch (ContradictionException e) {
      this.finished = true;
      this.problem.feasible = Boolean.FALSE;
    }
    //this.setRuntimeStatistic(PalmProblem.CPU, (int) (System.currentTimeMillis() - time));
  }

  public void postDynamicCut() {
    Constraint cut;
    try {
      reset();
      cut = getDynamicCut();
      dynamicCuts.add(cut);
      problem.post(cut);
      try {
        problem.propagate();
      } catch (PalmContradiction e) {
        repair();
      }
    } catch (ContradictionException e) {
      this.finished = true;
      this.problem.feasible = Boolean.FALSE;
    }
  }

  public abstract Constraint getDynamicCut();

  public abstract Number getOptimumValue();
}
