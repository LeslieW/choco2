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

public abstract class AbstractUnSetConstraint extends AbstractSetConstraint {

  /**
   * The unique variable of the constraint.
   */

  public SetVar v0;


  /**
   * The index of the constraint among all listeners of its first (and unique)
   * variable.
   */

  public int cIdx0;

  public AbstractUnSetConstraint(SetVar v0) {
    super();
    this.v0 = v0;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Let v be the i-th var of c, records that c is the n-th constraint involving v.
   */

  public void setConstraintIndex(int i, int val) {
    if (i == 0)
      cIdx0 = val;
    else
      throw new Error("bug in setConstraintIndex i:" + i + " this: " + this);
  }


  /**
   * Returns the index of this listeners in the variable <code>idx</code>.
   *
   * @param idx Index of the variable.
   */

  public int getConstraintIdx(int idx) {
    if (idx == 0)
      return cIdx0;
    else
      return -1;
  }


  /**
   * Checks if all the variables of the constraint are instantiated.
   */

  public boolean isCompletelyInstantiated() {
    return v0.isInstantiated();
  }


  /**
   * Returns the number of variables: 1 for an unIntConstraint.
   */

  public int getNbVars() {
    return (1);
  }


  /**
   * Returns the variable number <code>i</code>. Here, <code>i</code>
   * should be 0.
   */

  public Var getVar(int i) {
    if (i == 0)
      return v0;
    else
      return null;
  }

  public void setVar(int i, Var v) {
    if (v instanceof SetVar) {
      if (i == 0)
        this.v0 = (SetVar) v;
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
    else
      return null;
  }

  public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
    int j = i;
    j++;
    int cidx0 = root.connectVar(v0, j, dynamicAddition);
    setConstraintIndex(0, cidx0);
    return j;
  }
}
