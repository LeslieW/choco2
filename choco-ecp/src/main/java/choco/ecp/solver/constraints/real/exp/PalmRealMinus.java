//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.constraints.real.exp;

import choco.ecp.solver.PalmSolver;
import choco.ecp.solver.variables.real.PalmRealInterval;
import choco.ecp.solver.variables.real.PalmRealMath;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.real.RealExp;

/**
 * Implementation of a real substraction expression.
 */
public class PalmRealMinus extends AbstractPalmRealBinTerm {
  /**
   * Creates the substraction between two sub expressions.
   */
  public PalmRealMinus(Solver pb, RealExp exp1, RealExp exp2) {
    super(pb, exp1, exp2);
  }

  /**
   * Tightens the value of the expressions, that is affects values with respect to the values
   * (and explanations) of sub expressions.
   */
  public void tighten() {
    PalmRealInterval res = PalmRealMath.sub((PalmSolver) this.getSolver(), exp1, exp2);
    inf.set(res.getInf());
    sup.set(res.getSup());
    explanationOnInf.empties();
    res.self_explain(INF, explanationOnInf);
    explanationOnSup.empties();
    res.self_explain(SUP, explanationOnSup);
  }

  /**
   * Projects current value on sub-expressions.
   *
   * @throws ContradictionException
   */
  public void project() throws ContradictionException {
    exp1.intersect(PalmRealMath.add((PalmSolver) this.getSolver(), this, exp2));
    exp2.intersect(PalmRealMath.sub((PalmSolver) this.getSolver(), exp1, this));
  }
}
