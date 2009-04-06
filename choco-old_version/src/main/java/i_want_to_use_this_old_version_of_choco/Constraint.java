// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

public interface Constraint extends Entity, Cloneable {


  /**
   * <i>Network management:</i>
   * Get the number of variables involved in the constraint.
   */

  int getNbVars();

  /**
   * <i>Network management:</i>
   * Accessing the ith variable of a constraint.
   *
   * @param i index of the variable in the constraint
   */

  Var getVar(int i);

  /**
   * <i>Network management:</i>
   * Setting (or overwriting)  the ith variable of a constraint.
   *
   * @param i index of the variable in the constraint
   * @param v the variable (may be an IntDomainVar, SetVar, RealVar, ...
   */
  void setVar(int i, Var v);

  /**
   * <i>Semantic:</i>
   * Testing if the constraint is satisfied.
   * Note that all variables involved in the constraint must be
   * instantiated when this method is called.
   */

  boolean isSatisfied();

  /**
   * computes the constraint modelling the counter-opposite condition of this
   *
   * @return a new constraint (modelling the opposite condition)
   */
  AbstractConstraint opposite();

  /**
   * returns a copy of the constraint. This copy is a new object, may be a recursive copy in case
   * of composite constraints. The original and the copy share the same variables & plugins
   *
   * @return
   */
  // public Constraint copy();
  Object clone() throws CloneNotSupportedException;

  /**
   * tests the equivalence (logical equality of the conditions) between two constraints.
   * In particular whenever c1.equals(c2), then c1.isEquivalent(c2).
   *
   * @param compareTo the constraint to be compared to
   * @return true if and only if the constraints model the same logical condition.
   */
  boolean isEquivalentTo(Constraint compareTo);

  /**
   * computes the index of the i-th variable in the counter-opposite of the constraint
   *
   * @param i the index of the variable in the current constraint (this)
   * @return the index of the variable in the opposite constraint (this.opposite())
   */
  int getVarIdxInOpposite(int i);

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
