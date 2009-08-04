//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.search.dbt;


import choco.ecp.solver.explanations.dbt.PalmExplanation;
import choco.ecp.solver.propagation.dbt.PalmEngine;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.AbstractSConstraint;

import java.util.logging.Logger;

/**
 * Tool for maintaining the state of the search taht is the active posted decision constraint.
 */

public class PalmState extends PalmAbstractSolverTool {
  protected static final Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search");

  /**
   * The current state.
   */

  protected PalmExplanation path;

  public PalmExplanation getPath() {
    return path;
  }

  /**
   * Initializes the PalmState with the specified explain.
   *
   * @param expl The initial state.
   */

  public PalmState(PalmExplanation expl) {
    this.path = expl;
  }


  /**
   * Adds a new decision constraints in the state.
   *
   * @param constraint New decision constraint posted.
   */

  public void addDecision(AbstractSConstraint constraint) {
    this.path.add(constraint);
  }


  /**
   * Reverses a decision constraint. (The difference with removing is that it does not call the learning
   * tool).
   *
   * @param constraint The constraint to reverse.
   */

  public void reverseDecision(AbstractSConstraint constraint) {
    this.path.delete(constraint);
  }


  /**
   * Removes a decision constraint.
   *
   * @param constraint The involved constraint.
   */

  public void removeDecision(AbstractSConstraint constraint) {
    this.manager.getLearning().learnFromRemoval(constraint);
    this.path.delete(constraint);
  }


  /**
   * Discards the current solutions in order to find the next one : it raises a fake contradiction and tries
   * repairing the state.
   * @return
   */

  public boolean discardCurrentSolution() {
    try {
      this.manager.reset();
      try {
        ((PalmEngine) this.manager.getSolver().getPropagationEngine()).raisePalmFakeContradiction((PalmExplanation) this.path.copy());
      } catch (PalmContradiction e) {
        this.manager.repair();
      }
      return true;
    } catch (ContradictionException e) {
      return false;
    }
  }
}
