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

import choco.ecp.solver.constraints.PalmSConstraint;
import choco.ecp.solver.search.SymbolicDecision;
import choco.kernel.solver.constraints.SConstraint;

/**
 * An enumeration constraint like instantiation.
 */

public interface DecisionSConstraint extends PalmSConstraint, SymbolicDecision {

  /**
   * Returns the negation of this enumeration constraint.
   */

  public SConstraint negate();


  /**
   * Returns the number identifying the current branch.
   */

  public int getBranch();
}
