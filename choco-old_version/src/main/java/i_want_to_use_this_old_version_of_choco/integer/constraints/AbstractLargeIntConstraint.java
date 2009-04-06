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
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;

/**
 * An abstract class for all implementations of listeners over many search variables.
 */
public abstract class AbstractLargeIntConstraint extends AbstractIntConstraint {

  /**
   * The list of variables of the constraint.
   */

  public IntDomainVar[] vars;


  /**
   * The list, containing, for each variable, the index of the constraint among all
   * its incident listeners.
   */

  public int[] cIndices;

  /**
   * The search constant attached to the constraint.
   */

  public int cste;

  /**
   * constructor: allocates the data util for n variables
   *
   * @param n the number of variables involved in the constraint
   * @deprecated use AbstractLargeIntConstraint(IntDomainVar[] vars) instead
   */
  public AbstractLargeIntConstraint(int n) {
    vars = new IntDomainVarImpl[n];
    cIndices = new int[n];
  }

  public AbstractLargeIntConstraint(IntDomainVar[] vars) {
    this.vars = new IntDomainVar[vars.length];
    System.arraycopy(vars, 0, this.vars, 0, vars.length);
    cIndices = new int[vars.length];
  }

  public Object clone() throws CloneNotSupportedException {
    AbstractLargeIntConstraint newc = (AbstractLargeIntConstraint) super.clone();
    newc.vars = new IntDomainVar[this.vars.length];
    System.arraycopy(this.vars, 0, newc.vars, 0, this.vars.length);
    newc.cIndices = new int[this.cIndices.length];
    System.arraycopy(this.cIndices, 0, newc.cIndices, 0, this.cIndices.length);
    return newc;
  }

  /**
   * Let <i>v</i> be the <i>i</i>-th var of <i>c</i>, records that <i>c</i> is the
   * <i>n</i>-th constraint involving <i>v</i>.
   */

  public void setConstraintIndex(int i, int val) {
    if (i >= 0 && i < vars.length)
      cIndices[i] = val;
    else
      throw new Error("bug in setConstraintIndex i:" + i + " this: " + this);
  }


  /**
   * Returns the index of the constraint in the specified variable.
   */

  public int getConstraintIdx(int i) {
    if (i >= 0 && i < vars.length)
      return cIndices[i];
    else
      return -1;
  }


  /**
   * Checks wether all the variables are instantiated.
   */

  public boolean isCompletelyInstantiated() {
    int nVariables = vars.length;
    for (int i = 0; i < nVariables; i++) {
      if (!(vars[i].isInstantiated()))
        return false;
    }
    return true;
  }


  /**
   * Returns the number of variables.
   */

  public int getNbVars() {
    return vars.length;
  }


  /**
   * Returns the <code>i</code>th variable.
   */

  public Var getVar(int i) {
    if (i >= 0 && i < vars.length)
      return vars[i];
    else
      return null;
  }

  public void setVar(int i, Var v) {
    if (v instanceof IntDomainVar) {
      if (i >= 0 && i < vars.length)
        this.vars[i] = (IntDomainVar) v;
      else {
        throw new Error("BUG in CSP network management: too large index for setVar");
      }
    } else {
      throw new Error("BUG in CSP network management: wrong type of Var for setVar");
    }
  }


  /**
   * Propagates the constraint awake events.
   *
   * @throws ContradictionException
   */

  public void propagate() throws ContradictionException {
    for (int i = 0; i < vars.length; i++)
      awakeOnVar(i);
  }

  /**
   * Gets the <code>i</code>th search valued variable.
   */

  public IntDomainVar getIntVar(int i) {
    if (i >= 0 && i < getNbVars())
      return this.vars[i];
    else
      return null;
  }

  public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
    int j = i;
    for (int k = 0; k < getNbVars(); k++) {
      j++;
      int cidx = root.connectVar(vars[k], j, dynamicAddition);
      setConstraintIndex(k, cidx);
    }
    return j;
  }
}
