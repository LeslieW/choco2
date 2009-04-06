package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.AbstractEntity;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Entity;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

/**
 * a struct-like class implementing (IntVar, int) pairs
 * (useful for binary branchings that assign a value to a variable or remove
 * that same value from the variable domain
 */
public final class IntVarValPair extends AbstractEntity implements Entity {
    public final IntDomainVar var;
    public final int val;

    public IntVarValPair(IntDomainVar var, int val) {
      this.var = var;
      this.val = val;
    }

    public AbstractProblem getProblem() {
        return var.getProblem();
    }

  public String pretty() {
    return "(" + var + "," + val + ")";
  }

  public String toString() {
    return "(" + var + "," + val + ")";
  }

}
