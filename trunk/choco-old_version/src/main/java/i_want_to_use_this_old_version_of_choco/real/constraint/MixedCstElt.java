package i_want_to_use_this_old_version_of_choco.real.constraint;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.exp.RealIntervalConstant;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * Let x be an integer variable with n values and v be a real variable. Given n constant values a1 to an,
 * this constraint ensures that:
 * <p/>
 * <code>x = i iff v = ai</code>
 * <p/>
 * a1... an sequence is supposed to be ordered (a1&lt;a2&lt;... an)
 */
public class MixedCstElt extends AbstractBinRealIntConstraint implements MixedConstraint {
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

  public void awakeOnRemovals(int varIdx, IntIterator deltaDomain) throws ContradictionException {
  }

  public void updateIInf() throws ContradictionException {
    int inf = v1.getInf();
    while (values[inf] < v0.getInf()) {
      inf++;
    }
    if (inf > v1.getSup()) problem.getPropagationEngine().raiseContradiction();
    v1.updateInf(inf, cIdx1);
  }

  public void updateISup() throws ContradictionException {
    int sup = v1.getSup();
    while (values[sup] > v0.getSup()) {
      sup--;
    }
    if (sup < v1.getInf()) problem.getPropagationEngine().raiseContradiction();
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

  public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
    int j = i;
    for (int k = 0; k < getNbVars(); k++) {
      j++;
      int cidx = root.connectVar(getVar(k), j, dynamicAddition);
      setConstraintIndex(k, cidx);
    }
    return j;
  }

}
