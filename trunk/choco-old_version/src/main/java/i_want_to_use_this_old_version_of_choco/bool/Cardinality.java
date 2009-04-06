// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.bool;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * A class for cardinality/atleast/atmost constraints
 * (constraints modelling by a domain variable the number of subconstraints that are true among a list
 * @deprecated see Reifed package
 */
public class Cardinality extends AbstractLargeBoolConstraintWithCounterOpposite {

  protected boolean constrainOnInfNumber = true;

  protected boolean constrainOnSupNumber = true;

  public Cardinality(Constraint[] alternatives, IntDomainVar cardVar, boolean constrainOnInf, boolean constrainOnSup) {
    super(alternatives, new IntDomainVarImpl[]{(IntDomainVarImpl) cardVar});
    constrainOnInfNumber = constrainOnInf;
    constrainOnSupNumber = constrainOnSup;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * a local short-cut accessor
   *
   * @return the "meta"-variable denoting the number of constraints that will hold
   */
  protected IntDomainVarImpl getCardVar() {
    return additionalVars[0];
  }

  /**
   * a local short-cut accessor
   *
   * @return the index of the "meta"-variable denoting the number of constraints that will hold
   */
  protected int getCardVarIndex() {
    return additionalIndices[0];
  }

// back-propagates the upper bound of the counter variable on the status fo the subconstraints.
  protected void awakeOnNbTrue() throws ContradictionException {
    if (constrainOnSupNumber) {
      IntDomainVarImpl cardVar = getCardVar();
      cardVar.updateInf(nbTrueStatus.get(), -1); // TODO: check that -1 !!!!!
      if (nbTrueStatus.get() == cardVar.getSup()) {
        for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
          if (getStatus(constIdx) == null) {
            opposites[constIdx].awake();
          }
        }
      }
    }
  }

// back-propagates the lower bound of the counter variable on the status fo the subconstraints.
  protected void awakeOnNbFalse() throws ContradictionException {
    if (constrainOnInfNumber) {
      IntDomainVarImpl cardVar = getCardVar();
      cardVar.updateSup(nbConstraints - nbFalseStatus.get(), -1); // TODO: check that -1 !!!!!
      if (nbConstraints - nbFalseStatus.get() == cardVar.getInf()) {
        for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
          if (getStatus(constIdx) == null) {
            constraints[constIdx].awake();
          }
        }
      }
    }
  }

  public void checkStatus(int constIdx) throws ContradictionException {
    assert((0 <= constIdx) && (constIdx < nbConstraints));
    assert(getStatus(constIdx) == null);
    Boolean Bi = constraints[constIdx].isEntailed();
    if (Bi != null) {
      setStatus(constIdx, Bi.booleanValue());
      if (Bi.booleanValue() == true) {
        nbTrueStatus.set(nbTrueStatus.get() + 1);
        awakeOnNbTrue();
      } else {
        nbFalseStatus.set(nbFalseStatus.get() + 1);
        awakeOnNbFalse();
      }
    }
  }

  public void awakeOnInf(int varIdx) throws ContradictionException {
    if (varIdx == getNbVars() - 1) {
      awakeOnNbFalse();
    } else {
      assert(varIdx < getNbVars());
      int constIdx = getSubConstraintIdx(varIdx);
      if (getStatus(constIdx) == null) {
        checkStatus(constIdx);
      } else {
        int localIdx = ((constIdx == 0) ? varIdx : varIdx - offsets[constIdx - 1]);
        if (getStatus(constIdx) == Boolean.TRUE) {
          ((IntConstraint) constraints[constIdx]).awakeOnInf(localIdx);
        }
      }
    }
  }

  public void awakeOnSup(int varIdx) throws ContradictionException {
    if (varIdx == getNbVars() - 1) {
      awakeOnNbFalse();
    } else {
      assert(varIdx < getNbVars());
      int constIdx = getSubConstraintIdx(varIdx);
      if (getStatus(constIdx) == null) {
        checkStatus(constIdx);
      } else {
        int localIdx = ((constIdx == 0) ? varIdx : varIdx - offsets[constIdx - 1]);
        if (getStatus(constIdx) == Boolean.TRUE) {
          ((IntConstraint) constraints[constIdx]).awakeOnSup(localIdx);
        }
      }
    }
  }

  public void awakeOnBounds(int varIdx) throws ContradictionException {
    if (varIdx == getNbVars() - 1) {
      awakeOnNbFalse();
    } else {
      assert(varIdx < getNbVars());
      int constIdx = getSubConstraintIdx(varIdx);
      if (getStatus(constIdx) == null) {
        checkStatus(constIdx);
      } else {
        int localIdx = ((constIdx == 0) ? varIdx : varIdx - offsets[constIdx - 1]);
        if (getStatus(constIdx) == Boolean.TRUE) {
          ((IntConstraint) constraints[constIdx]).awakeOnInf(localIdx);
        }
      }
    }
  }

  public void awakeOnInst(int varIdx) throws ContradictionException {
    if (varIdx == getNbVars() - 1) {
      awakeOnNbFalse();
    } else {
      assert(varIdx < getNbVars());
      int constIdx = getSubConstraintIdx(varIdx);
      if (getStatus(constIdx) == null) {
        checkStatus(constIdx);
      } else {
        int localIdx = ((constIdx == 0) ? varIdx : varIdx - offsets[constIdx - 1]);
        if (getStatus(constIdx) == Boolean.TRUE) {
          ((IntConstraint) constraints[constIdx]).awakeOnInf(localIdx);
        }
      }
    }
  }

  public void awakeOnRem(int varIdx, int val) throws ContradictionException {
    if (varIdx == getNbVars() - 1) {
      awakeOnNbFalse();
    } else {
      assert(varIdx < getNbVars());
      int constIdx = getSubConstraintIdx(varIdx);
      if (getStatus(constIdx) == null) {
        checkStatus(constIdx);
      } else {
        int localIdx = ((constIdx == 0) ? varIdx : varIdx - offsets[constIdx - 1]);
        if (getStatus(constIdx) == Boolean.TRUE) {
          ((IntConstraint) constraints[constIdx]).awakeOnRem(localIdx, val);
        }
      }
    }
  }

// TODO: verifier le code en 1.328
  public void awake() throws ContradictionException {
    IntDomainVarImpl cardVar = getCardVar();
    int varIdx = getCardVarIndex();
    if (constrainOnInfNumber) {
      cardVar.updateInf(nbTrueStatus.get(), -1);
      {
        // TODO: check that -1 !!!!!
      }
      if (nbTrueStatus.get() == cardVar.getSup()) {
        if (constrainOnInfNumber) {
          cardVar.updateSup(nbConstraints, varIdx);
        }
        if (constrainOnSupNumber) {
          cardVar.updateInf(0, varIdx);
        }
        propagate();
      }
    }
  }

// propagates the status of the subconstraints onto the counter variable
  public void propagate() throws ContradictionException {
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      if (getStatus(constIdx) == null) {
        checkStatus(constIdx);
      }
    }
    awakeOnNbTrue();
    awakeOnNbFalse();
  }

  public boolean isSatisfied() {
    boolean result = true;
    int nbVal = getCardVar().getVal();
    int countSat = 0;
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      if (constraints[constIdx].isSatisfied()) {
        countSat++;
      }
    }
    if (constrainOnInfNumber) {
      result = (result && (nbVal <= countSat));
    }
    if (constrainOnSupNumber) {
      result = (result && (nbVal >= countSat));
    }
    return result;
  }

  public Boolean isEntailed() {
    IntDomainVarImpl nbVar = getCardVar();
    if ((nbTrueStatus.get() > nbVar.getSup()) ||
        (nbConstraints - nbFalseStatus.get() < nbVar.getInf())) {
      return Boolean.FALSE;
    } else {
      for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
        if (getStatus(constIdx) == null) {
          Boolean Bi = constraints[constIdx].isEntailed();
          if (Bi == Boolean.FALSE) {
            setStatus(constIdx, false);
            nbFalseStatus.set(nbFalseStatus.get() + 2);
          } else if (Bi == Boolean.TRUE) {
            setStatus(constIdx, true);
            nbTrueStatus.set(nbTrueStatus.get() + 2);
          }
        }
      }
      if ((constrainOnInfNumber && (nbTrueStatus.get() > nbVar.getInf())) ||
          (constrainOnSupNumber && (nbFalseStatus.get() < nbVar.getInf()))) {
        return Boolean.FALSE;
      } else if ((constrainOnInfNumber && (nbTrueStatus.get() <= nbVar.getInf())) &&
          (constrainOnSupNumber && (nbFalseStatus.get() >= nbVar.getInf()))) {
        return Boolean.TRUE;
      } else {
        return null;
      }
    }
  }

  public boolean isConsistent() {
    throw new UnsupportedOperationException();
  }

  public void awakeOnRemovals(int varIdx, IntIterator deltaDomain) throws ContradictionException {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
