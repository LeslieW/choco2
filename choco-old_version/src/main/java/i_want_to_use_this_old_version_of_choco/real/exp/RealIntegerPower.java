package i_want_to_use_this_old_version_of_choco.real.exp;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealMath;

public class RealIntegerPower extends AbstractRealUnTerm {
  protected int power;

  public RealIntegerPower(AbstractProblem pb, RealExp exp1, int power) {
    super(pb, exp1);
    this.power = power;
  }

   public String pretty() {
    return exp1.pretty() + "^" + power;
  }

  public void tighten() {
    RealInterval res = RealMath.iPower(exp1, power);
    inf.set(res.getInf());
    sup.set(res.getSup());
  }

  public void project() throws ContradictionException {
    RealInterval res = RealMath.iRoot(this, power, exp1);
    if (res.getInf() > res.getSup()) {
      problem.getPropagationEngine().raiseContradiction();
    }
    exp1.intersect(res);
  }
}
