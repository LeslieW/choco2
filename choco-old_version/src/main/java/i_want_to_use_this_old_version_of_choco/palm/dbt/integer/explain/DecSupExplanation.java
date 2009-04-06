//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.integer.explain;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;

import java.util.BitSet;

public class DecSupExplanation extends AbstractBoundExplanation {
  public DecSupExplanation(AbstractProblem pb, BitSet explanation, int previousValue, PalmIntVar variable) {
    super(pb);
    this.explanation = explanation;
    this.previousValue = previousValue;
    this.variable = variable;
  }

  public String toString() {
    return this.variable + ".sup < " + this.previousValue + " because " + super.toString();
  }

  public void postUndoRemoval(Constraint removed) {
    this.removeDependencies(removed);
    this.variable.restoreSup(this.previousValue);
  }
}
