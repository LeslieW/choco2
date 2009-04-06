package i_want_to_use_this_old_version_of_choco.real.var;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealMath;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.exp.RealIntervalConstant;

import java.util.List;
import java.util.Set;
/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */
/**
 * An implementation of real variables using RealDomain domains.
 */
public class RealVarImpl extends AbstractVar implements RealVar {
  protected RealDomain domain;

  public RealVarImpl(AbstractProblem pb, String name, double a, double b) {
    super(pb, name);
    this.domain = new RealDomainImpl(this, a, b);
    this.event = new RealVarEvent(this);
  }

  public String toString() {
    return this.name + "[" + this.getInf() + "," + this.getSup() + "]";
  }

  public String pretty() {
    return this.toString();
  }

  public RealInterval getValue() {
    return new RealIntervalConstant(getInf(), getSup());
  }

  public RealDomain getDomain() {
    return domain;
  }

  public void silentlyAssign(RealInterval i) {
    domain.silentlyAssign(i);
  }

  public double getInf() {
    return domain.getInf();
  }

  public double getSup() {
    return domain.getSup();
  }

  public void intersect(RealInterval interval) throws ContradictionException {
    this.domain.intersect(interval);
  }

  public void intersect(RealInterval interval, int index) throws ContradictionException {
    this.domain.intersect(interval, index);
  }

    /**
     * CPRU 07/12/2007: DomOverFailureDeg implementation
     * Add:
     * - call of super.fail()
     * - call of raiseContradiction(this)
     * - comment fail() initial
     *
     * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
     */
    public void fail() throws ContradictionException {
        super.fail();
        problem.getPropagationEngine().raiseContradiction(this);
        //fail();
  }

  public boolean isInstantiated() {
    return RealMath.isCanonical(this, this.problem.getPrecision());
  }

  public void tighten() {
  }

  public void project() {
  }

  public List subExps(List l) {
    l.add(this);
    return l;
  }

  public Set collectVars(Set s) {
    s.add(this);
    return s;
  }

  public boolean isolate(RealVar var, List wx, List wox) {
      return this == var;
  }
}
