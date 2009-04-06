package i_want_to_use_this_old_version_of_choco.set.constraint;

import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;
import i_want_to_use_this_old_version_of_choco.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public abstract class AbstractBinSetConstraint extends AbstractSetConstraint {

  /**
   * The first variable of the constraint.
   */

  public SetVar v0;


  /**
   * The second variable of the constraint.
   */

  public SetVar v1;


  /**
   * The index of the constraint among all listeners of its first variable.
   */

  public int cIdx0;


  /**
   * The index of the constraint among all listeners of its second variable.
   */

  public int cIdx1;


  /**
   * Let v be the i-th var of c, records that c is the n-th constraint involving v.
   */

  public void setConstraintIndex(int i, int val) {
    if (i == 0)
      cIdx0 = val;
    else if (i == 1)
      cIdx1 = val;
    else
      throw new Error("bug in setConstraintIndex i:" + i + " this: " + this);
  }


  /**
   * Returns the index of this constraint for the specified variables.
   */

  public int getConstraintIdx(int idx) {
    if (idx == 0)
      return cIdx0;
    else if (idx == 1)
      return cIdx1;
    else
      return -1;
  }


  /**
   * Checks if all the variables are instantiated.
   */

  public boolean isCompletelyInstantiated() {
    return (v0.isInstantiated() && v1.isInstantiated());
  }


  /**
   * Returns the number of varibles.
   */

  public int getNbVars() {
    return (2);
  }


  /**
   * Returns the specified variable.
   */

  public Var getVar(int i) {
    if (i == 0)
      return v0;
    else if (i == 1)
      return v1;
    else
      return null;
  }

  public void setVar(int i, Var v) {
    if (v instanceof SetVar) {
      if (i == 0)
        this.v0 = (SetVar) v;
      else if (i == 1)
        this.v1 = (SetVar) v;
      else
        throw new Error("BUG in CSP network management: too large index for setVar");
    } else {
      throw new Error("BUG in CSP network management: wrong type of Var for setVar");
    }
  }

  /**
   * Gets the <code>i</code>th search valued variable.
   */

  public SetVar getSetVar(int i) {
    if (i == 0)
      return v0;
    else if (i == 1)
      return v1;
    else
      return null;
  }

  public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
    int j = i;
    j++;
    int cidx0 = root.connectVar(v0, j, dynamicAddition);
    setConstraintIndex(0, cidx0);
    j++;
    int cidx1 = root.connectVar(v1, j, dynamicAddition);
    setConstraintIndex(1, cidx1);
    return j;
  }
}
