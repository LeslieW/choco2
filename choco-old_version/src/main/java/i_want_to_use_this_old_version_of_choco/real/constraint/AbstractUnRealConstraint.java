package i_want_to_use_this_old_version_of_choco.real.constraint;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;

public abstract class AbstractUnRealConstraint extends AbstractConstraint implements RealConstraint {
  /**
   * The unique variable of the constraint.
   */
  protected RealVar v0;

  /**
   * The index of this constraints w.r.t. the variable v0.
   */
  protected int cIdx0;

  /**
   * Returns the only one variable if i=0, null otherwise.
   */
  public RealVar getRealVar(int i) {
    if (i == 0)
      return v0;
    else
      return null;
  }

  /**
   * Returns the number of real variables.
   */
  public int getRealVarNb() {
    return 1;
  }

  /**
   * Let v0 be the i-th var of c, records that c is the idx-th constraint involving v0.
   */
  public void setConstraintIndex(int i, int idx) {
    if (i == 0)
      cIdx0 = idx;
    else
      throw new Error("bug in setConstraintIndex i:" + i + " this: " + this);
  }

  /**
   * Returns the index of this constraints in all constraints involving v0.
   */
  public int getConstraintIdx(int idx) {
    if (idx == 0)
      return cIdx0;
    else
      return -1;
  }

  /**
   * Returns the number of variables.
   */
  public int getNbVars() {
    return 1;
  }

  /**
   * Returns the only one variable v0 if i==0, null otherwise.
   */
  public Var getVar(int i) {
    if (i == 0)
      return v0;
    else
      return null;
  }

  public void setVar(int i, Var v) {
    if (v instanceof RealVar) {
      if (i == 0)
        this.v0 = (RealVar) v;
      throw new Error("BUG in CSP network management: too large index for setVar");
    } else {
      throw new Error("BUG in CSP network management: wrong type of Var for setVar");
    }
  }

  /**
   * Checks if the only one variable of the constraint is instantiated.
   */
  public boolean isCompletelyInstantiated() {
    return v0.isInstantiated();
  }

  public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
    int j = i;
    j++;
    int cidx = root.connectVar(v0, j, dynamicAddition);
    setConstraintIndex(0, cidx);
    return j;
  }
}
