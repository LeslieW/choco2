//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, 
//                                   Guillaume Rochart...

package choco.ecp.solver.constraints.real.exp;

import choco.ecp.solver.PalmSolver;
import choco.ecp.solver.variables.real.PalmRealInterval;
import choco.ecp.solver.variables.real.PalmRealMath;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.real.RealExp;

/**
 * Implementation of a explained real multiplication expression.
 */
public class PalmRealMult extends AbstractPalmRealBinTerm {
  /**
   * Creates the product of two sub expressions.
   * @param pb the problem of the constraint
   * @param exp1 the first expression operand
   * @param exp2 the second expression operand
   */
  public PalmRealMult(final Solver pb, final RealExp exp1,
      final RealExp exp2) {
    super(pb, exp1, exp2);
  }

  /**
   * Tightens the value of the expressions, 
   * that is affects values with respect to the values
   * (and explanations) of sub expressions.
   */
  public void tighten() {
    PalmRealInterval res = PalmRealMath.mul(
        (PalmSolver) this.getSolver(), exp1, exp2);
    inf.set(res.getInf());
    sup.set(res.getSup());
    explanationOnInf.empties();
    res.self_explain(INF, explanationOnInf);
    explanationOnSup.empties();
    res.self_explain(SUP, explanationOnSup);
  }

  /**
   * Projects current value on sub-expressions.
   * @throws ContradictionException if a domain becomes empty or if a
   * contradiction is infered
   */
  public void project() throws ContradictionException {
    PalmRealInterval res =
        PalmRealMath.odiv_wrt((PalmSolver)
        this.getSolver(), this, exp2, exp1);
    if (res.getInf() > res.getSup()) {
      solver.getPropagationEngine().raiseContradiction();
    }
    exp1.intersect(res);

    res = PalmRealMath.odiv_wrt(
        (PalmSolver) this.getSolver(), this, exp1, exp2);
    if (res.getInf() > res.getSup()) {      
      solver.getPropagationEngine().raiseContradiction();
    }
    exp2.intersect(res);
  }
}
