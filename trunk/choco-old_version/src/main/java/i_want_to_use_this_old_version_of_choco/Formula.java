// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

/**
 * a class that is used to represent a syntatic formula involving unknowns.
 * It is not a propagator (formulas have no behaviors, no semantic)
 * By defaut, an AbstractModeler creates formulas instead of constraints
 */
public class Formula extends AbstractEntity implements Constraint {
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

  public AbstractConstraint opposite() {
    throw new UnsupportedOperationException();
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public int getConstraintOperator() {
    return constraintOperator;
  }

  public boolean isEquivalentTo(Constraint compareTo) {
    boolean same = (this instanceof Formula); // TODO : too restrictive
    if (same) {
      same = same && (this.getConstraintOperator() == ((Formula) compareTo).getConstraintOperator());
      if (same) {
        same = same && (this.getNbVars() == compareTo.getNbVars());
        if (same) {
          for (int i = 0; i < this.getNbVars(); i++) {
            same = same && (this.getVar(i) == compareTo.getVar(i));
          }
          // TODO: on would still need to check that all parameters are the same
        }
      }
    }
    return same;
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

  public AbstractProblem getProblem() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String pretty() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
