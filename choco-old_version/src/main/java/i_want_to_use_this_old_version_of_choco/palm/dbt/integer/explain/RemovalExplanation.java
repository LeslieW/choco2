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
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.GenericExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;

import java.util.BitSet;

public class RemovalExplanation extends GenericExplanation implements IRemovalExplanation {
  protected int value;
  protected PalmIntVar variable;

  public RemovalExplanation(AbstractProblem pb, BitSet explanation, int value, PalmIntVar variable) {
    super(pb);
    this.value = value;
    this.explanation = explanation;
    this.variable = variable;
  }

  public String toString() {
    return this.variable + " != " + this.value + " because " + super.toString();
  }

  public void postUndoRemoval(Constraint removed) {
    this.removeDependencies(removed);
    this.variable.restoreVal(this.value);
    this.variable.resetExplanationOnVal(value);
  }
}
