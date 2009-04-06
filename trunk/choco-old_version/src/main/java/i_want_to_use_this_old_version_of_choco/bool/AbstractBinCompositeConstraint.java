// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.bool;

import i_want_to_use_this_old_version_of_choco.*;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;

/**
 * Abstract implementation of a composite constraint (like boolean ones)
 * involving two sub-constraints.
 * @deprecated see Reifed package
 */
public abstract class AbstractBinCompositeConstraint
    extends AbstractCompositeConstraint {
  /**
   * The first sub-constraint of the composition.
   */
  protected AbstractConstraint const0;
  
  /**
   * The second sub-constraint of the composition.
   */
  protected AbstractConstraint const1;
  
  /**
   * The number of variables in the first sub-constraint.
   * Therefore the offset in the numbering of the variables
   * from the second sub-constraint.
   */
  protected int offset = 0;


    public void setProblem(AbstractProblem problem) {
        super.setProblem(problem);
        const0.setProblem(problem);
        const1.setProblem(problem);
    }

    /**
   * Builds a new binary composite constraint with the two specified
   * sub-constraints.
   * @param c1 the first sub-constraint
   * @param c2 the second sub-constraint
   */
  public AbstractBinCompositeConstraint(final AbstractConstraint c1,
      final AbstractConstraint c2) {
    const0 = c1;
    const1 = c2;
    offset = const0.getNbVars();
  }
  
  /**
   * Builds a copy of this constraint.
   * @return a copy of this constraint
   * @throws CloneNotSupportedException if an problem occurs when cloning
   * elements pf this constraint
   */
  public Object clone() throws CloneNotSupportedException {
    AbstractBinCompositeConstraint newc =
        (AbstractBinCompositeConstraint) super.clone();
    newc.const0 = (AbstractConstraint) this.const0.clone();
    newc.const1 = (AbstractConstraint) this.const1.clone();
    return newc;
  }
  
  /**
   * Assigns indices to variables for the global constraint involving
   * this one.
   * @param root the global constraint including this one
   * @param i the first available index
   * @param dynamicAddition states if the constraint is added definitively
   * @return the next available index for the global constraint
   */
  public int assignIndices(final AbstractReifiedConstraint root,
      final int i, final boolean dynamicAddition) {
    int j = i;
    j = ((Propagator) const0).assignIndices(root, j, dynamicAddition);
    this.offset = j - i;
    j = ((Propagator) const1).assignIndices(root, j, dynamicAddition);
    return j;
  }
  
  /**
   * <i>Network management:</i>
   * Accessing the i-th search variable of a constraint.
   * @param varIdx index of the variable among all search variables 
   * in the constraint. Numbering start from 0 on.
   * @return the variable, or null when no such variable is found
   */
  
  public IntDomainVar getIntVar(final int varIdx) {
    return (IntDomainVar) getVar(varIdx);
  }
  
  /**
   * Returns the index of the sub-constraint involving the variable
   * varIdx.
   * @param varIdx the variable index
   * @return 0 if this is the first sub-constraint, 1 else
   */
  public int getSubConstraintIdx(final int varIdx) {
    if (varIdx < offset) {
      return 0;
    } else {
      return 1;
    }
  }
  
  /**
   * Returns the variable indx in the opposite constraint.
   * @param i the variable index
   * @return the index in the opposite constraint
   */
  public int getVarIdxInOpposite(final int i) {
    int constIdx = getSubConstraintIdx(i);
    if (constIdx == 0) {
      return const0.getVarIdxInOpposite(i);
    } else {
      assert constIdx == 1;
      return const1.getVarIdxInOpposite(i - offset) + offset;
    }
  }
  
  /**
   * Determines the number of variables, that is the sum of all variables
   * in sub-constraints.
   * @return the number of variables
   */
  public int getNbVars() {
    return const0.getNbVars() + const1.getNbVars();
  }
  
  /**
   * Accesses the variable i.
   * @param i the index of the variable
   * @return the requested variable
   */
  public Var getVar(final int i) {
    return ((i < offset) ? const0.getVar(i) : const1.getVar(i - offset));
  }
  
  /**
   * Sets the variable i.
   * @param i the variable index
   * @param v the variable
   */
  public void setVar(final int i, final Var v) {
    if (i < offset) {
      const0.setVar(i, v);
    } else {
      const1.setVar(i - offset, v);
    }
  }
  
  /**
   * Checks if all variables are instantiated, that if sub-constraints
   * variables are instantiated.
   * @return true if all variables are instantiated
   */
  public boolean isCompletelyInstantiated() {
    return (const0.isCompletelyInstantiated() 
    && const1.isCompletelyInstantiated());
  }
  
  /**
   * Returns the constraint index according to the variable i.
   * @param i the variable index
   * @return this constraint index according to the variable
   */
  public int getConstraintIdx(final int i) {
    return ((i < offset) ? const0.getConstraintIdx(i)
    : const1.getConstraintIdx(i - offset));
  }
  
  /**
   * Sets the constraint index according to the variable i.
   * @param i the variable index
   * @param idx the requested constraint index
   */
  public void setConstraintIndex(final int i, final int idx) {
    if (i < offset) {
      const0.setConstraintIndex(i, idx);
    } else {
      const1.setConstraintIndex(i - offset, idx);
    }
  }
  
  /**
   * Accesses the sub-constraints.
   * @param constIdx the constraint index (0 or 1 here)
   * @return the requested constraint
   */
  public Constraint getSubConstraint(final int constIdx) {
    return ((constIdx == 0) ? const0 : const1);
  }
  
  /**
   * Returns the number of direct sub-constraints (2 here since this is a
   * binary composite constraint).
   * @return the number of direct sub-constraints
   */
  public int getNbSubConstraints() {
    return 2;
  }
}
