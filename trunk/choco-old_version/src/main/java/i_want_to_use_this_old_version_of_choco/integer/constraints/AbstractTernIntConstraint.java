// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.integer.constraints;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;


/**
 * An abstract class for all implementations of (unary) listeners over one
 * search variable.
 */
public abstract class AbstractTernIntConstraint extends AbstractIntConstraint {

  /**
   * The first variable of the constraint.
   */
  protected IntDomainVar v0;

  /**
   * The second variable of the constraint.
   */
  protected IntDomainVar v1;

  /**
   * The third variable of the constraint.
   */
  protected IntDomainVar v2;

  /**
   * The index of the constraint among all listeners of its first variable.
   */
  protected int cIdx0;

  /**
   * The index of the constraint among all listeners of its second variable.
   */
  protected int cIdx1;

  /**
   * The index of the constraint among all listeners of its third variable.
   */
  protected int cIdx2;

  /**
   * Builds a ternary constraint with the specified variables.
   * @param x0 the first variable
   * @param x1 the second variable
   * @param x2 the third variable
   */
  public AbstractTernIntConstraint(final IntDomainVar x0, 
      final IntDomainVar x1, final IntDomainVar x2) {
    v0 = x0;
    v1 = x1;
    v2 = x2;
  }

  /**
   * Let v be the i-th var of c, 
   * records that c is the constraint n according to v.
   * @param i the variable index
   * @param val the constraint index according to the variable
   */
  public void setConstraintIndex(final int i, final int val) {
    if (i == 0) {
      cIdx0 = val;
    } else if (i == 1) {
      cIdx1 = val;
    } else if (i == 2) {
      cIdx2 = val;
    } else {
      throw new Error("bug in setConstraintIndex i:" + i + " this: " + this);
    }
  }

  /**
   * Returns the index of the constraint in the specified variable.
   * @param idx the variable index
   * @return the constraint index according to the variable
   */
  public int getConstraintIdx(final int idx) {
    if (idx == 0) {
      return cIdx0;
    } else if (idx == 1) {
      return cIdx1;
    } else if (idx == 2) {
      return cIdx2;
    } else {
      return -1;
    }
  }

  /**
   * Checks if all the variables are instantiated.
   * @return true if all variables are sintantiated
   */
  public boolean isCompletelyInstantiated() {
    return (v0.isInstantiated() && v1.isInstantiated() && v2.isInstantiated());
  }

  /**
   * Returns the number of variables.
   * @return the number of variables, here always 3.
   */
  public int getNbVars() {
    return 3;
  }

  /**
   * Gets the specified variable.
   * @param i the variable index
   * @return the variable with the specified index according to this constraint
   */
  public Var getVar(final int i) {
    if (i == 0) {
      return v0;
    } else if (i == 1) {
      return v1;
    } else if (i == 2) {
      return v2;
    } else {
      return null;
    }
  }

  /**
   * Sets the association between variable and index of this variable.
   * @param i the variable index
   * @param v the variable
   */
  public void setVar(final int i, final Var v) {
    if (v instanceof IntDomainVar) {
      if (i == 0) {
        this.v0 = (IntDomainVar) v;
      } else if (i == 1) {
        this.v1 = (IntDomainVar) v;
      } else if (i == 2) {
        this.v2 = (IntDomainVar) v;
      } else {
        throw new Error("BUG in CSP network management: " 
            + "too large index for setVar");
      }
    } else {
      throw new Error("BUG in CSP network management: " 
          + "wrong type of Var for setVar");
    }
  }

  /**
   * Propagation for the constraint awake var.
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain becomes empty or the
   * filtering algorithm infers a contradiction
   */
  public void propagate() throws ContradictionException {
    awakeOnVar(0);
    awakeOnVar(1);
    awakeOnVar(2);
  }

  /**
   * Gets the <code>i</code>th search valued variable.
   * @param i the variable index
   * @return the variable with index i
   */
  public IntDomainVar getIntVar(final int i) {
    if (i == 0) {
      return v0;
    } else if (i == 1) {
      return v1;
    } else if (i == 2) {
      return v2;
    } else {
      return null;
    }
  }

  /**
   * Assign indices to variables for this constraint but also for global
   * constraint if this constraint is included in a global boolean
   * meta-constraint.
   * @param root the global constraint
   * @param i the current available index for the global constraint
   * @param dynamicAddition states if the constraint is definitive
   * @return the new available index for the global constraint that is
   * i+3 here
   */
  public int assignIndices(final AbstractReifiedConstraint root,
      final int i, final boolean dynamicAddition) {
    int j = i;
    j++;
    int cidx0 = root.connectVar(v0, j, dynamicAddition);
    setConstraintIndex(0, cidx0);
    j++;
    int cidx1 = root.connectVar(v1, j, dynamicAddition);
    setConstraintIndex(1, cidx1);
    j++;
    int cidx2 = root.connectVar(v2, j, dynamicAddition);
    setConstraintIndex(2, cidx2);
    return j;
  }
}
