package i_want_to_use_this_old_version_of_choco.real.exp;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealMath;

/**
 * An expression modelling a multiplication.
 */
public class RealMult extends AbstractRealBinTerm {
  public RealMult(AbstractProblem pb, RealExp exp1, RealExp exp2) {
    super(pb, exp1, exp2);
  }

  public String pretty() {
    return exp1.pretty() + " * " + exp2.pretty();
  }

  public void tighten() {
    RealInterval res = RealMath.mul(exp1, exp2);
    inf.set(res.getInf());
    sup.set(res.getSup());
  }

  public void project() throws ContradictionException {
    RealInterval res = RealMath.odiv_wrt(this, exp2, exp1);
    if (res.getInf() > res.getSup()) {
      problem.getPropagationEngine().raiseContradiction();
    }
    exp1.intersect(res);

    res = RealMath.odiv_wrt(this, exp1, exp2);
    if (res.getInf() > res.getSup()) {
      problem.getPropagationEngine().raiseContradiction();
    }
    exp2.intersect(res);
  }
}
