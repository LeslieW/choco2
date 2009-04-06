package i_want_to_use_this_old_version_of_choco.real.exp;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealMath;

/**
 * An expression modelling a substraction.
 */
public class RealMinus extends AbstractRealBinTerm {
  public RealMinus(AbstractProblem pb, RealExp exp1, RealExp exp2) {
    super(pb, exp1, exp2);
  }

   public String pretty() {
    return exp1.pretty() + " - " + exp2.pretty();
  }

  public void tighten() {
    RealInterval res = RealMath.sub(exp1, exp2);
    inf.set(res.getInf());
    sup.set(res.getSup());
  }

  public void project() throws ContradictionException {
    exp1.intersect(RealMath.add(this, exp2));
    exp2.intersect(RealMath.sub(exp1, this));
  }
}
