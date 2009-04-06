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
package choco.kernel.model;

import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.real.RealExp;
import choco.kernel.solver.variables.real.RealConstant;
import choco.kernel.solver.variables.real.RealVar;

/**
 * Modeler for real expressions.
 */
public interface RealModeler {


  /**
   * Builds an interval variable.
   * @param name name of the variable
   * @param inf lower bound of the variable
   * @param sup upper bound of the variable
   */
  public RealVar makeRealVar(String name, double inf, double sup);

  /***
   * Builds an anonnymous interval variable
   * @param inf lower bound of the variable
   * @param sup upper bound of the variable
   */
  public RealVar makeRealVar(double inf, double sup);

  /**
   * Builds an interval variable without any information about bounds
   */
  public RealVar makeRealVar(String name);

  /**
   * Makes an equation from an expression and a constantt interval. It is used by all methods building
   * constraints. This is  useful for subclassing this modeller  for another kind of model (like PaLM).
   * @param exp The expression
   * @param cst The interval this expression should be in
   */
  public SConstraint makeEquation(RealExp exp, RealConstant cst);

  /**
   * Eqality constraint.
   */
  public SConstraint eq(RealExp exp1, RealExp exp2);

  public SConstraint eq(RealExp exp, double cst);

  public SConstraint eq(double cst, RealExp exp);

  /**
   * Inferority constraint.
   */
  public SConstraint leq(RealExp exp1, RealExp exp2);

  public SConstraint leq(RealExp exp, double cst);

  public SConstraint leq(double cst, RealExp exp);

  /**
   * Superiority constraint.
   */
  public SConstraint geq(RealExp exp1,RealExp exp2);

  public SConstraint geq(RealExp exp, double cst);

  public SConstraint geq(double cst, RealExp exp) ;

  /**
   * Addition of two expressions.
   */
  public RealExp plus(RealExp exp1, RealExp exp2);

  /**
   * Substraction of two expressions.
   */
  public RealExp minus(RealExp exp1, RealExp exp2);

  /**
   * Multiplication of two expressions.
   */
  public RealExp mult(RealExp exp1, RealExp exp2);

  /**
   * Power of an expression.
   */
  public RealExp power(RealExp exp, int power);

  /**
   * Cosinus of an expression.
   */
  public RealExp cos(RealExp  exp);

  /**
   * Sinus of an expression.
   */
  public RealExp sin(RealExp exp);

  /**
   * Arounds a double d to <code>[d - epsilon, d + epilon]</code>.
   */
  public RealConstant around(double d);

  /**
   * Makes a constant interval from a double d ([d,d]).
   */
  public RealConstant cst(double d);

  /**
   * Makes a constant interval between two doubles [a,b].
   */
  public RealConstant cst(double a, double b);
}
