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
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractIntConstraint;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;

public class ConstantConstraint extends AbstractIntConstraint implements IntConstraint {
  private boolean satisfied;

  public ConstantConstraint(boolean value) {
    satisfied = value;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public int getNbVars() {
    return 0;
  }

  public Var getVar(int i) {
    return null;
  }

  public IntDomainVar getIntVar(int i) {
	  return null;
  }

  public void setVar(int i, Var v) {
    throw new Error("BUG in CSP network management: too large index for setVar");
  }

  public boolean isCompletelyInstantiated() {
    return true;
  }

  public boolean isSatisfied() {
    return satisfied;
  }

  public boolean isSatisfied(int[] tuple) {
    return satisfied;
  }

  public void propagate() throws ContradictionException {
    if (!satisfied) {
      fail();
    }
  }

  public boolean isConsistent() {
    return satisfied;
  }

  public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
    return i;
  }

  public void setConstraintIndex(int i, int idx) {
    throw new UnsupportedOperationException();
  }

  public int getConstraintIdx(int idx) {
    throw new UnsupportedOperationException();
  }

  public final boolean isEquivalentTo(Constraint compareTo) {
    if (compareTo instanceof ConstantConstraint) {
      ConstantConstraint c = (ConstantConstraint) compareTo;
      return (this.satisfied == c.satisfied);
    } else {
      return false;
    }
  }

  public Boolean isEntailed() {
    return satisfied?Boolean.TRUE:Boolean.FALSE;
  }

  public AbstractConstraint opposite() {
    return new ConstantConstraint(!satisfied);
  }


}
