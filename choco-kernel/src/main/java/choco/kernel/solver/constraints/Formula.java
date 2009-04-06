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
package choco.kernel.solver.constraints;

import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.Var;

/**
 * a class that is used to represent a syntatic formula involving unknowns.
 * It is not a propagator (formulas have no behaviors, no semantic)
 * By defaut, an AbstractModeler creates formulas instead of constraints
 */
public class Formula implements SConstraint {

  /**
   * The (optimization or decision) model to which the entity belongs.
   */

  public Solver solver;
  /** possible static values for the constraintOperator field */
  public static int EQUAL_XC = 0;
  public static int NOT_EQUAL_XC = 1;
  public static int GREATER_OR_EQUAL_XC = 2;
  public static int LESS_OR_EQUAL_XC = 3;

  public static int EQUAL_XYC = 4;
  public static int NOT_EQUAL_XYC = 5;
  public static int GREATER_OR_EQUAL_XYC = 6;
  public static int TIMES_XYZ = 12;

  public static int INT_LIN_COMB = 7;
  public static int OCCURRENCE = 8;
  public static int ALL_DIFFERENT = 9;
  public static int GLOBAL_CARDINALITY = 10;
  public static int NTH = 11;

  /**
   * this slots characterizes the type of formula being stored (the predicate/relation/operator)
   */
  public int constraintOperator;

  /**
   * storing the variables (IntVar, SetVar, ...) involved in the constraint
   */
  public Var[] variables;

  /**
   * storing the parameters of the constraint
   */
  public Object[] parameters;

  public Formula(Var v0, int c, int cop) {
    variables = new Var[]{v0};
    parameters = new Object[]{new Integer(c)};
    constraintOperator = cop;
  }

  public Formula(Var v0,  Var v1, int c, int cop) {
    variables = new Var[]{v0, v1};
    parameters = new Object[]{new Integer(c)};
    constraintOperator = cop;
  }

  public Formula(Var v0,  Var v1, Var v2, int cop) {
    variables = new Var[]{v0, v1, v2};
    parameters = new Object[]{};
    constraintOperator = cop;
  }

  public Formula(Var[] vars, int[] coeffs, int c1, int c2, int cop) {
    variables = vars;
    parameters = new Object[]{coeffs, new Integer(c1), new Integer(c2)};
    constraintOperator = cop;
  }

public Formula(Var[] vars, int[] coeffs, int c1, int c2, int c3, int cop) {
  variables = vars;
  parameters = new Object[]{coeffs, new Integer(c1), new Integer(c2), new Integer(c3)};
  constraintOperator = cop;
}

  public int getNbVars() {
    return variables.length;
  }

  public Var getVar(int i) {
    return variables[i];
  }

  public void setVar(int i, Var v) {
    variables[i] = v;
  }

  public boolean isSatisfied() {
    throw new UnsupportedOperationException();
  }

  public AbstractSConstraint opposite() {
    throw new UnsupportedOperationException();
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public int getConstraintOperator() {
    return constraintOperator;
  }

  public int getVarIdxInOpposite(int i) {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setConstraintIndex(int i, int idx) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public int getConstraintIdx(int idx) {
    throw new UnsupportedOperationException();
  }

    /**
   * Retrieves the solver of the entity
   */

  public Solver getSolver() {
    return solver;
  }

  public void setSolver(Solver solver) {
    this.solver = solver;
  }

    public String pretty() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

    public int getFineDegree(int idx) {
        return 1;
    }
}
