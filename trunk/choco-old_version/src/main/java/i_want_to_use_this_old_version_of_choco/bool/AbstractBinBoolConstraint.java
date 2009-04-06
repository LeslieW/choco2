// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.bool;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.util.BitSet;

/**
 * An abstract class for binary Boolean composition
 * @deprecated see Reifed package
 */
public abstract class AbstractBinBoolConstraint extends AbstractBinCompositeConstraint implements BoolConstraint {

  /**
   * using a bitvector to store four three-valued data (true, false, unknown): status/targetStatus of c.const0/2
   * Each data is stored on two bits:
   * - the first bit indicates whether the data is known or unknown
   * - the second bit indicates whether the data is true or false
   * Therefore, the second bit is meaningful only when the first one is true.
   */
  protected IStateInt statusBitVector;

  public AbstractBinBoolConstraint(AbstractConstraint c1, AbstractConstraint c2) {
    super(c1, c2);
    statusBitVector = getProblem().getEnvironment().makeInt(0);
  }

  public Object clone() throws CloneNotSupportedException {
    AbstractBinBoolConstraint newc = (AbstractBinBoolConstraint) super.clone();
    newc.statusBitVector = getProblem().getEnvironment().makeInt(0);
    return newc;
  }

  public Boolean getStatus(int constIdx) {
    if (BitSet.getBit(statusBitVector.get(), 4 * constIdx)) {
      if (BitSet.getBit(statusBitVector.get(), 4 * constIdx + 1))
        return Boolean.TRUE;
      else
        return Boolean.FALSE;
    } else
      return null;
  }

  public Boolean getTargetStatus(int constIdx) {
    if (BitSet.getBit(statusBitVector.get(), 4 * constIdx + 2)) {
      if (BitSet.getBit(statusBitVector.get(), 4 * constIdx + 3))
        return Boolean.TRUE;
      else
        return Boolean.FALSE;
    } else
      return null;
  }

  public void setStatus(int constIdx, boolean st) {
    assert((getStatus(constIdx) == null) || (getStatus(constIdx) == Boolean.valueOf(st)));                // TODO! move into a junit currentElement
    statusBitVector.set(BitSet.setBit(statusBitVector.get(), 4 * constIdx));
    if (st) {
      if (getStatus(constIdx) == Boolean.FALSE)
        statusBitVector.set(BitSet.setBit(statusBitVector.get(), 4 * constIdx + 1));
    } else {
      if (getStatus(constIdx) == Boolean.TRUE)
        statusBitVector.set(BitSet.unsetBit(statusBitVector.get(), 4 * constIdx + 1));
    }
    assert(!(getStatus(constIdx) == null));                // TODO! move into a junit currentElement
    assert((getStatus(constIdx) == Boolean.valueOf(st)));  // TODO! move into a junit currentElement
  }

  public void setTargetStatus(int constIdx, boolean st) {
    assert((getTargetStatus(constIdx) == null));             // TODO! move into a junit currentElement
    statusBitVector.set(BitSet.setBit(statusBitVector.get(), 4 * constIdx + 2));
    if (st) {
      if (getTargetStatus(constIdx) == Boolean.FALSE)
        statusBitVector.set(BitSet.setBit(statusBitVector.get(), 4 * constIdx + 3));
    } else {
      if (getTargetStatus(constIdx) == Boolean.TRUE)
        statusBitVector.set(BitSet.unsetBit(statusBitVector.get(), 4 * constIdx + 3));
    }
    assert(!(getTargetStatus(constIdx) == null));                // TODO! move into a junit currentElement
    assert((getTargetStatus(constIdx) == Boolean.valueOf(st)));  // TODO! move into a junit currentElement
  }

  public void setSubConstraintStatus(Constraint subConstraint, boolean status, int varOffset) {
    int subConstIdx = getSubConstraintIdx(varOffset);
    if (subConstraint == getSubConstraint(subConstIdx)) {
      setStatus(subConstIdx, status);
    } else {
      BoolConstraint newRoot = (BoolConstraint) getSubConstraint(subConstIdx);
      int deltaOffset = (subConstIdx == 0) ? 0 : this.offset;
      newRoot.setSubConstraintStatus(subConstraint, status, varOffset - deltaOffset);
    }
  }

}
