package i_want_to_use_this_old_version_of_choco.real;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.real.constraint.Equation;
import i_want_to_use_this_old_version_of_choco.real.exp.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Modeler for real expressions.
 */
public class RealModeler {
  /**
   * The problem this modler should build classes for.
   */
  protected Problem problem;

  /**
   * Builds a modeler for the specified problem.
   */
  public RealModeler(Problem problem) {
    this.problem = problem;
  }

  /**
   * Builds an interval variable.
   * @param name name of the variable
   * @param inf lower bound of the variable
   * @param sup upper bound of the variable
   */
  public RealVar makeRealVar(String name, double inf, double sup) {
    return problem.makeRealVar(name, inf, sup);
  }

  /***
   * Builds an anonnymous interval variable
   * @param inf lower bound of the variable
   * @param sup upper bound of the variable
   */
  public RealVar makeRealVar(double inf, double sup) {
    return makeRealVar("", inf, sup);
  }

  /**
   * Builds an interval variable without any information about bounds
   */
  public RealVar makeRealVar(String name) {
    return makeRealVar(name, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
  }

  /**
   * Makes an equation from an expression and a constantt interval. It is used by all methods building
   * constraints. This is  useful for subclassing this modeller  for another kind of problem (like PaLM).
   * @param exp The expression
   * @param cst The interval this expression should be in
   */
  public Constraint makeEquation(RealExp exp, RealIntervalConstant cst) {
    // Collect the variables
    Set collectedVars = new HashSet();
    exp.collectVars(collectedVars);
    RealVar[] tmpVars = new RealVar[0];
    tmpVars = (RealVar[]) collectedVars.toArray(tmpVars);
    return new Equation(problem, tmpVars, exp, cst);
  }

  /**
   * Eqality constraint.
   */
  public Constraint eq(RealExp exp1, RealExp exp2) {
    if (exp1 instanceof RealIntervalConstant) {
      return makeEquation(exp2, (RealIntervalConstant) exp1);
    } else if (exp2 instanceof RealIntervalConstant) {
      return makeEquation(exp1, (RealIntervalConstant) exp2);
    } else {
      return makeEquation(minus(exp1, exp2), cst(0.0));
    }
  }

  public Constraint eq(RealExp exp, double cst) {
    return makeEquation(exp, cst(cst));
  }

  public Constraint eq(double cst, RealExp exp) {
    return makeEquation(exp, cst(cst));
  }

  /**
   * Inferority constraint.
   */
  public Constraint leq(RealExp exp1, RealExp exp2) {
    if (exp1 instanceof RealIntervalConstant) {
      return makeEquation(exp2, cst(exp1.getInf(), Double.POSITIVE_INFINITY));
    } else if (exp2 instanceof RealIntervalConstant) {
      return makeEquation(exp1, cst(Double.NEGATIVE_INFINITY, exp2.getSup()));
    } else {
      return makeEquation(minus(exp1, exp2), cst(Double.NEGATIVE_INFINITY, 0.0));
    }
  }

  public Constraint leq(RealExp exp, double cst) {
    return makeEquation(exp, cst(Double.NEGATIVE_INFINITY, cst));
  }

  public Constraint leq(double cst, RealExp exp) {
    return makeEquation(exp, cst(cst, Double.POSITIVE_INFINITY));
  }

  /**
   * Superiority constraint.
   */
  public Constraint geq(RealExp exp1,RealExp exp2) {
    return leq(exp2, exp1);
  }

  public Constraint geq(RealExp exp, double cst) {
    return leq (cst, exp);
  }

  public Constraint geq(double cst, RealExp exp) {
    return leq(exp, cst);
  }

  /**
   * Addition of two expressions.
   */
  public RealExp plus(RealExp exp1, RealExp exp2) {
    return new RealPlus(problem, exp1, exp2);
  }

  /**
   * Substraction of two expressions.
   */
  public RealExp minus(RealExp exp1, RealExp exp2) {
    return new RealMinus(problem, exp1, exp2);
  }

  /**
   * Multiplication of two expressions.
   */
  public RealExp mult(RealExp exp1, RealExp exp2) {
    return new RealMult(problem, exp1, exp2);
  }

  /**
   * Power of an expression.
   */
  public RealExp power(RealExp exp, int power) {
    return new RealIntegerPower(problem, exp, power);
  }

  /**
   * Cosinus of an expression.
   */
  public RealExp cos(RealExp  exp) {
    return new RealCos(problem, exp);
  }

  /**
   * Sinus of an expression.
   */
  public RealExp sin(RealExp exp) {
    return new RealSin(problem, exp);
  }

  /**
   * Arounds a double d to <code>[d - epsilon, d + epilon]</code>.
   */
  public RealIntervalConstant around(double d) {
    return cst(RealMath.prevFloat(d), RealMath.nextFloat(d));
  }

  /**
   * Makes a constant interval from a double d ([d,d]).
   */
  public RealIntervalConstant cst(double d) {
    return new RealIntervalConstant(d, d);
  }

  /**
   * Makes a constant interval between two doubles [a,b].
   */
  public RealIntervalConstant cst(double a, double b) {
    return new RealIntervalConstant(a, b);
  }
}
