package i_want_to_use_this_old_version_of_choco.real.exp;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

import java.util.List;
import java.util.Set;

/**
 * A constant real interval.
 */
public class RealIntervalConstant implements RealExp {
  protected final double inf;
  protected final double sup;

  public RealIntervalConstant(RealInterval interval) {
    this.inf = interval.getInf();
    this.sup = interval.getSup();
  }

  public RealIntervalConstant(double inf, double sup) {
    this.inf = inf;
    this.sup = sup;
    //this.problem = pb;
  }

  public String toString() {
    return "[" + inf + "," + sup + "]";
  }

  public double getInf() {
    return inf;
  }

  public double getSup() {
    return sup;
  }

  public void intersect(RealInterval interval) throws ContradictionException {
  }

  public void intersect(RealInterval interval, int index) throws ContradictionException {
  }

  public void tighten() {
  }

  public void project() {
  }

  public String pretty() {
    return toString();
  }

  public List subExps(List l) {
    l.add(this);
    return l;
  }

  public Set collectVars(Set s) {
    return s;
  }

  public boolean isolate(RealVar var, List wx, List wox) {
    return false;
  }
}
