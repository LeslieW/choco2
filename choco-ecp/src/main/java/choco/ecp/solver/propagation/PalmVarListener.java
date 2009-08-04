//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.propagation;

import choco.kernel.solver.propagation.VarEventListener;


public interface PalmVarListener extends VarEventListener {

  /**
   * Method called when one of the variables is modified.
   *
   * @param idx      Index of the variable.
   * @param select   Type of modification (<code>PalmIntVar.INF</code>, <code>PalmIntVar.SUP</code>,
   *                 <code>PalmIntVar.VAL</code>).
   * @param newValue New value (or modified value).
   * @param oldValue Old value (or 0 if this a value removal).
   */

  public void updateDataStructuresOnConstraint(int idx, int select, int newValue, int oldValue);


  /**
   * Method called when one of the variables has a restored value.
   *
   * @param idx      Index of the variable.
   * @param select   Type of modification.
   * @param newValue New value (or modified value).
   * @param oldValue Old value (or 0 if this a value removal).
   */

  public void updateDataStructuresOnRestoreConstraint(int idx, int select, int newValue, int oldValue);

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
