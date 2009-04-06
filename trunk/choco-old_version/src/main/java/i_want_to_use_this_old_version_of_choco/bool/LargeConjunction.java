// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.bool;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * @deprecated see Reifed package
 */
public class LargeConjunction extends AbstractLargeBoolConstraint {
                                                                     
                                                                             
  public LargeConjunction(Constraint[] alternatives) {
    super(alternatives);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Pretty print of the constraint.
   */

  public String pretty() {
    StringBuffer buf = new StringBuffer();
    buf.append(" (" + constraints[0].pretty() + ") ");
    for (int i = 1; i < constraints.length; i++) {
      AbstractConstraint constraint = constraints[i];
      buf.append("and (" + constraint.pretty() + ") ");
    }
    return buf.toString();
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    int constIdx = getSubConstraintIdx(idx);
    IntConstraint subConstraint = (IntConstraint) constraints[constIdx];
    int localIdx = ((constIdx == 0) ? idx : idx - offsets[constIdx - 1]);
    subConstraint.awakeOnInf(localIdx);
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    int constIdx = getSubConstraintIdx(idx);
    IntConstraint subConstraint = (IntConstraint) constraints[constIdx];
    int localIdx = ((constIdx == 0) ? idx : idx - offsets[constIdx - 1]);
    subConstraint.awakeOnSup(localIdx);
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    int constIdx = getSubConstraintIdx(idx);
    IntConstraint subConstraint = (IntConstraint) constraints[constIdx];
    int localIdx = ((constIdx == 0) ? idx : idx - offsets[constIdx - 1]);
    subConstraint.awakeOnInst(localIdx);
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    int constIdx = getSubConstraintIdx(idx);
    IntConstraint subConstraint = (IntConstraint) constraints[constIdx];
    int localIdx = ((constIdx == 0) ? idx : idx - offsets[constIdx - 1]);
    subConstraint.awakeOnRem(localIdx, x);
  }

  public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
    int constIdx = getSubConstraintIdx(idx);
    IntConstraint subConstraint = (IntConstraint) constraints[constIdx];
    int localIdx = ((constIdx == 0) ? idx : idx - offsets[constIdx - 1]);
    subConstraint.awakeOnRemovals(localIdx, deltaDomain);
  }

  public void awakeOnBounds(int varIndex) throws ContradictionException {
    int constIdx = getSubConstraintIdx(varIndex);
    IntConstraint subConstraint = (IntConstraint) constraints[constIdx];
    int localIdx = ((constIdx == 0) ? varIndex : varIndex - offsets[constIdx - 1]);
    subConstraint.awakeOnBounds(localIdx);
  }

  public void propagate() throws ContradictionException {
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      if (getStatus(constIdx) != Boolean.TRUE) {
        constraints[constIdx].propagate();
      }
    }
  }

  public void awake() throws ContradictionException {
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      if (getStatus(constIdx) != Boolean.TRUE) {
        constraints[constIdx].awake();
      }
    }
  }

  public boolean isSatisfied() {
    boolean satisfied = true;
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      if (!constraints[constIdx].isSatisfied()) {
        satisfied = false;
        break;
      }
    }
    return satisfied;
  }

  public Boolean isEntailed() {
    boolean allTrue = true;
    boolean oneFalse = false;
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      Boolean Bi = getStatus(constIdx);
      if (Bi == null) {
        Bi = constraints[constIdx].isEntailed();
        if (Bi != null) {
          setStatus(constIdx, Bi.booleanValue());
        }
      }
      if (Bi != Boolean.TRUE) allTrue = false;
      if (Bi == Boolean.FALSE) oneFalse = true;
    }
    if (allTrue) {
      return Boolean.TRUE;
    } else if (oneFalse) {
      return Boolean.FALSE;
    } else {
      return null;
    }
  }

  public boolean isConsistent() {
    boolean consistent = true;
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      if (!constraints[constIdx].isConsistent()) {
        consistent = false;
        break;
      }
    }
    return consistent;
  }

  public AbstractConstraint opposite() {
    Constraint[] branches = new Constraint[nbConstraints];
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      branches[constIdx] = constraints[constIdx].opposite();
    }
    return new LargeDisjunction(branches);
  }

  public boolean isEquivalentTo(Constraint compareTo) {
    if (compareTo instanceof LargeConjunction) {
      LargeConjunction c = (LargeConjunction) compareTo;
      int nbSubConst = this.getNbSubConstraints();
      if (c.getNbSubConstraints() == nbSubConst) {
        boolean allEquiv = true;
        for (int subConstIdx = 0; subConstIdx < nbSubConst; subConstIdx++) {
          if (!this.getSubConstraint(subConstIdx).isEquivalentTo(c.getSubConstraint(subConstIdx))) {
            allEquiv = false;
            break;
          }
        }
        return allEquiv;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

}
