package i_want_to_use_this_old_version_of_choco.real.constraint;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.prop.VarEventListener;

/**
 * An interface for real event listener (like real constraints for instance).
 */
public interface RealListener extends VarEventListener {
  /**
   * Default propagation on improved lower bound: propagation on domain revision.
   */

  public void awakeOnInf(int idx) throws ContradictionException;


  /**
   * Default propagation on improved upper bound: propagation on domain revision.
   */

  public void awakeOnSup(int idx) throws ContradictionException;

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
