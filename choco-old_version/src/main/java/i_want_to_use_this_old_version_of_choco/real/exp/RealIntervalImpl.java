package i_want_to_use_this_old_version_of_choco.real.exp;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;

/**
 * @deprecated
 */
public class RealIntervalImpl implements RealInterval {
  protected double inf;
  protected double sup;

  public RealIntervalImpl(double inf, double sup) {
    this.inf = inf;
    this.sup = sup;
  }

  public RealIntervalImpl() {
    this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
  }

  public RealIntervalImpl(RealInterval i) {
    this(i.getInf(), i.getSup());
  }

  public String toString() {
    return "[" + inf + "," + sup + "]";
  }

  public String pretty() {
    return this.toString();
  }

  public double getInf() {
    return inf;
  }

  public double getSup() {
    return sup;
  }

  public void intersect(RealInterval interval) throws ContradictionException {
    intersect(interval, VarEvent.NOCAUSE);
  }

  public void intersect(RealInterval interval, int index) throws ContradictionException {
    if (interval.getInf() > inf) inf = interval.getInf();
    if (interval.getSup() < sup) sup = interval.getSup();
  }
}
