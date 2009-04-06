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
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;

/**
 * An abstract class storing the counterpart of each subconstraint + an index correspondence for variables
 * (between subconstraints and opposite subconstraints)
 * @deprecated see Reifed package
 */
public abstract class AbstractLargeBoolConstraintWithCounterOpposite extends AbstractLargeBoolConstraint {

  /**
   * the counterpart of the first subconstraint
   */
  AbstractConstraint[] opposites;

  /**
   * the index correspondance for the variables of the subconstraints and their counterparts
   */
  int[][] indicesInOpposites;


  public AbstractLargeBoolConstraintWithCounterOpposite(Constraint[] subConstraints) {
    super(subConstraints);
    initAbstractLargeBoolConstraintWithCounterOpposite();
  }

  public AbstractLargeBoolConstraintWithCounterOpposite(Constraint[] subConstraints, IntDomainVar[] moreVars) {
    super(subConstraints, moreVars);
    initAbstractLargeBoolConstraintWithCounterOpposite();
  }

  public Object clone() throws CloneNotSupportedException {
    AbstractLargeBoolConstraintWithCounterOpposite newc = (AbstractLargeBoolConstraintWithCounterOpposite) super.clone();
    newc.initAbstractLargeBoolConstraintWithCounterOpposite();
    return newc;
  }

  protected void initAbstractLargeBoolConstraintWithCounterOpposite() {
    int nbc = getNbSubConstraints();
    opposites = new AbstractConstraint[nbc];
    for (int constIdx = 0; constIdx < nbc; constIdx++) {
      Constraint subConst = getSubConstraint(constIdx);
      opposites[constIdx] = subConst.opposite();
      for (int varIdx = 0; varIdx < subConst.getNbVars(); varIdx++) {
        subConst.getVarIdxInOpposite(varIdx);
      }
    }

    indicesInOpposites = new int[nbc][];
    for (int constIdx = 0; constIdx < nbc; constIdx++) {
      Constraint subConst = getSubConstraint(constIdx);
      indicesInOpposites[constIdx] = new int[subConst.getNbVars()];
      for (int k = 0; k < subConst.getNbVars(); k++) {
        indicesInOpposites[constIdx][k] = subConst.getVarIdxInOpposite(k);
      }
    }
  }

  public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
    int j = i;
    for (int constIdx = 0; constIdx < this.nbConstraints; constIdx++) {
      int j0 = j;
      AbstractConstraint subc = constraints[constIdx];
      j = subc.assignIndices(root, j, dynamicAddition);
      assert(offsets[constIdx] == j - i);
      for (int varIdx = 0; varIdx < j - j0; varIdx++) {
        opposites[constIdx].setConstraintIndex(indicesInOpposites[constIdx][varIdx], subc.getConstraintIdx(varIdx));
      }
    }
    assert(additionalIndices.length == additionalVars.length);
    for (int varIdx = 0; varIdx < additionalVars.length; varIdx++) {
      j++;
      int cidx = root.connectVar(additionalVars[varIdx], j, dynamicAddition);
      this.additionalIndices[varIdx] = cidx;
    }
    return j;
  }

  public Constraint getOppositeSubConstraint(int subConstIdx) {
    return opposites[subConstIdx];
  }

  public void setSubConstraintStatus(Constraint subConstraint, boolean status, int varOffset) {
    int subConstIdx = getSubConstraintIdx(varOffset);
    if (subConstraint == getSubConstraint(subConstIdx)) {
      setStatus(subConstIdx, status);
    } else if (subConstraint == getOppositeSubConstraint(subConstIdx)) {
      setStatus(subConstIdx, !status);
    } else {
      BoolConstraint newRoot = (BoolConstraint) getSubConstraint(subConstIdx);
      int deltaOffset = (subConstIdx == 0) ? 0 : offsets[subConstIdx - 1];
      newRoot.setSubConstraintStatus(subConstraint, status, varOffset - deltaOffset);
    }
  }
}