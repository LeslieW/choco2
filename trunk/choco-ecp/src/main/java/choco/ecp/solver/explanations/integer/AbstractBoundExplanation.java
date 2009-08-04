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

public abstract class AbstractBoundExplanation extends GenericExplanation implements IBoundExplanation {
  int previousValue;
  PalmIntVar variable;

  public AbstractBoundExplanation(Solver pb) {
    super(pb);
  }

  public int getPreviousValue() {
    return previousValue;
  }

  public PalmIntVar getVariable() {
    return variable;
  }
}
