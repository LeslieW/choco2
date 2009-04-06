package i_want_to_use_this_old_version_of_choco.real.var;

import i_want_to_use_this_old_version_of_choco.AbstractEntity;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateFloat;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

/**
 * An implmentation of real variable domains using two stored floats for storing bounds.
 */
public class RealDomainImpl extends AbstractEntity implements RealDomain {

  //public double width_zero = 1.e-8;
  //public double reduction_factor = 0.99;

  /**
   * for the delta domain: current value of the inf (domain lower bound) when the bound started beeing propagated
   * (just to check that it does not change during the propagation phase)
   */
  protected double currentInfPropagated = Double.NEGATIVE_INFINITY;

  /**
   * for the delta domain: current value of the sup (domain upper bound) when the bound started beeing propagated
   * (just to check that it does not change during the propagation phase)
   */
  protected double currentSupPropagated = Double.POSITIVE_INFINITY;

  protected IStateFloat inf;

  protected IStateFloat sup;

  protected RealVar variable;

  public RealDomainImpl(RealVar v, double a, double b) {
    variable = v;
    problem = v.getProblem();
    IEnvironment env = problem.getEnvironment();
    inf = env.makeFloat(a);
    sup = env.makeFloat(b);
  }

  public String toString() {
    return (super.toString() + ": [" +this.getInf() +", "+this.getSup()+"]");
  }

  public String pretty() {
    return (this.toString());
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
    if ((interval.getInf() > this.getSup()) || (interval.getSup() < this.getInf())) {
      throw new ContradictionException(this);
    }

    double old_width = this.getSup() - this.getInf();
    double new_width = Math.min(interval.getSup(), this.getSup()) -
        Math.max(interval.getInf(), this.getInf());
    boolean toAwake = (variable.getProblem().getPrecision() / 100. <= old_width)
        && (new_width < old_width * variable.getProblem().getReduction());

    if (interval.getInf() > this.getInf()) {
      if (toAwake) problem.getPropagationEngine().postUpdateInf(variable, index);
      inf.set(interval.getInf());
    }

    if (interval.getSup() < this.getSup()) {
      if (toAwake) problem.getPropagationEngine().postUpdateSup(variable, index);
      sup.set(interval.getSup());
    }
  }

  public void clearDeltaDomain() {
    currentInfPropagated = Double.NEGATIVE_INFINITY;
    currentSupPropagated = Double.POSITIVE_INFINITY;
  }

  public boolean releaseDeltaDomain() {
    boolean noNewUpdate = ((getInf() == currentInfPropagated) && (getSup() == currentSupPropagated));
    currentInfPropagated = Double.NEGATIVE_INFINITY;
    currentSupPropagated = Double.POSITIVE_INFINITY;
    return noNewUpdate;
  }

  public void freezeDeltaDomain() {
    currentInfPropagated = getInf();
    currentSupPropagated = getSup();
  }

  public boolean getReleasedDeltaDomain() {
    return true;
  }

  public void silentlyAssign(RealInterval i) {
    inf.set(i.getInf());
    sup.set(i.getSup());
  }
}
