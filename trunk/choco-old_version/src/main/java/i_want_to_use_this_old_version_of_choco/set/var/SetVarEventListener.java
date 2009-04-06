package i_want_to_use_this_old_version_of_choco.set.var;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.prop.VarEventListener;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 6 juin 2004
 * Time: 14:08:38
 * To change this template use File | Settings | File Templates.
 */
public interface SetVarEventListener extends VarEventListener {
  /**
   * Default propagation on kernel modification: propagation on adding a value to the kernel.
   */
  public void awakeOnKer(int varIdx, int x) throws ContradictionException;


  /**
   * Default propagation on enveloppe modification: propagation on removing a value from the enveloppe.
   */
  public void awakeOnEnv(int varIdx, int x) throws ContradictionException;


  /**
   * Default propagation on instantiation.
   */
  public void awakeOnInst(int varIdx) throws ContradictionException;

  /**
   * <i>Network management:</i>
   * Storing that among all listeners linked to the i-th variable of c,
   * this (the current constraint) is found at index idx.
   *
   * @param i   index of the variable in the constraint
   * @param idx index of the constraint in the among all listeners linked to that variable
   */

  void setConstraintIndex(int i, int idx);

  /**
   * <i>Network management:</i>
   * Among all listeners linked to the idx-th variable of c,
   * find the index of constraint c.
   *
   * @param idx index of the variable in the constraint
   */

  int getConstraintIdx(int idx);
}
