//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.search;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.BetterConstraintComparator;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;

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

  public Constraint selectDecisionToUndo(PalmExplanation expl) {
    return (Constraint) Collections.min(expl.toSet(), new BetterConstraintComparator());
  }
}
