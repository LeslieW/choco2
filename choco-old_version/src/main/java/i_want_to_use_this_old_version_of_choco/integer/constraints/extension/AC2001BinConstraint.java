package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.util.DisposableIntIterator;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class AC2001BinConstraint extends CspBinConstraint {

  protected IStateInt[] currentSupport0;
  protected IStateInt[] currentSupport1;

  protected int offset0;
  protected int offset1;

  public AC2001BinConstraint(IntDomainVar x0, IntDomainVar x1, BinRelation relation) {
    super(x0, x1, relation);
    offset0 = x0.getInf();
    offset1 = x1.getInf();
    currentSupport0 = new IStateInt[x0.getSup() - x0.getInf() + 1];
    currentSupport1 = new IStateInt[x1.getSup() - x1.getInf() + 1];
    for (int i = 0; i < currentSupport0.length; i++) {
      currentSupport0[i] = this.getProblem().getEnvironment().makeInt();
      currentSupport0[i].set(-1);
    }
    for (int i = 0; i < currentSupport1.length; i++) {
      currentSupport1[i] = this.getProblem().getEnvironment().makeInt();
      currentSupport1[i].set(-1);
    }
  }

  public Object clone() {
    return new AC2001BinConstraint(this.v0, this.v1, this.relation);
  }

  // updates the support for all values in the domain of v1, and remove unsupported values for v1
  public void reviseV1() throws ContradictionException {
    DisposableIntIterator itv1 = v1.getDomain().getIterator();
    while (itv1.hasNext()) {
      int y = itv1.next();
      if (!v0.canBeInstantiatedTo(currentSupport1[y - offset1].get()))
        updateSupportVal1(y);
    }
    itv1.dispose();
  }

  // updates the support for all values in the domain of v0, and remove unsupported values for v0
  public void reviseV0() throws ContradictionException {
    DisposableIntIterator itv0 = v0.getDomain().getIterator();
    while (itv0.hasNext()) {
      int x = itv0.next();
      if (!v1.canBeInstantiatedTo(currentSupport0[x - offset0].get()))
        updateSupportVal0(x);
    }
    itv0.dispose();
  }

  protected void updateSupportVal0(int x) throws ContradictionException {
    boolean found = false;
    int support = currentSupport0[x - offset0].get();
    int max2 = v1.getSup();
    //System.out.println("max2 " + max2);
    while (!found && support < max2) {
      //System.out.println(" " + support);
      support = v1.getDomain().getNextValue(support);
      if (relation.isConsistent(x, support)) found = true;
    }
    if (found)
      currentSupport0[x - offset0].set(support);
    else {
      v0.removeVal(x, cIdx0);
    }
  }

  protected void updateSupportVal1(int y) throws ContradictionException {
    boolean found = false;
    int support = currentSupport1[y - offset1].get();
    int max1 = v0.getSup();
    while (!found && support < max1) {
      support = v0.getDomain().getNextValue(support);
      if (relation.isConsistent(support, y)) found = true;
    }
    if (found)
      currentSupport1[y - offset1].set(support);
    else {
      v1.removeVal(y, cIdx1);
    }
  }

  public void awake() throws ContradictionException {
    DisposableIntIterator itv0 = v0.getDomain().getIterator();
    int support = 0;
    boolean found = false;
    while (itv0.hasNext()) {
      DisposableIntIterator itv1 = v1.getDomain().getIterator();
      int val0 = itv0.next();
      while (itv1.hasNext()) {
        int val1 = itv1.next();
        if (relation.isConsistent(val0, val1)) {
          support = val1;
          found = true;
          break;
        }
      }
      itv1.dispose();
      if (!found) {
        v0.removeVal(val0, cIdx0);
      } else
        currentSupport0[val0 - offset0].set(support);

      found = false;
    }
    itv0.dispose();
    found = false;
    DisposableIntIterator itv1 = v1.getDomain().getIterator();
    while (itv1.hasNext()) {
      itv0 = v0.getDomain().getIterator();
      int val1 = itv1.next();
      while (itv0.hasNext()) {
        int val0 = itv0.next();
        if (relation.isConsistent(val0, val1)) {
          support = val0;
          found = true;
          break;
        }
      }
      itv0.dispose();
      if (!found) {
        v1.removeVal(val1, cIdx1);
      } else
        currentSupport1[val1 - offset1].set(support);
      found = false;
    }
    itv1.dispose();
    //propagate();
  }

  public void propagate() throws ContradictionException {
    reviseV0();
    reviseV1();
  }

  public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
    if (idx == 0)
      reviseV1();
    else
      reviseV0();
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx == 0)
      reviseV1();
    else
      reviseV0();
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx == 0)
      reviseV1();
    else
      reviseV0();
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    if (idx == 0)
      reviseV1();
    else
      reviseV0();
  }

  public void awakeOnBounds(int varIndex) throws ContradictionException {
    if (varIndex == 0)
      reviseV1();
    else
      reviseV0();
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    if (idx == 0) {
      int value = v0.getVal();
      DisposableIntIterator itv1 = v1.getDomain().getIterator();
      while (itv1.hasNext()) {
        int val = itv1.next();
        if (!relation.isConsistent(value, val)) {
          v1.removeVal(val, cIdx1);
        }
      }
      itv1.dispose();
    } else {
      int value = v1.getVal();
      DisposableIntIterator itv0 = v0.getDomain().getIterator();
      while (itv0.hasNext()) {
        int val = itv0.next();
        if (!relation.isConsistent(val, value)) {
          v0.removeVal(val, cIdx0);
        }
      }
      itv0.dispose();
    }
  }

  public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append("AC2001(").append(v0.pretty()).append(", ").append(v1.pretty()).append(", ").
        append(this.relation.getClass().getSimpleName()).append(")");
    return sb.toString();
  }
}
