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

import choco.ecp.solver.variables.integer.dbt.PalmIntVar;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.BitSet;

public class IncInfExplanation extends AbstractBoundExplanation {
  public IncInfExplanation(Solver pb, BitSet explanation, int previousValue, PalmIntVar variable) {
    super(pb);
    this.explanation = explanation;
    this.previousValue = previousValue;
    this.variable = variable;
  }

  public String toString() {
    return this.variable + ".inf > " + this.previousValue + " because " + super.toString();
  }

  public void postUndoRemoval(SConstraint removed) {
    this.removeDependencies(removed);
    this.variable.restoreInf(this.previousValue);
  }
}
