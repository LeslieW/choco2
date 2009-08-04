//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.explanations.integer;

import choco.ecp.solver.explanations.dbt.GenericExplanation;
import choco.ecp.solver.variables.integer.dbt.PalmIntVar;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.BitSet;

public class RemovalExplanation extends GenericExplanation implements IRemovalExplanation {
  protected int value;
  protected PalmIntVar variable;

  public RemovalExplanation(Solver pb, BitSet explanation, int value, PalmIntVar variable) {
    super(pb);
    this.value = value;
    this.explanation = explanation;
    this.variable = variable;
  }

  public String toString() {
    return this.variable + " != " + this.value + " because " + super.toString();
  }

  public void postUndoRemoval(SConstraint removed) {
    this.removeDependencies(removed);
    this.variable.restoreVal(this.value);
    this.variable.resetExplanationOnVal(value);
  }
}
