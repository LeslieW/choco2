/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.real;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import static choco.kernel.solver.ContradictionException.Type.CONSTRAINT;
import choco.kernel.solver.constraints.real.AbstractBinRealIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealIntervalConstant;
import choco.kernel.solver.variables.real.RealVar;

/**
 * Let x be an integer variable with n values and v be a real variable. Given n constant values a1 to an,
 * this constraint ensures that:
 * <p/>
 * <code>x = i iff v = ai</code>
 * <p/>
 * a1... an sequence is supposed to be ordered (a1&lt;a2&lt;... an)
 */
public class MixedCstElt extends AbstractBinRealIntSConstraint{
  protected double[] values;

  public MixedCstElt(RealVar v0, IntDomainVar v1, double[] values) {
    super(v0, v1);
    this.values = values;
  }

  public Object clone() throws CloneNotSupportedException {
    MixedCstElt newc = (MixedCstElt) super.clone();
    newc.values = new double[this.values.length];
    System.arraycopy(this.values, 0, newc.values, 0, this.values.length);
    return newc;
  }

  public void awake() throws ContradictionException {
    v1.updateSup(values.length - 1, cIdx1);
    v1.updateInf(0, cIdx1);
    this.propagate();
  }

  public void propagate() throws ContradictionException {
    updateIInf();
    updateISup();
    updateReal();
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx == 0) {
      updateIInf();
      updateReal();
    } else {
      updateReal();
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx == 0) {
      updateISup();
      updateReal();
    } else {
      updateReal();
    }
  }

  public void awakeOnBounds(int idx) throws ContradictionException {
    if (idx == 0) {
      updateIInf();
      updateISup();
      updateReal();
    } else {
      updateReal();
    }
  }

  public void awakeOnInst(int varIdx) throws ContradictionException {
    awakeOnBounds(varIdx);
  }

  public void awakeOnRem(int varIdx, int val) throws ContradictionException {
  }

  public void awakeOnRemovals(int varIdx, DisposableIntIterator deltaDomain) throws ContradictionException {
  }

  public void updateIInf() throws ContradictionException {
    int inf = v1.getInf();
    while (values[inf] < v0.getInf()) {
      inf++;
    }
    if (inf > v1.getSup()) this.solver.getPropagationEngine().raiseContradiction(this, CONSTRAINT);
    v1.updateInf(inf, cIdx1);
  }

  public void updateISup() throws ContradictionException {
    int sup = v1.getSup();
    while (values[sup] > v0.getSup()) {
      sup--;
    }
    if (sup < v1.getInf()) this.solver.getPropagationEngine().raiseContradiction(this, CONSTRAINT);
    v1.updateSup(sup, cIdx1);
  }

  public void updateReal() throws ContradictionException {
    v0.intersect(new RealIntervalConstant(values[v1.getInf()], values[v1.getSup()]));
  }

  public boolean isConsistent() {
    return values[v1.getInf()] <= v0.getSup() && v0.getInf() <= values[v1.getSup()];
  }

  public boolean isSatisfied() {
    return isConsistent();
  }

    public int getFineDegree(int idx) {
        return 1;
    }
}
