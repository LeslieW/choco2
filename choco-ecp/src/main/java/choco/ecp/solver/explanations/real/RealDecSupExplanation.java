//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.explanations.real;

import choco.ecp.solver.variables.real.PalmRealVar;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.BitSet;

/**
 * Implements an explanation for upper bound decrease.
 */
public class RealDecSupExplanation extends AbstractRealBoundExplanation {
  /**
   * Creates such an explanation with all constraints contained in the explanation parameter, with
   * the specified previous value of the bound, and for the specified touched variable.
   */
  public RealDecSupExplanation(Solver pb, BitSet explanation, double previousValue, PalmRealVar variable) {
    super(pb);
    this.explanation = explanation;
    this.previousValue = previousValue;
    this.variable = variable;
  }

  /**
   * Creates a string representing this explanation.
   */
  public String toString() {
    return this.variable + ".sup << " + this.previousValue + " because " + super.toString();
  }

  /**
   * If a constraint contained in this explanation is removed, this method cancels past effects of this
   * constraint.
   *
   * @param removed
   */
  public void postUndoRemoval(SConstraint removed) {
    this.removeDependencies(removed);
    this.variable.restoreSup(this.previousValue);
  }
}
