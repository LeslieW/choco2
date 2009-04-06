// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.bool;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.mem.IStateIntVector;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;
import i_want_to_use_this_old_version_of_choco.util.BitSet;

/**
 * An abstract class for Boolean composition of more than two constraints
 * @deprecated see Reifed package
 */
public abstract class AbstractLargeBoolConstraint extends AbstractLargeCompositeConstraint implements BoolConstraint {
  protected IStateIntVector statusBitVector;
  protected IStateInt nbTrueStatus;
  protected IStateInt nbFalseStatus;

  public AbstractLargeBoolConstraint(Constraint[] subConstraints) {
    super(subConstraints);
    initAbstractLargeBoolConstraint();
  }

  public AbstractLargeBoolConstraint(Constraint[] subConstraints, IntDomainVar[] moreVars) {
    super(subConstraints, moreVars);
    initAbstractLargeBoolConstraint();
  }

  public Object clone() throws CloneNotSupportedException {
    AbstractLargeBoolConstraint newc = (AbstractLargeBoolConstraint) super.clone();
    newc.initAbstractLargeBoolConstraint();
    return newc;
  }

  protected void initAbstractLargeBoolConstraint() {
    IEnvironment env = getProblem().getEnvironment();
    statusBitVector = env.makeIntVector((nbConstraints - 1) / 8 + 1, 0);
    nbTrueStatus = env.makeInt(0);
    nbFalseStatus = env.makeInt(0);
  }

  public Boolean getStatus(int constIdx) {
    int i1 = (constIdx / 8);
    int i2 = 4 * (constIdx % 8);
    if (BitSet.getBit(statusBitVector.get(i1), i2)) {
      if (BitSet.getBit(statusBitVector.get(i1), i2 + 1)) {
        return Boolean.TRUE;
      } else {
        return Boolean.FALSE;
      }
    } else {
      return null;
    }
  }

  public Boolean getTargetStatus(int constIdx) {
    int i1 = (constIdx / 8);
    int i2 = 4 * (constIdx % 8);
    if (BitSet.getBit(statusBitVector.get(i1), i2 + 2)) {
      if (BitSet.getBit(statusBitVector.get(i1), i2 + 3)) {
        return Boolean.TRUE;
      } else {
        return Boolean.FALSE;
      }
    } else {
      return null;
    }
  }

  public void setStatus(int constIdx, boolean st) {
    assert(getStatus(constIdx) == null);
    int i1 = (constIdx / 8);
    int i2 = 4 * (constIdx % 8);
    statusBitVector.set(i1, BitSet.setBit(statusBitVector.get(i1), i2));
    if (st) {
      if (getStatus(constIdx) == Boolean.FALSE)
        statusBitVector.set(i1, BitSet.setBit(statusBitVector.get(i1), i2 + 1));
    } else {
      if (getStatus(constIdx) == Boolean.TRUE)
        statusBitVector.set(i1, BitSet.unsetBit(statusBitVector.get(i1), i2 + 1));
    }
    assert(getStatus(constIdx) == Boolean.valueOf(st));
  }

  public void setTargetStatus(int constIdx, boolean st) {
    assert(getTargetStatus(constIdx) == null);
    int i1 = (constIdx / 8);
    int i2 = 4 * (constIdx % 8);
    statusBitVector.set(i1, BitSet.setBit(statusBitVector.get(i1), i2 + 2));
    if (st) {
      if (getTargetStatus(constIdx) == Boolean.FALSE)
        statusBitVector.set(i1, BitSet.setBit(statusBitVector.get(i1), i2 + 3));
    } else {
      if (getTargetStatus(constIdx) == Boolean.TRUE)
        statusBitVector.set(i1, BitSet.unsetBit(statusBitVector.get(i1), i2 + 3));
    }
    assert(getTargetStatus(constIdx) == Boolean.valueOf(st));
  }


  public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
    int j = i;
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      j = constraints[constIdx].assignIndices(root, j, dynamicAddition);
    }
    for (int k = 0; k < additionalVars.length; k++) {
      j++;
      root.connectVar(additionalVars[k], j, dynamicAddition);
    }
    return j;
  }

  public void setSubConstraintStatus(Constraint subConstraint, boolean status, int varOffset) {
    int subConstIdx = getSubConstraintIdx(varOffset);
    if (subConstraint == getSubConstraint(subConstIdx)) {
      setStatus(subConstIdx, status);
    } else {
      BoolConstraint newRoot = (BoolConstraint) getSubConstraint(subConstIdx);
      int deltaOffset = (subConstIdx == 0) ? 0 : offsets[subConstIdx - 1];
      newRoot.setSubConstraintStatus(subConstraint, status, varOffset - deltaOffset);
    }
  }

}