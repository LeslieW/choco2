// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.bool;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * An abstract class for all implementations of listeners over search variables.
 * @deprecated see Reified package
 */
public abstract class AbstractCompositeConstraint extends AbstractConstraint implements CompositeConstraint {

  public void awakeOnBounds(int varIndex) throws ContradictionException {
    awakeOnInf(varIndex);
    awakeOnSup(varIndex);
  }

  public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
    if (deltaDomain != null) {
      for (; deltaDomain.hasNext();) {
        int val = deltaDomain.next();
        awakeOnRem(idx, val);
      }
    } else {
      awakeOnVar(idx);
    }
  }

  public int getGlobalVarIndex(Constraint subConstraint, int localVarIdx) {
    AbstractVar v = (AbstractVar) subConstraint.getVar(localVarIdx);
    int constIdx = subConstraint.getConstraintIdx(localVarIdx);
    return v.getVarIndex(constIdx);
  }

  public void addListener(boolean dynamicAddition) {
    //assignIndices(this, -1, dynamicAddition);
    active = this.getProblem().getEnvironment().makeBool(true);
    if (this.hook != null) this.hook.addListener();
  }

	public boolean isSatisfied(int[] tuple) {
		throw new Error(this + " needs to implement isSatisfied(int[] tuple) to be embedded in reified constraints");
	}

}