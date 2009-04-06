//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran?ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.search;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.PalmVar;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.PalmEngine;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.pathrepair.PathRepairLearn;
import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchLimit;
import i_want_to_use_this_old_version_of_choco.search.GlobalSearchLimit;
import i_want_to_use_this_old_version_of_choco.search.Solve;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A default solver for Palm. By default, it uses <code>mac-dbt</code> algorithm (for extension and repairing).
 */

public class PalmGlobalSearchSolver extends Solve {

  public static final int LIMIT_TIME = 0;
  public static final int LIMIT_NODES = 1;

  /**
   * States if the search is finished.
   */

  protected boolean finished = false;


  /**
   * States if the problem is feasible.
   */

  protected boolean feasible = false;


  /**
   * Maintains the state of the search (past branching decisions).
   */

  protected PalmState state;


  /**
   * Learning algorithms.
   */

  protected PalmLearn learning;

  /**
   * Extension algrithm using some branching strategies.
   */

  protected PalmExtend extending;


  /**
   * Repairing algorithm.
   */

  protected PalmRepair repairing;


  /**
   * Creates a solver for the specified problem. It initiliazes all contained structures (repairer, learner...).
   *
   * @param pb The problem to search.
   */

  public PalmGlobalSearchSolver(AbstractProblem pb) {
    super(pb);
  }

  public void newTreeSearch() {
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(true);
    }
    nbSolutions = 0;
  }

  public void endTreeSearch() {
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(false);
    }
    if (logger.isLoggable(Level.SEVERE)) {
      if (problem.feasible == Boolean.TRUE) {
        logger.log(Level.INFO, "solve => " + new Integer(nbSolutions) + " solutions");
      } else {
        logger.info("solve => no solution");
      }
      printRuntimeStatistics();
    }
  }

  /**
   * Resets the solver (statistics and business data).
   */

  public void reset() {
    this.finished = false;
    ((PalmEngine) this.problem.getPropagationEngine()).resetDummyVariable();
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(false);
    }
    //for (int i = 0; i < 3; i++) {
    //  this.setRuntimeStatistic(i, 0);
    //}
  }

  /**
   * Attaches a new PalmState to the solver.
   *
   * @param ext The PalmState that should be used for maintaining state of the search.
   */

  public void attachPalmState(PalmState ext) {
    this.state = ext;
    ext.setManager(this);
  }


  /**
   * Attaches a new extension tool.
   */

  public void attachPalmExtend(PalmExtend ext) {
    this.extending = ext;
    ext.setManager(this);
  }


  /**
   * Attaches a new list of branching. It creates a linked list from the specified list of branching.
   */

  public void attachPalmBranchings(List lbr) {
    PalmAbstractBranching previous = null;
    for (Iterator iterator = lbr.iterator(); iterator.hasNext();) {
      PalmAbstractBranching branching = (PalmAbstractBranching) iterator.next();
      if (previous != null) previous.setNextBranching(branching);
      branching.setExtender(this.extending);
      previous = branching;
    }
    if (lbr.size() > 0) this.extending.setBranching((PalmAbstractBranching) lbr.get(0));
  }

  /**
   * Attache a new learning tool.
   *
   * @param ext The learning extension the solver shoudl use.
   */

  public void attachPalmLearn(PalmLearn ext) {
    this.learning = ext;
    ext.setManager(this);
  }


  /**
   * Attaches a new PalmRepair algorithm.
   *
   * @param ext The new repairing algorithm the solver must use.
   */

  public void attachPalmRepair(PalmRepair ext) {
    this.repairing = ext;
    ext.setManager(this);
  }

  /**
   * Chechs if the solver has finished searching a solution.
   */

  public boolean isFinished() {
    return finished;
  }


  /**
   * Sets if the solver has finished searching a solution.
   *
   * @param f New value.
   */

  public void setFinished(boolean f) {
    this.finished = f;
  }


  /**
   * Stores the current solution.
   */

  public void recordSolution() {
    if (this.problem.feasible.booleanValue()) {
      PalmSolution solution = new PalmSolution(this.problem);

      for (int i = 0; i < this.problem.getNbIntVars(); i++) {
        solution.recordIntValue(i, ((IntDomainVar) this.problem.getIntVar(i)).getInf());
      }

      if (this instanceof PalmBranchAndBound) {
        // TODO : il faut rendre ce generique pour entier ou flottant...
        //solution.recordIntObjective(((PalmBranchAndBound) this).getObjectiveValue());
      }
      if (learning instanceof PathRepairLearn) { // When a solveAll is called with decision repair, we store the solution as a nogood
        ((PathRepairLearn) learning).addSolution();
      }
      for (int i = 0; i < limits.size(); i++) {
        solution.recordStatistic(i, ((AbstractGlobalSearchLimit) limits.get(i)).getNb());
      }

      this.solutions.add(0, solution);
      this.nbSolutions += 1;
    }
  }


  /**
   * Starts solving.
   */

  public void incrementalRun() {
    try {
      this.finished = false;
      try {
        this.problem.propagate();
      } catch (PalmContradiction e) {
        this.repair();
      }
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
      this.recordSolution();
    } catch (ContradictionException e) {
      this.finished = true;
      //this.problem.feasible = Boolean.FALSE;
    }
  }


  /**
   * Extends the state of the search thanks to the extending algorithm.
   *
   * @throws ContradictionException
   */

  public void extend() throws ContradictionException {
    this.extending.explore(this.extending.getBranching());
  }


  /**
   * Tries to repair the state for finding a consistent state.
   *
   * @throws ContradictionException
   */

  public void repair() throws ContradictionException {
    PalmEngine pe = (PalmEngine) this.problem.getPropagationEngine();
    if (pe.isContradictory()) {
      PalmVar cause = (PalmVar) pe.getContradictionCause();
      PalmExplanation expl = (PalmExplanation) ((PalmProblem) this.problem).makeExplanation();


      cause.self_explain(PalmIntDomain.DOM, expl);
      if (Logger.getLogger("choco").isLoggable(Level.FINE)) {
        Logger.getLogger("choco").fine("Repairing");
        Logger.getLogger("choco").fine("Cause : " + cause);
        Logger.getLogger("choco").fine("Expl : " + expl);
      }

      pe.setContradictory(false);
      this.learning.learnFromContradiction(expl);
      if (expl.isEmpty()) {
        pe.raiseSystemContradiction();
      } else {
        AbstractConstraint constraint = (AbstractConstraint) this.repairing.selectDecisionToUndo(expl);
        if (constraint != null) {
          if (((PalmConstraintPlugin) constraint.getPlugIn()).getWeight() <= ((PalmProblem) this.problem).getMaxRelaxLevel()) {
            //this.incRuntimeStatistic(PalmProblem.RLX, 1);
            endTreeNode();
            if (((PalmConstraintPlugin) constraint.getPlugIn()).getWeight() > 0) {
            } else {
              this.state.removeDecision(constraint);
            }

            int timeStamp = PalmConstraintPlugin.getLastTimeStamp();
            try {
              ((PalmProblem) this.problem).remove(constraint);
              this.problem.propagate();
            } catch (PalmContradiction e) {
              this.repair();
            }

            if (((PalmConstraintPlugin) constraint.getPlugIn()).getWeight() == 0) {
              // Negation
              expl.delete(constraint);

              Constraint negCons = ((DecisionConstraint) constraint).negate();
              if (negCons != null) {
                if (expl.isValid(timeStamp)) {
                  expl.clear();
                  try {
                    if (Logger.getLogger("choco").isLoggable(Level.FINE))
                      Logger.getLogger("choco").fine("Negation posted and propagated.");
                    ((PalmProblem) this.problem).post(negCons, expl);
                    this.problem.propagate();
                  } catch (PalmContradiction e) {
                    this.repair();
                  }
                }
              }
            }
          } else {
            if (Logger.getLogger("choco").isLoggable(Level.INFO))
              Logger.getLogger("choco").info("Contradiction because of: " + expl);
            ((PalmProblem) problem).setContradictionExplanation((PalmExplanation) expl.copy());
            pe.raiseSystemContradiction();
          }
        } else {
          if (Logger.getLogger("choco").isLoggable(Level.INFO))
            Logger.getLogger("choco").info("Contradiction because of: " + expl);
          ((PalmProblem) problem).setContradictionExplanation((PalmExplanation) expl.copy());
          pe.raiseSystemContradiction();
        }
      }
    }
  }


  /**
   * Gets the PalmState tool.
   */

  public PalmState getState() {
    return state;
  }


  /**
   * Gets the PalmLearn tool.
   */

  public PalmLearn getLearning() {
    return learning;
  }

  /**
   * Gets the PalmExtend tool.
   */

  public PalmExtend getExtending() {
    return extending;
  }

  public int getTimeLimit() {
    return ((AbstractGlobalSearchLimit) limits.get(LIMIT_TIME)).getNbMax();
  }

  public void setTimeLimit(int timeLimit) {
    ((AbstractGlobalSearchLimit) limits.get(LIMIT_TIME)).setNbMax(timeLimit);
  }

  public int getNodeLimit() {
    return ((AbstractGlobalSearchLimit) limits.get(LIMIT_NODES)).getNbMax();
  }

  public void setNodeLimit(int nodeLimit) {
    ((AbstractGlobalSearchLimit) limits.get(LIMIT_NODES)).setNbMax(nodeLimit);
  }

  public GlobalSearchLimit getLimit(int i) {
    return (GlobalSearchLimit) limits.get(i);
  }

  public int getNbLimit() {
    return limits.size();
  }
}
