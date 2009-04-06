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
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * @deprecated see Reifed package
 */
public class LargeDisjunction extends AbstractLargeBoolConstraintWithCounterOpposite {

  public LargeDisjunction(Constraint[] subConstraints) {
    super(subConstraints);
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
      buf.append("or (" + constraint.pretty() + ") ");
    }
    return buf.toString();
  }

  /**
   * this compares the number of false constraints (subconstraints whose status is false)
   * with the overall number of constraints.
   */
  protected void checkNbFalseStatus() throws ContradictionException {
    if (nbFalseStatus.get() == nbConstraints) {
      fail();
    } else if ((nbFalseStatus.get() == nbConstraints - 1) && nbTrueStatus.get() == 0) {
      for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
        if (getStatus(constIdx) == null) {
          setTargetStatus(constIdx, true);
          constraints[constIdx].awake();
          break;
        }
      }
    }
  }

  /**
   * checks the status of the i-th constraint of the disjunction and propagates accordingly
   *
   * @param constIdx
   * @throws ContradictionException
   */
  protected void checkStatus(int constIdx) throws ContradictionException {
    assert(getStatus(constIdx) == null);
    Boolean Bi = constraints[constIdx].isEntailed();
    if (Bi != null) {
      Boolean TBi = getTargetStatus(constIdx);
      if (TBi != null) {
        if (TBi != Bi) {
          fail();
        }
      } else {
        setStatus(constIdx, Bi.booleanValue());
        if (Bi == Boolean.TRUE) {
          nbTrueStatus.set(nbTrueStatus.get() + 1);
        } else {
          nbFalseStatus.set(nbFalseStatus.get() + 1);
          checkNbFalseStatus();
        }
      }
    }
  }

  public void awakeOnInf(int varIdx) throws ContradictionException {
    int constIdx = getSubConstraintIdx(varIdx);
    Boolean Bi = getStatus(constIdx);
    Boolean TBi = getTargetStatus(constIdx);
    if (Bi == null) {
      if (TBi == Boolean.TRUE) {
        int localVarIndex = getLocalVarIndex(varIdx, constIdx);
        ((IntConstraint) constraints[constIdx]).awakeOnInf(localVarIndex);
      } else if (TBi == null) {
        checkStatus(constIdx);
      }
    }
  }

  public void awakeOnSup(int varIdx) throws ContradictionException {
    int constIdx = getSubConstraintIdx(varIdx);
    Boolean Bi = getStatus(constIdx);
    Boolean TBi = getTargetStatus(constIdx);
    if (Bi == null) {
      if (TBi == Boolean.TRUE) {
        int localVarIndex = getLocalVarIndex(varIdx, constIdx);
        ((IntConstraint) constraints[constIdx]).awakeOnSup(localVarIndex);
      } else if (TBi == null) {
        checkStatus(constIdx);
      }
    }
  }

  public void awakeOnRemovals(int varIdx, IntIterator deltaDomain) throws ContradictionException {
    int constIdx = getSubConstraintIdx(varIdx);
    Boolean Bi = getStatus(constIdx);
    Boolean TBi = getTargetStatus(constIdx);
    if (Bi == null) {
      if (TBi == Boolean.TRUE) {
        int localVarIndex = getLocalVarIndex(varIdx, constIdx);
        ((IntConstraint) constraints[constIdx]).awakeOnRemovals(localVarIndex, deltaDomain);
      } else if (TBi == null) {
        checkStatus(constIdx);
      }
    }
  }

  public void awakeOnBounds(int varIdx) throws ContradictionException {
    int constIdx = getSubConstraintIdx(varIdx);
    Boolean Bi = getStatus(constIdx);
    Boolean TBi = getTargetStatus(constIdx);
    if (Bi == null) {
      if (TBi == Boolean.TRUE) {
        int localVarIndex = getLocalVarIndex(varIdx, constIdx);
        ((IntConstraint) constraints[constIdx]).awakeOnBounds(localVarIndex);
      } else if (TBi == null) {
        checkStatus(constIdx);
      }
    }
  }

  public void awakeOnInst(int varIdx) throws ContradictionException {
    int constIdx = getSubConstraintIdx(varIdx);
    Boolean Bi = getStatus(constIdx);
    Boolean TBi = getTargetStatus(constIdx);
    if (Bi == null) {
      if (TBi == Boolean.TRUE) {
        int localVarIndex = getLocalVarIndex(varIdx, constIdx);
        ((IntConstraint) constraints[constIdx]).awakeOnInst(localVarIndex);
      } else if (TBi == null) {
        checkStatus(constIdx);
      }
    }
  }

  public void awakeOnRem(int varIdx, int val) throws ContradictionException {
    int constIdx = getSubConstraintIdx(varIdx);
    Boolean Bi = getStatus(constIdx);
    Boolean TBi = getTargetStatus(constIdx);
    if (Bi == null) {
      if (TBi == Boolean.TRUE) {
        int localVarIndex = getLocalVarIndex(varIdx, constIdx);
        ((IntConstraint) constraints[constIdx]).awakeOnRem(localVarIndex, val);
      } else if (TBi == null) {
        checkStatus(constIdx);
      }
    }
  }

  public boolean isSatisfied() {
    boolean oneSatisfied = false;
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      if (constraints[constIdx].isSatisfied()) {
        oneSatisfied = true;
        break;
      }
    }
    return oneSatisfied;
  }

  public boolean isConsistent() {
    throw new UnsupportedOperationException();
  }

  public Boolean isEntailed() {
    boolean allFalse = true;
    boolean oneTrue = false;
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      Boolean Bi = getStatus(constIdx);
      if (Bi == null) {
        Bi = constraints[constIdx].isEntailed();
        if (Bi != null) {
          setStatus(constIdx, Bi.booleanValue());
        }
      }
      if (Bi != Boolean.FALSE) allFalse = false;
      if (Bi == Boolean.TRUE) oneTrue = true;
    }
    if (oneTrue) {
      return Boolean.TRUE;
    } else if (allFalse) {
      return Boolean.FALSE;
    } else {
      return null;
    }
  }

  public void propagate() throws ContradictionException {
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      if (getStatus(constIdx) == null) {
        checkStatus(constIdx);
      }
    }
  }

  // the initial account of all statuses catches the case of disjunctions with no or one single disjunct
  public void awake() throws ContradictionException {
    checkNbFalseStatus();
    propagate();
  }

  public AbstractConstraint opposite() {
    Constraint[] branches = new Constraint[nbConstraints];
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      branches[constIdx] = opposites[constIdx]; //constraints[constIdx].opposite();
    }
    return new LargeConjunction(branches);
  }

  public boolean isEquivalentTo(Constraint compareTo) {
    if (compareTo instanceof LargeDisjunction) {
      LargeDisjunction c = (LargeDisjunction) compareTo;
      int nbSubConst = this.getNbSubConstraints();
      if (c.getNbSubConstraints() == nbSubConst) {
        boolean allEquiv = true;
        for (int subConstIdx = 0; subConstIdx < nbSubConst; subConstIdx++) {
          Constraint subc0 = this.getSubConstraint(subConstIdx);
          Constraint subc1 = c.getSubConstraint(subConstIdx);
          if (!subc0.isEquivalentTo(subc1)) {
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
