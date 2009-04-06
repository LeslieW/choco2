// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.IntExp;
import i_want_to_use_this_old_version_of_choco.integer.IntVar;

/**
 * An interface for all methods related modelling constraint programs over search domains
 */
public interface IntModeler {

  /**
   * Creates an instantiated variable
   *
   * @param name the name of the variable
   * @param val  the value
   * @return the variable
   */
  public IntVar makeConstantIntVar(String name, int val);

  /**
   * Creates an unnamed instantiated variable
   *
   * @param val the value
   * @return the variable
   */
  public IntVar makeConstantIntVar(int val);

  /**
   * Creates a new search variable with an enumerated domain
   *
   * @param name the name of the variable
   * @param min  minimal allowed value (included in the domain)
   * @param max  maximal allowed value (included in the domain)
   * @return the variable
   */
  public IntDomainVar makeEnumIntVar(String name, int min, int max);

  /**
   * Creates a new search variable with an interval domain
   *
   * @param name the name of the variable
   * @param min  minimal allowed value (included in the domain)
   * @param max  maximal allowed value (included in the domain)
   * @return the variable
   */
  public IntDomainVar makeBoundIntVar(String name, int min, int max);

  /**
   * Creates a one dimensional array of integer variables
   *
   * @param name the name of the array (a prefix shared by all individual IntVars)
   * @param dim  the number of entries
   * @param min  the minimal domain value for all variables in the array
   * @param max  the maximal domain value for all variables in the array
   */
  public IntDomainVar[] makeBoundIntVarArray(String name, int dim, int min, int max);

  /**
   * Creates a one dimensional array of integer variables
   *
   * @param name the name of the array (a prefix shared by all individual IntVars)
   * @param dim1 the number of entries for the first index
   * @param dim2 the number of entries for the second index
   * @param min  the minimal domain value for all variables in the array
   * @param max  the maximal domain value for all variables in the array
   */
  public IntDomainVar[][] makeBoundIntVarArray(String name, int dim1, int dim2, int min, int max);

  /**
   * Creates a simple linear term from one coefficient and one variable
   *
   * @param a the coefficient
   * @param x the variable
   * @return the term
   */
  public IntExp mult(int a, IntExp x);

  /**
   * Adding two search expressions one to another
   *
   * @param t1 first expression
   * @param t2 second expression
   * @return the resulting expression (a fresh one)
   */
  public IntExp plus(IntExp t1, IntExp t2);

  /**
   * Adding an search constant to an expression
   *
   * @param t1 the expression
   * @param c  the search constant
   * @return the resulting expression (a fresh one)
   */
  public IntExp plus(IntExp t1, int c);

  /**
   * Adding an search constant to an expression
   *
   * @param t1 the expression
   * @param c  the search constant
   * @return the resulting expression (a fresh one)
   */
  public IntExp plus(int c, IntExp t1);

  /**
   * Subtracting two search expressions one from another
   *
   * @param t1 first expression
   * @param t2 second expression
   * @return the resulting expression (a fresh one)
   */
  public IntExp minus(IntExp t1, IntExp t2);

  /**
   * Subtracting an search constant from an expression
   *
   * @param t1 the expression
   * @param c  the search constant
   * @return the resulting expression (a fresh one)
   */
  public IntExp minus(IntExp t1, int c);

  /**
   * Subtracting an expression from an search
   *
   * @param t1 the expression
   * @param c  the search constant
   * @return the resulting expression (a fresh one)
   */
  public IntExp minus(int c, IntExp t1);

  /**
   * Building a term from a scalar product of coefficients and variables
   *
   * @param lc the array of coefficients
   * @param lv the array of variables
   * @return the term
   */
  public IntExp scalar(int[] lc, IntVar[] lv);

  /**
   * Building a term from a scalar product of coefficients and variables
   *
   * @param lv the array of variables
   * @param lc the array of coefficients
   * @return the term
   */
  public IntExp scalar(IntVar[] lv, int[] lc);

  /**
   * Building a term from a sum of integer expressions
   *
   * @param lv the array of integer expressions
   * @return the term
   */
  public IntExp sum(IntExp[] lv);

  /**
   * Creates a constraint by stating that a variable is greater or equal than a constant
   *
   * @param x the first search expression
   * @param y the second search expression
   * @return the linear equality constraint
   */
  public Constraint geq(IntExp x, IntExp y);

  /**
   * Creates a constraint by stating that a variable is greater or equal than a constant
   *
   * @param x the search expression
   * @param c the search constant
   * @return the linear equality constraint
   */
  public Constraint geq(IntExp x, int c);

  /**
   * Creates a constraint by stating that a variable is greater or equal than a constant
   *
   * @param x the search expression
   * @param c the search constant
   * @return the linear equality constraint
   */
  public Constraint geq(int c, IntExp x);

  /**
   * Creates a constraint by stating that a variable is strictly than a constant
   *
   * @param x the first search expression
   * @param y the second search expression
   * @return the linear equality constraint
   */
  public Constraint gt(IntExp x, IntExp y);

  /**
   * Creates a constraint by stating that a variable is strictly than a constant
   *
   * @param x the search expression
   * @param c the search constant
   * @return the linear equality constraint
   */
  public Constraint gt(IntExp x, int c);

  /**
   * Creates a constraint by stating that a variable is strictly than a constant
   *
   * @param x the search expression
   * @param c the search constant
   * @return the linear equality constraint
   */
  public Constraint gt(int c, IntExp x);

  /**
   * Creates a constraint by stating that integer expression is less or equal than a constant
   *
   * @param x the search expression
   * @param c the search constant
   * @return the linear equality constraint
   */
  public Constraint leq(IntExp x, int c);

  /**
   * Creates a constraint by stating that a constant is less or equal than an integer expression
   *
   * @param x the search expression
   * @param c the search constant
   * @return the linear equality constraint
   */
  public Constraint leq(int c, IntExp x);

  /**
   * Creates a constraint by stating that an integer expression is less or equal than another one
   *
   * @param x the first search expression
   * @param y the second search expression
   * @return the linear equality constraint
   */
  public Constraint leq(IntExp x, IntExp y);

  /**
   * Creates a constraint by stating that integer expression is strictly less than a constant
   *
   * @param x the search expression
   * @param c the search constant
   * @return the linear equality constraint
   */
  public Constraint lt(IntExp x, int c);

  /**
   * Creates a constraint by stating that a constant is strictly less than an integer expression
   *
   * @param x the search expression
   * @param c the search constant
   * @return the linear equality constraint
   */
  public Constraint lt(int c, IntExp x);

  /**
   * Creates a constraint by stating that an integer expression is strictly less than another one
   *
   * @param x the first search expression
   * @param y the second search expression
   * @return the linear equality constraint
   */
  public Constraint lt(IntExp x, IntExp y);

  /**
   * Creates a constraint by stating that a term is equal than a constant
   *
   * @param x the first search expression
   * @param y the second search expression
   * @return the linear equality constraint
   */
  public Constraint eq(IntExp x, IntExp y);

  /**
   * Creates a constraint by stating that a term is equal than a constant
   *
   * @param x the search expression
   * @param c the search constant
   * @return the linear equality constraint
   */
  public Constraint eq(IntExp x, int c);

  /**
   * Creates a constraint by stating that a term is equal than a constant
   *
   * @param x the search expression
   * @param c the search constant
   * @return the linear equality constraint
   */
  public Constraint eq(int c, IntExp x);

  /**
   * Creates a constraint by stating that a term is not equal than a constant
   *
   * @param x the search expression
   * @param c the search constant
   * @return the linear disequality constraint
   */
  public Constraint neq(IntExp x, int c);

  /**
   * Creates a constraint by stating that a term is not equal than a constant
   *
   * @param x the search expression
   * @param c the search constant
   * @return the linear disequality constraint
   */
  public Constraint neq(int c, IntExp x);

  /**
   * Creates a constraint by stating that two term are different
   *
   * @param x the first variable
   * @param y the second variale
   * @return the linear disequality constraint
   */
  public Constraint neq(IntExp x, IntExp y);

  /**
     * Creates a constraint by stating that X*Y=Z
     *
     * @param x the first operand of the multiplication
     * @param y the second operand of the multiplication
     * @param z the result of the multiplication
     * @return the multiplication constraint
     */
    public Constraint times(IntVar x, IntVar y, IntVar z);

  /**
   * sets the optimization mode to minimization and sets the objective function
   *
   * @param obj the variable to be minimized
   */
  public void setMinimizationObjective(IntVar obj);

  /**
   * sets the optimization mode to maximization and sets the objective function
   *
   * @param obj the variable to be maximized
   */
  public void setMaximizationObjective(IntVar obj);
}
