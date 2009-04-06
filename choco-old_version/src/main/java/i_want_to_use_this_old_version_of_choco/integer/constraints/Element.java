// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.integer.constraints;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.prop.VarEvent;
import i_want_to_use_this_old_version_of_choco.util.Arithm;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

public class Element extends AbstractBinIntConstraint {
  int[] lval;
  int cste;

  public Element(IntDomainVar index, int[] values, IntDomainVar var, int offset) {
    super(index, var);
    this.lval = values;
    this.cste = offset;
  }

  public Element(IntDomainVar index, int[] values, IntDomainVar var) {
    this(index, values, var, 0);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public String toString() {
    return "Element";
  }

  public String pretty() {
    return (this.v1.pretty() + " = nth(" + this.v0.pretty() + ", " + Arithm.pretty(this.lval) + ")");
  }

  protected void updateValueFromIndex() throws ContradictionException {
    int minVal = Integer.MAX_VALUE;
    int maxVal = Integer.MIN_VALUE;
    for (IntIterator iter = this.v0.getDomain().getIterator(); iter.hasNext();) {
      int index = iter.next();
      if (minVal > this.lval[index]) minVal = this.lval[index];
      if (maxVal < this.lval[index]) maxVal = this.lval[index];
    }
    this.v1.updateInf(minVal, this.cIdx1);
    this.v1.updateSup(maxVal, this.cIdx1);

     // todo : <hcambaza> : why it does not perform AC on the value variable ?
  }

  protected void updateIndexFromValue() throws ContradictionException {
    int minFeasibleIndex = Math.max(0 - cste, this.v0.getInf());
    int maxFeasibleIndex = Math.min(this.v0.getSup(), lval.length - 1 - cste);
    int cause = this.v1.hasEnumeratedDomain() ? this.cIdx0 : VarEvent.NOCAUSE;

    while ((this.v0.canBeInstantiatedTo(minFeasibleIndex))
        && !(this.v1.canBeInstantiatedTo(lval[minFeasibleIndex + this.cste])))
      minFeasibleIndex++;
    this.v0.updateInf(minFeasibleIndex, cause);

    while ((this.v0.canBeInstantiatedTo(maxFeasibleIndex))
        && !(this.v1.canBeInstantiatedTo(lval[maxFeasibleIndex + this.cste])))
      maxFeasibleIndex--;
    this.v0.updateSup(maxFeasibleIndex, cause);

    if (this.v0.hasEnumeratedDomain()) {
      for (int i = minFeasibleIndex + 1; i <= maxFeasibleIndex - 1; i++) {
        if (this.v0.canBeInstantiatedTo(i) && !(this.v1.canBeInstantiatedTo(this.lval[i + this.cste])))
          this.v0.removeVal(i, cause);
      }
    }
  }

  public void awake() throws ContradictionException {
    this.updateIndexFromValue();
    this.updateValueFromIndex();
  }

  public void awakeOnInf(int i) throws ContradictionException {
    if (i == 0)
      this.updateValueFromIndex();
    else
      this.updateIndexFromValue();
  }

  public void awakeOnSup(int i) throws ContradictionException {
    if (i == 0)
      this.updateValueFromIndex();
    else
      this.updateIndexFromValue();
  }

  public void awakeOnInst(int i) throws ContradictionException {
    if (i == 0)
      this.v1.instantiate(this.lval[this.v0.getVal() + this.cste], this.cIdx1);
    else
      this.updateIndexFromValue();
  }

  public void awakeOnRem(int i, int x) throws ContradictionException {
    if (i == 0)
      this.updateValueFromIndex();
    else
      this.updateIndexFromValue();
  }

  public Boolean isEntailed() {
    if (this.v1.isInstantiated()) {
      boolean b = true;
      for (IntIterator iter = this.v0.getDomain().getIterator(); iter.hasNext();) {
        int val = iter.next();
        b &= (val + this.cste) >= 0;
        b &= (val + this.cste) < this.lval.length;
        b &= this.lval[val + this.cste] == this.v1.getVal();
      }
      if (b) return Boolean.TRUE;
    } else {
      boolean b = false;
      IntIterator iter = this.v0.getDomain().getIterator();
      while (iter.hasNext() && !b) {
        int val = iter.next();
        b &= (val + this.cste) >= 0;
        b &= (val + this.cste) < this.lval.length;
        b &= this.v1.canBeInstantiatedTo(this.lval[val + this.cste]);
      }
      if (b) return null;
    }
    return Boolean.FALSE;
  }

  public boolean isSatisfied(int[] tuple) {
    return this.lval[tuple[0] + this.cste] == tuple[1];
  }
}
