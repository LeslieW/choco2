package i_want_to_use_this_old_version_of_choco.real.exp;

import i_want_to_use_this_old_version_of_choco.AbstractEntity;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateFloat;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;

/**
 * A compound expression depending on other terms.
 */
public abstract class AbstractRealCompoundTerm extends AbstractEntity implements RealExp {
  protected IStateFloat inf;
  protected IStateFloat sup;

  public AbstractRealCompoundTerm(AbstractProblem pb) {
    super(pb);
    IEnvironment env = pb.getEnvironment();
    inf = env.makeFloat(Double.NEGATIVE_INFINITY);
    sup = env.makeFloat(Double.POSITIVE_INFINITY);
  }

  public String toString() {
    return "[" + inf.get() + "," + sup.get() + "]";
  }

  public double getInf() {
    return inf.get();
  }

  public double getSup() {
    return sup.get();
  }

  public void intersect(RealInterval interval) throws ContradictionException {
    intersect(interval, VarEvent.NOCAUSE);
  }

  public void intersect(RealInterval interval, int index) throws ContradictionException {
    if (interval.getInf() > inf.get()) inf.set(interval.getInf());
    if (interval.getSup() < sup.get()) sup.set(interval.getSup());
    if (inf.get() > sup.get()) {
      problem.getPropagationEngine().raiseContradiction();
    }
  }
}
