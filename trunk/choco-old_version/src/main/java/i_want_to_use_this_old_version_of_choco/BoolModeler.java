// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;


public interface BoolModeler {
  /**
   * Creates a disjunction from two constraints
   *
   * @param c1 the first branch
   * @param c2 the second branch
   * @return the disjunction constraint
   */
  public Constraint or(Constraint c1, Constraint c2);

  /**
   * Creates a conjunction from two constraints
   *
   * @param c1 the first branch
   * @param c2 the second branch
   * @return the conjunction constraint
   */
  public Constraint and(Constraint c1, Constraint c2);

  /**
   * Creates an implication from two constraints
   *
   * @param c1 the condition constraint
   * @param c2 the conclusion constraint
   * @return the implication constraint
   *         (implemented as a disjunction between the conclusion and the opposite of the condition)
   */
  public Constraint implies(Constraint c1, Constraint c2);

  /**
   * Creates a lazy implication from two constraints
   *
   * @param c1 the condition constraint
   * @param c2 the conclusion constraint
   * @return the implication constraint
   */
  public Constraint ifThen(Constraint c1, Constraint c2);

  /**
   * Creates an equivalence from two constraints
   *
   * @param c1 the first branch
   * @param c2 the second branch
   * @return the equivalence constraint
   */
  public Constraint ifOnlyIf(Constraint c1, Constraint c2);

  /**
   * Creates the logical opposite of a constraint
   *
   * @param c the constraint to be negated
   * @return the negation of the constraint
   */
  public Constraint not(Constraint c);
}
