//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm;

import i_want_to_use_this_old_version_of_choco.*;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.dbt.PalmSolver;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.GenericExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;
import i_want_to_use_this_old_version_of_choco.palm.dbt.prop.PalmEngine;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.PalmContradiction;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.PalmGlobalSearchSolver;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealVarImpl;
import i_want_to_use_this_old_version_of_choco.prop.ChocEngine;
import i_want_to_use_this_old_version_of_choco.prop.ConstraintEvent;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

import java.util.Iterator;
import java.util.List;

/**
 * Choco problem extension involving explanations and explanation-based algorithms (mac-dbt, decision-repair...)
 */

public class PalmProblem extends JumpProblem implements ExplainedProblem {

  /**
   * Displays release information (date, verions, ...).
   */

  public final static void ReleasePalmDisplay() {
    logger.info("** JPaLM : Constraint Programming with Explanations");
    logger.info("** JPaLM v0.9b (July, 2004), Copyright (c) 2000-2004 N. Jussien");
    displayRelease = false;
  }


  /**
   * Creates a Palm Problem with the specified environment.
   */
  public PalmProblem() {
    super();

    // Ensures a determinist behaviour
    GenericExplanation.reinitTimestamp();

    // Specialized engine and solver for Palm
    this.propagationEngine = new PalmEngine(this);
    this.solver = new PalmSolver(this);

    // Displays information about Palm
    if (displayRelease) ReleasePalmDisplay();
  }

  /**
   * Factory to create explanation.
   * It offers the possibility to make another kind of explanation, only by extending PalmProblem
   *
   * @return the new explanation object
   */
  public Explanation makeExplanation() {
    return new GenericExplanation(this);
  }

  public ExplainedConstraintPlugin makeConstraintPlugin(AbstractConstraint ct) {
    return new PalmConstraintPlugin(ct);
  }

  public void explainedFail(Explanation exp) throws ContradictionException {
    ((PalmEngine) this.getPropagationEngine()).raisePalmFakeContradiction((PalmExplanation) exp);
  }

  /**
   * Searches one solution of the problem.
   *
   * @return True if a solution was found.
   * @deprecated
   */
  public Boolean searchOneSolution() {
    solver.getSearchSolver().incrementalRun();
    return this.isFeasible();
  }


  /**
   * Tries to search the problem by finding one solution or all solutions.
   *
   * @param allSolutions If true, all the solutions are searched.
   * @deprecated
   */
  public Boolean solve(boolean allSolutions) {
    solver.setFirstSolution(!allSolutions);
    solver.generateSearchSolver(this);
    this.feasible = Boolean.FALSE;
    try {
      solver.getSearchSolver().newTreeSearch();
    } catch (ContradictionException e) {
      logger.severe("Should not happen : PalmProblem.solve(boolean)");
    }
    if (allSolutions) {
      boolean soluble = (this.searchOneSolution() == Boolean.TRUE);
      while (soluble) {
        logger.info("A solution was found.");
        soluble = ((PalmGlobalSearchSolver) solver.getSearchSolver()).getState().discardCurrentSolution()
            && (this.searchOneSolution() == Boolean.TRUE);
      }
    } else
      return this.searchOneSolution();
    solver.getSearchSolver().endTreeSearch();
    return Boolean.TRUE;
  }

  /**
   * Tries to search the <i>first</i> solution of the problem.
   */
  public Boolean solve() {
    solver.generateSearchSolver(this);
    this.feasible = Boolean.FALSE;
    try {
      solver.getSearchSolver().newTreeSearch();
    } catch (ContradictionException e) {
      logger.severe("Should not happen : PalmProblem.solve(boolean)");
    }
    solver.getSearchSolver().incrementalRun();
    //if (this.isFeasible() == Boolean.FALSE) {
    solver.getSearchSolver().endTreeSearch();
    //}
    return this.isFeasible();
  }

  /**
   * Tries to find another solution.
   */
  public Boolean nextSolution() {
    if (((PalmGlobalSearchSolver) solver.getSearchSolver()).getState().discardCurrentSolution()) {
      solver.getSearchSolver().incrementalRun();
    } else {
      solver.getSearchSolver().endTreeSearch();
      return Boolean.FALSE;
    }
    //if (this.isFeasible() == Boolean.FALSE) {
    solver.getSearchSolver().endTreeSearch();
    //}
    return this.isFeasible();
  }

  /**
   * Checks if current solution is still valid.
   */
  public boolean checkSolution() {
    try {
      this.propagate();
      return true;
    } catch (PalmContradiction e) {
      return false;
    } catch (ContradictionException e) {
      logger.severe("This should not happen: PalmProblem.checkSolution()");
      return false;
    }
  }

  /**
   * Tries to find all solutions of the problem.
   */
  public Boolean solveAll() {
    solver.generateSearchSolver(this);
    this.feasible = Boolean.FALSE;
    try {
      solver.getSearchSolver().newTreeSearch();
    } catch (ContradictionException e) {
      logger.severe("Should not happen : PalmProblem.solve(boolean)");
    }
    solver.getSearchSolver().incrementalRun();
    boolean soluble = (this.isFeasible() == Boolean.TRUE);
    while (soluble) {
      logger.info("A solution was found.");
      soluble = ((PalmGlobalSearchSolver) solver.getSearchSolver()).getState().discardCurrentSolution();
      if (soluble) {
        solver.getSearchSolver().incrementalRun();
        soluble = (this.isFeasible() == Boolean.TRUE);
      }
    }
    solver.getSearchSolver().endTreeSearch();
    return isFeasible();
  }

  /**
   * Maximize an objective variable with a PalmBranchAndBound
   */
  public Boolean maximize(Var objective, boolean restart) {
    ((PalmSolver) solver).setObjective(objective);
    ((PalmSolver) solver).setDoMaximize(true);
    solver.generateSearchSolver(this);
    solver.getSearchSolver().incrementalRun();
    return this.isFeasible();
  }


  /**
   * Minimize an objective variable with a PalmBranchAndBound
   */
  public Boolean minimize(Var objective, boolean restart) {
    ((PalmSolver) solver).setObjective(objective);
    ((PalmSolver) solver).setDoMaximize(false);
    solver.generateSearchSolver(this);
    solver.getSearchSolver().incrementalRun();
    return this.isFeasible();
  }

  /**
   * Posts a constraints in the problem. If it has ever been posted (but deactivated), it is
   * only reactivated and repropagated.
   *
   * @param constraint The constraint to post.
   */
  public void post(Constraint constraint) {
    if (constraint instanceof PalmConstraint) {
      PalmConstraint pconstraint = (PalmConstraint) constraint;
      PalmConstraintPlugin pi = (PalmConstraintPlugin) pconstraint.getPlugIn();
      if (!(pi.isEverConnected())) {
        int idx;
        constraints.staticAdd(constraint);
        idx = this.constraints.size() - 1;
        pi.setConstraintIdx(idx);
        pconstraint.addListener(false);
        ConstraintEvent event = (ConstraintEvent) pconstraint.getEvent();
        propagationEngine.registerEvent(event);
        propagationEngine.postConstAwake(pconstraint, true);
      } else {
        logger.fine("The constraint " + constraint + " is reactivated.");
        ((ChocEngine) this.propagationEngine).postConstAwake(pconstraint, true);
        pconstraint.setActive();
      }
      if (pi.isDepending())
        pi.setDependance();
    } else {
      throw new Error("Impossible to post non-Palm constraints to a Palm problem");
    }
  }

  public void postCut(Constraint constraint) {
    throw new UnsupportedOperationException();
  }

  /**
   * Posts a constraint with the specified weight.
   *
   * @param constraint The constraint to post.
   * @param w          The weight associated to the constraint.
   */
  public void post(Constraint constraint, int w) {
    this.post(constraint);
    ((PalmConstraintPlugin) ((Propagator) constraint).getPlugIn()).setWeight(w);
  }


  /**
   * Posts an indirect constraint with an explain.
   *
   * @param constraint The constraint to post.
   * @param expl       The set of constraint this posted constraint depends on.
   */
  public void post(Constraint constraint, PalmExplanation expl) {
    ((PalmConstraintPlugin) ((Propagator) constraint).getPlugIn()).setIndirect(expl);
    this.post(constraint);
  }

  /**
   * Posts and propagates several decision constraints (that is decisions taken by the solver).
   *
   * @param constraints The constraints to post.
   * @throws ContradictionException
   */
  public void propagateAllDecisionsConstraints(List constraints) throws ContradictionException {
    //this.palmSolver.incRuntimeStatistic(EXT, 1);  move in explore
    for (Iterator iterator = constraints.iterator(); iterator.hasNext();) {
      AbstractConstraint constraint = (AbstractConstraint) iterator.next();
      this.post(constraint, 0); // Avant la mise a jour de l'etat sinon la contrainte n'existe pas encore !!
      ((PalmGlobalSearchSolver) solver.getSearchSolver()).getState().addDecision(constraint);
      this.propagate();
    }
  }


  /**
   * Tries to repair the problem after a PalmContradiction thanks to removing a responsible
   * constraint (that is a constraint in the explain of the contradiction).
   *
   * @throws ContradictionException
   */
  public void repair() throws ContradictionException {
    ((PalmGlobalSearchSolver) solver.getSearchSolver()).repair();
  }


  /**
   * Removes properly a constraint from the problem: the constraint is deactivated and all the depending
   * filtering decisions are undone.
   *
   * @param constraint The constraint to remove.
   */
  public void remove(Propagator constraint) {
    ((PalmEngine) this.propagationEngine).remove(constraint);
    if (((PalmConstraintPlugin) constraint.getPlugIn()).isEphemeral()) {
      //System.out.println("+Remove : " + ((PalmConstraintPlugin)constraint.getPlugIn()).getConstraintIdx());
      //this.eraseConstraint(((PalmConstraintPlugin)constraint.getPlugIn()).getConstraintIdx());
      constraint.delete();
    }
  }


  /**
   * Returns the maximum level the solver can relax without user interaction.
   */
  public int getMaxRelaxLevel() {
    return maxRelaxLevel;
  }

  /**
   * Sets the maximum level the solver can relax without user interaction (default value is 0, that is only
   * decision constraints).
   *
   * @param maxRelaxLevel the new level
   */
  public void setMaxRelaxLevel(int maxRelaxLevel) {
    this.maxRelaxLevel = maxRelaxLevel;
  }

  protected RealVar createRealVal(String name, double min, double max) {
    return new PalmRealVarImpl(this, name, min, max);
  }

  protected IntDomainVar createIntVar(String name, int domainType, int min, int max) {
    return new PalmIntVar(this, name, domainType, min, max);
  }

  protected IntDomainVar createIntVar(String name, int[] sortedValues) {
    return new PalmIntVar(this, name, sortedValues);
  }
}
