package i_want_to_use_this_old_version_of_choco.palm.benders.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Solution;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.BendersProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.benders.MasterSlavesRelation;
import i_want_to_use_this_old_version_of_choco.palm.cbj.explain.JumpExplanation;
import i_want_to_use_this_old_version_of_choco.palm.cbj.search.JumpContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;
import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchLimit;

import java.util.ArrayList;
import java.util.logging.Level;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************


/**
 * Benders search in case of problem of the form P_{xy} (
 * an optimization function on the variable of the master AND slaves)
 * assuming that the master provide a relaxation of the whole problem
 */
public class MasterSlaveOptimizer extends MasterOptimizer {

  /**
   * store the optimal solution of each subproblem
   */
  protected int[] subOptSol;

  /**
   * store the objectives variables of each subproblem
   */
  protected IntDomainVar[] subOptVar;


  // TODO : allow some/all of the subproblems to be objectives free
  public MasterSlaveOptimizer(IntDomainVar mobj, IntDomainVar[] objs, boolean maximize, MasterSlavesRelation relation) {
    super(mobj, objs.length, relation);
    //this.master = new SubSearchSolver(objs[0].getProblem(),false);
    this.master = new SubOptimizer(mobj, maximize, false);
    this.subproblems = new SubOptimizer(objs[0], maximize, true);
    this.maximize = maximize;
    this.subOptVar = objs;
    this.subOptSol = new int[objs.length];
    zobjective = mobj;
  }


  public void solveSubProblems() {
    BendersProblem pb = (BendersProblem) problem;
    boolean boundReached = false;
    if (logger.isLoggable(Level.FINE)) logMasterSolution();
    for (int i = 0; i < pb.getNbSubProblems(); i++) { // solve the subproblems
      boundReached = goToSubProblem(i);
      if (!boundReached) {
        problem.worldPush(); // push a world
        Boolean subres = subproblems.nextOptimalSolution(masterWorld);
        if (subres != null) {
          bendersCut[i] = pb.getContradictionExplanation();
          ((JumpExplanation) bendersCut[i]).delete(masterWorld + 1);  // remove the world pushed between master and slaves
          storeCuts(bendersCut[i], i);
          if (subres == Boolean.TRUE) {
            storePartialSolution(i);
            nbFeasibleProblems += 1;
          } else if (subres == Boolean.FALSE) {
            if (((JumpExplanation) bendersCut[i]).nogoodSize() == 0)
              feasible = false;
          }
        } // sinon un limite a �t� atteinte
        problem.worldPopUntil(masterWorld);
        if (masterWorld == pb.getEnvironment().getWorldIndex()) // clean the state if contradiction occur at the root node of the subproblem
          pb.getPropagationEngine().flushEvents();
      } else {
        bendersCut[i] = pb.getContradictionExplanation();
        storeCuts(bendersCut[i], i);
        break;
      }
    }
  }


  public boolean goToSubProblem(int i) {
    ((SubOptimizer) subproblems).changeGoal(subgoals[i], subOptVar[i]);
    try { // speedup the resolution of subproblems by updating their bounds (not mandatory)
      if (maximize && i == ((BendersProblem) problem).getNbSubProblems()) {
        ((ExplainedIntVar) subOptVar[i]).updateInf(targetLowerBound - decomposition.computeBound(zobjective.getVal(), subOptSol, i), -1,
            ((BendersProblem) problem).makeExplanation());
      } else if (!maximize) {
        //System.out.println(targetUpperBound + "jkjkljkljlkj " + (targetUpperBound - decomposition.computeBound(subOptSol, i + 1)));
        ((ExplainedIntVar) subOptVar[i]).updateSup(targetUpperBound - decomposition.computeBound(zobjective.getVal(), subOptSol, i), -1,
            ((BendersProblem) problem).makeExplanation());
      }
    } catch (JumpContradictionException e) {
      if (logger.isLoggable(Level.FINE))
        logger.fine("Contradiction while updating the bound of subpb n�" + i + " to " + (targetUpperBound - decomposition.computeBound(zobjective.getVal(), subOptSol, i)));
      ((BendersProblem) problem).setContradictionExplanation(e.getExplanation());
      return true;
    } catch (ContradictionException e) {
      throw new Error("contradiction exception in goToSubProblem " + e);
    }
    return false;
  }

  public void storePartialSolution(int subpb) {
    super.storePartialSolution(subpb);
    subOptSol[subpb] = subOptVar[subpb].getVal();
  }

  public void manageCuts() {
    super.manageCuts();
    if (nbFeasibleProblems == ((BendersProblem) problem).getNbSubProblems()) {
      int zbound = decomposition.computeBound(zobjective.getVal(), subOptSol, 0);
      if (updateTargetBound(zbound)) {
        objective = zbound;
        restorePartialSolutions();
        recordSolution();
        printBestSol();
        if (logger.isLoggable(Level.FINE))
          logBestSol();
      }
    }
    resetSubPbData();
  }

  protected Solution makeSolutionFromCurrentState() {
    Solution sol = super.makeSolutionFromCurrentState();
    sol.recordIntObjective(objective);
    return sol;
  }

  public void resetSubPbData() {
    nbFeasibleProblems = 0;
    for (int i = 0; i < subOptSol.length; i++) {
      subOptSol[i] = -1;
    }
  }

  public boolean updateTargetBound(int globalbound) {
    if (maximize && globalbound >= targetLowerBound) {
      targetLowerBound = globalbound + 1;
      return true;
    } else if (!maximize && globalbound <= targetUpperBound) {
      targetUpperBound = globalbound - 1;
      return true;
    }
    return false;
  }

  public void nextMasterMove() {
    master.nextMove = INIT_SEARCH;
    master.traceStack = new ArrayList();
    master.solutions.clear();
    master.currentTraceIndex = -1;
    fail = null;
    problem.worldPopUntil(baseWorld);
    problem.worldPush();
    ((SubOptimizer) master).initBounds();
    try {
      postKnownBound();
    } catch (ContradictionException e) {
      stop = true;
    }
    cuts.constAwake(true);
  }

  public void postKnownBound() throws ContradictionException {
    if (maximize) {
      Explanation expl = new JumpExplanation(this.problem);
      ((ExplainedIntVar) zobjective).updateInf(objective + 1, -1, expl);
    } else {
      Explanation expl = new JumpExplanation(this.problem);
      ((ExplainedIntVar) zobjective).updateSup(objective - 1, -1, expl);
    }
  }

  // ----------------------------------------------------
  // ------------------- logs ---------------------------
  // ----------------------------------------------------

  public void printBestSol() {
    System.out.print("... global solution with costs");
    for (int i = 0; i < subOptSol.length; i++) {
      System.out.print(" " + subOptVar[i] + ":" + subOptSol[i]);
    }
    System.out.print(" = " + objective + " - ");
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      System.out.print(lim.pretty() + " ");
    }
    System.out.println("");
  }


  public void logBestSol() {
    String ms = "";
    ms += ("... global solution with costs");
    for (int i = 0; i < subOptSol.length; i++) {
      ms += (" " + subOptVar[i] + ":" + subOptSol[i]);
    }
    ms += (" = " + objective + " - ");
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      ms += (lim.pretty() + " ");
    }
    logger.fine(ms);
  }
}
