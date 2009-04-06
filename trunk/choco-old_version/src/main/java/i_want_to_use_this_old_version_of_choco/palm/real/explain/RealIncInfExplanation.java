//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real.explain;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealVar;

import java.util.BitSet;

/**
 * Implements an explanation for lower bound increase.
 */
public class RealIncInfExplanation extends AbstractRealBoundExplanation {
  /**
   * Creates such an explanation with all constraints contained in the explanation parameter, with
   * the specified previous value of the bound, and for the specified touched variable.
   */
  public RealIncInfExplanation(AbstractProblem pb, BitSet explanation, double previousValue, PalmRealVar variable) {
    super(pb);
    this.explanation = explanation;
    this.previousValue = previousValue;
    this.variable = variable;
  }

  /**
   * Creates a string representing this explanation.
   */
  public String toString() {
    return this.variable + ".inf > " + this.previousValue + " because " + super.toString();
  }

  /**
   * If a constraint contained in this explanation is removed, this method cancels past effects of this
   * constraint.
   *
   * @param removed
   */
  public void postUndoRemoval(Constraint removed) {
    this.removeDependencies(removed);
    this.variable.restoreInf(this.previousValue);
  }
}
