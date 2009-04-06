//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real.exp;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealInterval;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealMath;
import i_want_to_use_this_old_version_of_choco.real.RealExp;

/**
 * Implementation of a real addition expression.
 */
public class PalmRealPlus extends AbstractPalmRealBinTerm {
  /**
   * Creates the addition of two sub expressions.
   */
  public PalmRealPlus(AbstractProblem pb, RealExp exp1, RealExp exp2) {
    super(pb, exp1, exp2);
  }

  /**
   * Tightens the value of the expressions, that is affects values with respect to the values
   * (and explanations) of sub expressions.
   */
  public void tighten() {
    PalmRealInterval res = PalmRealMath.add((PalmProblem) this.getProblem(), exp1, exp2);
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
    exp1.intersect(PalmRealMath.sub((PalmProblem) this.getProblem(), this, exp2));
    exp2.intersect(PalmRealMath.sub((PalmProblem) this.getProblem(), this, exp1));
  }
}
