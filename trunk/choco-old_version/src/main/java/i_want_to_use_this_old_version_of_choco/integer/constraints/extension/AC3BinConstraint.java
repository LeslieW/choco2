// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.util.DisposableIntIterator;

public class AC3BinConstraint extends CspBinConstraint {

  public AC3BinConstraint(IntDomainVar x0, IntDomainVar x1, BinRelation rela) {//int[][] consistencyMatrice) {
    super(x0, x1, rela);
  }

  public Object clone() {
    return new AC3BinConstraint(this.v0, this.v1, this.relation);
  }

  // updates the support for all values in the domain of v1, and remove unsupported values for v1
  public void reviseV1() throws ContradictionException {
    int nbs = 0;
    DisposableIntIterator itv1 = v1.getDomain().getIterator();
    while (itv1.hasNext()) {
      DisposableIntIterator itv0 = v0.getDomain().getIterator();
      int val1 = itv1.next();
      while (itv0.hasNext()) {
        int val0 = itv0.next();
        if (relation.isConsistent(val0, val1)) {
          nbs += 1;
          break;
        }
      }
      itv0.dispose();
      itv0 = null;
      if (nbs == 0) v1.removeVal(val1, cIdx1);
      nbs = 0;
    }
    itv1.dispose();
    itv1 = null;
  }

  // updates the support for all values in the domain of v0, and remove unsupported values for v0
  public void reviseV0() throws ContradictionException {
    int nbs = 0;
    DisposableIntIterator itv0 = v0.getDomain().getIterator();
    while (itv0.hasNext()) {
      DisposableIntIterator itv1 = v1.getDomain().getIterator();
      int val0 = itv0.next();
      while (itv1.hasNext()) {
        int val1 = itv1.next();
        if (relation.isConsistent(val0, val1)) {
          nbs += 1;
          break;
        }
      }
      itv1.dispose();
      itv1 = null;
      if (nbs == 0) v0.removeVal(val0, cIdx0);
      nbs = 0;
    }
    itv0.dispose();
    itv0 = null;
  }

  // standard filtering algorithm initializing all support counts
  public void propagate() throws ContradictionException {
    reviseV0();
    reviseV1();
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    if (idx == 0) {
      reviseV1();
    } else
      reviseV0();
  }
  //propagate();

  /**
   * Propagation when a minimal bound of a variable was modified.
   *
   * @param idx The index of the variable.
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */
  // Note: these methods could be improved by considering for each value, the minimal and maximal support considered into the count
  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx == 0) {
      reviseV1();
    } else
      reviseV0();
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx == 0) {
      reviseV1();
    } else
      reviseV0();
  }


  /**
   * Propagation when a variable is instantiated.
   *
   * @param idx The index of the variable.
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */

  public void awakeOnInst(int idx) throws ContradictionException {
    if (idx == 0) {
      reviseV1();
    } else
      reviseV0();
  }


  public AbstractConstraint opposite() {
    BinRelation rela2 = (BinRelation) ((ConsistencyRelation) relation).getOpposite();
    AbstractConstraint ct = new AC3BinConstraint(v0, v1, rela2);
    return ct;
  }

  public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append("AC3(").append(v0.pretty()).append(", ").append(v1.pretty()).append(", ").
        append(this.relation.getClass().getSimpleName()).append(")");
    return sb.toString();
  }
}
