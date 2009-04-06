package i_want_to_use_this_old_version_of_choco.real.exp;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealMath;

/**
 * An expression modelling a real addition.
 */
public class RealPlus extends AbstractRealBinTerm {
  /**
   * Builds an addition expression for real constraint modelling.
   * @param pb is the current problem
   * @param exp1 is the first expression operand
   * @param exp2 is the second expression operand
   */
  public RealPlus(final AbstractProblem pb, final RealExp exp1, 
      final RealExp exp2) {
    super(pb, exp1, exp2);
  }

  public String pretty() {
    return exp1.pretty() + " + " + exp2.pretty();
  }

  /**
   * Tightens the expression to find the smallest interval containing values
   * the expression can equal according to operand domains.
   */
  public void tighten() {
    RealInterval res = RealMath.add(exp1, exp2);
    inf.set(res.getInf());
    sup.set(res.getSup());
  }

  /**
   * Projects domain reduction on operands according to the expression
   * domain itself (due to constraint restrictions).
   * @throws ContradictionException if a domain becomes empty
   */
  public void project() throws ContradictionException {
    exp1.intersect(RealMath.sub(this, exp2));
    exp2.intersect(RealMath.sub(this, exp1));
  }
}
