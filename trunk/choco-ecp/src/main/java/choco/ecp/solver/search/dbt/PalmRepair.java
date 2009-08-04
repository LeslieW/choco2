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

import choco.ecp.solver.explanations.dbt.BetterConstraintComparator;
import choco.ecp.solver.explanations.dbt.PalmExplanation;
import choco.kernel.solver.constraints.SConstraint;

import java.util.Collections;

/**
 * A repairing algorithm.
 */

public class PalmRepair extends PalmAbstractSolverTool {

  /**
   * Selects a decision to undo for repairing a contradiction. In this default implementation, it
   * selects the minimal constraints in the provided explain w.r.t. the BetterConstraintComparator
   * order.
   *
   * @param expl The explain of the contradiction.
   */

  public SConstraint selectDecisionToUndo(PalmExplanation expl) {
    return (SConstraint) Collections.min(expl.toSet(), new BetterConstraintComparator());
  }
}
