/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.variables.integer;

import choco.kernel.solver.constraints.integer.IntExp;
import choco.kernel.solver.variables.integer.IntVar;

/**
 * Implements linear terms: Sigma_i(a_i*X_i), where a_i are search coefficients,
 * and X_i are search domain variable
 */
public class IntTerm implements IntExp {

  /**
   * The coefficients
   */
  protected final int[] coefficients;

  /**
   * The variables
   */
  protected final IntVar[] variables;

  /**
   * number of variables involved in the term
   */
  protected int nbVars;

  /**
   * the integer constant involved in the term
   */
  protected int constant;

  /**
   * Constructor
   *
   * @param capacity number of variables that will be involved in the term
   */
  public IntTerm(int capacity) {
    coefficients = new int[capacity];
    variables = new IntVar[capacity];
    nbVars = capacity;
    constant = 0;
  }

  /**
   * Constructor by copy
   *
   * @param t1 the IntTerm to be copied
   */
  public IntTerm(IntTerm t1) {
    int capacity = t1.getSize();
    coefficients = new int[capacity];
    variables = new IntVar[capacity];
    nbVars = capacity;
    for (int i = 0; i < capacity; i++) {
      coefficients[i] = t1.getCoefficient(i);
      variables[i] = t1.getVariable(i);
    }
    constant = t1.constant;
  }

  /**
   * Pretty print of the expression
   */
  public String pretty() {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < coefficients.length; i++) {
      int coefficient = coefficients[i];
      IntVar variable = variables[i];
      if (i > 0) buf.append(" + ");
      buf.append(coefficient + "*" + variable);
    }
    buf.append(" + " + constant);
    return buf.toString();
  }

  /**
   * retrieve the array of coefficients
   *
   * @return the integer coefficients that are involved in the term
   */
  public int[] getCoefficients() {
    return coefficients;
  }

  /**
   * retrieve the array of variables
   *
   * @return the variables that are involved in the term
   */
  public IntVar[] getVariables() {
    return variables;
  }

  /**
   * retrieve the i-th coefficient
   *
   * @param index the index of the variable/coefficient in the expression
   * @return the coefficient
   */
  public int getCoefficient(int index) {
    return coefficients[index];
  }

  /**
   * retrieve the i-th variable
   *
   * @param index the index of the variable/coefficient in the expression
   * @return the coefficient
   */
  public IntVar getVariable(int index) {
    return variables[index];
  }

  /**
   * sets the i-th coefficient
   *
   * @param index the index of the variable/coefficient in the expression
   * @param coef  the coefficient
   */
  public void setCoefficient(int index, int coef) {
    coefficients[index] = coef;
  }

  /**
   * sets the i-th variable
   *
   * @param index the index of the variable/coefficient in the expression
   * @param var   the variable
   */
  public void setVariable(int index, IntVar var) {
    variables[index] = var;
  }

  /**
   * returns the term capacity
   *
   * @return the capacity that has been reserved for storing coefficients and varibales
   */
  public int getSize() {
    return nbVars;
  }

  /**
   * returns the integer constant involved in the linear term
   *
   * @return the value of the integer constant
   */
  public int getConstant() {
    return constant;
  }

  /**
   * sets the integer constant involved in the linear term
   *
   * @param constant the target value
   */
  public void setConstant(int constant) {
    this.constant = constant;
  }
}
