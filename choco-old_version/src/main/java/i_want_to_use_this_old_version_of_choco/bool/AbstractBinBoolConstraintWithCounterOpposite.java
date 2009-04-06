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
import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;

/**
 * An abstract class storing the counterpart of each subconstraint + an index correspondence for variables
 * (between subconstraints and opposite subconstraints)
 * @deprecated see Reifed package
 */
public abstract class AbstractBinBoolConstraintWithCounterOpposite extends AbstractBinBoolConstraint {
  /**
   * the counterpart of the first subconstraint
   */
  AbstractConstraint oppositeConst0;

  /**
   * the counterpart of the second subconstraint
   */
  AbstractConstraint oppositeConst1;

  /**
   * the index correspondance for the variables of the first subconstraint (with its counterpart)
   */
  int[] indicesInOpposite0;

  /**
   * the index correspondance for the variables of the first subconstraint (with its counterpart)
   */
  int[] indicesInOpposite1;

  public AbstractBinBoolConstraintWithCounterOpposite(AbstractConstraint c1, AbstractConstraint c2) {
    super(c1, c2);
    oppositeConst0 = const0.opposite();
    oppositeConst1 = const1.opposite();
    assert(const0.getNbVars() == oppositeConst0.getNbVars());
    assert(const1.getNbVars() == oppositeConst1.getNbVars());
    indicesInOpposite0 = new int[const0.getNbVars()];
    for (int k = 0; k < const0.getNbVars(); k++) {
      indicesInOpposite0[k] = ((Propagator) const0).getVarIdxInOpposite(k);
    }
    indicesInOpposite1 = new int[const1.getNbVars()];
    for (int k = 0; k < const1.getNbVars(); k++) {
      indicesInOpposite1[k] = const1.getVarIdxInOpposite(k);
    }
  }

  public Object clone() throws CloneNotSupportedException {
    AbstractBinBoolConstraintWithCounterOpposite newc = (AbstractBinBoolConstraintWithCounterOpposite) super.clone();
    newc.indicesInOpposite0 = new int[this.indicesInOpposite0.length];
    System.arraycopy(this.indicesInOpposite0, 0, newc.indicesInOpposite0, 0, this.indicesInOpposite0.length);
    newc.indicesInOpposite1 = new int[this.indicesInOpposite1.length];
    System.arraycopy(this.indicesInOpposite1, 0, newc.indicesInOpposite1, 0, this.indicesInOpposite1.length);
    newc.oppositeConst0 = (AbstractConstraint) this.oppositeConst0.clone();
    newc.oppositeConst1 = (AbstractConstraint) this.oppositeConst1.clone();
    return newc;
  }

  public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
    int j = i;
    j = const0.assignIndices(root, j, dynamicAddition);
    this.offset = j - i;
    for (int k = 0; k < this.offset; k++) {
      oppositeConst0.setConstraintIndex(indicesInOpposite0[k], const0.getConstraintIdx(k));
    }
    j = const1.assignIndices(root, j, dynamicAddition);
    int delta = j - (i + offset);
    for (int k = 0; k < delta; k++) {
      oppositeConst1.setConstraintIndex(indicesInOpposite1[k], const1.getConstraintIdx(k));
    }
    return j;
  }

  public Constraint getOppositeSubConstraint(int subConstIdx) {
    return ((subConstIdx == 0) ? oppositeConst0 : oppositeConst1);
  }

  public void setSubConstraintStatus(Constraint subConstraint, boolean status, int varOffset) {
    int subConstIdx = getSubConstraintIdx(varOffset);
    if (subConstraint == getSubConstraint(subConstIdx)) {
      setStatus(subConstIdx, status);
    } else if (subConstraint == getOppositeSubConstraint(subConstIdx)) {
      setStatus(subConstIdx, !status);
    } else {
      BoolConstraint newRoot = (BoolConstraint) getSubConstraint(subConstIdx);
      int deltaOffset = (subConstIdx == 0) ? 0 : this.offset; //offsets[subConstIdx];
      newRoot.setSubConstraintStatus(subConstraint, status, varOffset - deltaOffset);
    }
  }
}
