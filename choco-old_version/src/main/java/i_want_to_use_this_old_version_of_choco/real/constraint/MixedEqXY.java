package i_want_to_use_this_old_version_of_choco.real.constraint;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.exp.RealIntervalConstant;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

public class MixedEqXY extends AbstractBinRealIntConstraint implements MixedConstraint {

  public MixedEqXY(RealVar v0, IntDomainVar v1) {
    super(v0, v1);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
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

  public boolean isConsistent() {
    return v1.getInf() <= v0.getSup() && v0.getInf() <= v1.getSup();
  }

  public boolean isSatisfied() {
    return isConsistent();
  }

  public void propagate() throws ContradictionException {
    if (v0.getInf() > v1.getInf()) {
      updateIInf();
    }
    if (v0.getSup() < v1.getSup()) {
      updateISup();
    }
    updateReal();
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx == 0) {
      if (v0.getInf() > v1.getInf()) {
        updateIInf();
        updateReal();
      }
    } else if (idx == 1) {
      updateReal();
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx == 0) {
      if (v0.getSup() < v1.getSup()) {
        updateISup();
        updateReal();
      }
    } else if (idx == 1) {
      updateReal();
    }
  }

  public void awakeOnInst(int varIdx) throws ContradictionException {
    if (varIdx == 1) {
      updateReal();
    }
  }

  public void awakeOnRem(int varIdx, int val) throws ContradictionException {
  }

  public void awakeOnRemovals(int varIdx, IntIterator deltaDomain) throws ContradictionException {
  }

  public void awakeOnBounds(int varIdx) throws ContradictionException {
    if (varIdx == 0) {
      if (v0.getInf() > v1.getInf()) {
        updateIInf();
      }
      if (v0.getSup() < v1.getSup()) {
        updateISup();
      }
      updateReal();
    } else if (varIdx == 1) {
      updateReal();
    }
  }

  protected void updateIInf() throws ContradictionException {
    v1.updateInf((int) Math.ceil(v0.getInf()), cIdx1);
  }

  protected void updateISup() throws ContradictionException {
    v1.updateSup((int) Math.floor(v0.getSup()), cIdx1);
  }

  protected void updateReal() throws ContradictionException {
    v0.intersect(new RealIntervalConstant(v1.getInf(), v1.getSup()), cIdx0);
  }
}
