// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.bool;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;

/**
 * @deprecated see Reifed package
 */
public abstract class AbstractLargeCompositeConstraint extends AbstractCompositeConstraint {

  /**
   * all subconstraints from the composition
   */
  protected AbstractConstraint[] constraints;

  /**
   * offsets[i] is the total number of variables in all previous terms constraints[0]...constraints[i-1]
   * Therefore it is the offset in the variable indexing of constraints[i]
   */
  protected int[] offsets;

  /**
   * the number of sub-constraints
   */
  protected int nbConstraints;

  /**
   * variables in the composition that are not in the subconstraints
   */
  protected IntDomainVarImpl[] additionalVars;

  /**
   * constraint indices (for the constraint network management) corresponding to the additional variables
   */
  protected int[] additionalIndices;

  public AbstractLargeCompositeConstraint(Constraint[] subConstraints) {
    this(subConstraints, null);
  }

  public AbstractLargeCompositeConstraint(Constraint[] subConstraints, IntDomainVar[] moreVars) {
    int nbc = subConstraints.length;
    constraints = new AbstractConstraint[nbc];
    for (int constIdx = 0; constIdx < nbc; constIdx++) {
      AbstractConstraint c = (AbstractConstraint) subConstraints[constIdx];
      constraints[constIdx] = c;
    }
    nbConstraints = nbc;
    int nbVars = 0;
    offsets = new int[nbc];
    for (int i = 0; i < nbc; i++) {
      nbVars += constraints[i].getNbVars();
      offsets[i] = nbVars;
    }
    int nbv = ((moreVars == null) ? 0 : moreVars.length);
    additionalVars = new IntDomainVarImpl[nbv];
    additionalIndices = new int[nbv];
    for (int j = 0; j < nbv; j++) {
      additionalVars[j] = (IntDomainVarImpl) moreVars[j];
      additionalIndices[j] = 0;
    }
    assert(nbVars + nbv == getNbVars());
  }


    public void setProblem(AbstractProblem problem) {
        super.setProblem(problem);
        for (int i = 0; i < constraints.length; i++) {
            constraints[i].setProblem(problem);
        }
    }

    public Object clone() throws CloneNotSupportedException {
    AbstractLargeCompositeConstraint newc = (AbstractLargeCompositeConstraint) super.clone();
    newc.constraints = new AbstractConstraint[this.constraints.length];
    System.arraycopy(this.constraints, 0, newc.constraints, 0, this.constraints.length);
    newc.offsets = new int[this.offsets.length];
    System.arraycopy(this.offsets, 0, newc.offsets, 0, this.offsets.length);
    newc.additionalVars = new IntDomainVarImpl[this.additionalVars.length];
    System.arraycopy(this.additionalVars, 0, newc.additionalVars, 0, this.additionalVars.length);
    newc.additionalIndices = new int[this.additionalIndices.length];
    System.arraycopy(this.additionalIndices, 0, newc.additionalIndices, 0, this.additionalIndices.length);
    return newc;
  }

  public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
    int j = i;
    for (int constIdx = 0; constIdx < this.nbConstraints; constIdx++) {
      int j0 = j;
      AbstractConstraint subc = constraints[constIdx];
      j = subc.assignIndices(root, j, dynamicAddition);
      assert(offsets[constIdx] == j - i);
    }
    assert(additionalIndices.length == additionalVars.length);
    for (int varIdx = 0; varIdx < additionalVars.length; varIdx++) {
      j++;
      int cidx = root.connectVar(additionalVars[varIdx], j, dynamicAddition);
      this.additionalIndices[varIdx] = cidx;
    }
    return j;
  }

  public int getNbVarsFromSubConst() {
    return offsets[offsets.length - 1];
  }

  public int getSubConstraintIdx(int varIdx) {
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      if (offsets[constIdx] > varIdx) {
        return constIdx;
      }
    }
    return -1;
  }

  protected int getLocalVarIndex(int varIdx, int constIdx) {
    return ((constIdx == 0) ? varIdx : varIdx - offsets[constIdx - 1]);
  }

  public int getVarIdxInOpposite(int i) {
    int constIdx = getSubConstraintIdx(i);
    if (constIdx == 0) {
      return constraints[0].getVarIdxInOpposite(i);
    } else {
      assert(constIdx < constraints.length);
      int off = offsets[constIdx - 1];
      return constraints[constIdx].getVarIdxInOpposite(i - off) + off;
    }
  }

  public void setConstraintIndex(int i, int idx) {
    int constIdx = getSubConstraintIdx(i);
    if ((constIdx == -1)) {
      int varIdx = i - offsets[offsets.length - 1];
      additionalIndices[varIdx] = idx;
    } else {
      int varIdx = (constIdx == 0) ? i : i - offsets[constIdx - 1];
      constraints[constIdx].setConstraintIndex(varIdx, idx);
    }
  }

  public int getConstraintIdx(int i) {
    int constIdx = getSubConstraintIdx(i);
    if ((constIdx == -1)) {
      int varIdx = i - offsets[offsets.length - 1];
      return additionalIndices[varIdx];
    } else {
      int varIdx = (constIdx == 0) ? i : i - offsets[constIdx - 1];
      return constraints[constIdx].getConstraintIdx(varIdx);
    }
  }

  public int getNbVars() {
    return offsets[nbConstraints - 1] + additionalVars.length;
  }

  public Var getVar(int i) {
    int constIdx = getSubConstraintIdx(i);
    int varIdx;

    if ((constIdx == -1)) {
      varIdx = i - offsets[nbConstraints - 1];
      return additionalVars[varIdx];
    } else if ((constIdx == 0)) {
      varIdx = i;
      return constraints[constIdx].getVar(varIdx);
    } else {
      varIdx = i - offsets[constIdx - 1];
      return constraints[constIdx].getVar(varIdx);
    }
  }

  public void setVar(int i, Var v) {
    int constIdx = getSubConstraintIdx(i);
    int varIdx;

    if ((constIdx == -1)) {
      varIdx = i - offsets[nbConstraints];
      additionalVars[varIdx] = (IntDomainVarImpl) v;
    } else if ((constIdx == 0)) {
      varIdx = i;
      constraints[constIdx].setVar(varIdx, v);
    } else {
      varIdx = i - offsets[constIdx - 1];
      constraints[constIdx].setVar(varIdx, v);
    }
  }

  public IntDomainVar getIntVar(int varIdx) {
    return (IntDomainVar) getVar(varIdx);
  }

  public boolean isCompletelyInstantiated() {
    boolean instantiated = true;
    for (int constIdx = 0; constIdx < nbConstraints; constIdx++) {
      if (!constraints[constIdx].isCompletelyInstantiated()) {
        instantiated = false;
        break;
      }
    }
    for (int varIdx = 0; varIdx < additionalVars.length; varIdx++) {
      if (!additionalVars[varIdx].isInstantiated()) {
        instantiated = false;
        break;
      }
    }
    return instantiated;
  }

  public Constraint getSubConstraint(int constIdx) {
    return constraints[constIdx];
  }

  public int getNbSubConstraints() {
    return nbConstraints;
  }
}
