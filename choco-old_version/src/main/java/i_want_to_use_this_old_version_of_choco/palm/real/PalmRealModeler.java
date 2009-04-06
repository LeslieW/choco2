package i_want_to_use_this_old_version_of_choco.palm.real;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.palm.real.constraints.PalmEquation;
import i_want_to_use_this_old_version_of_choco.palm.real.exp.PalmRealIntervalConstant;
import i_want_to_use_this_old_version_of_choco.palm.real.exp.PalmRealMinus;
import i_want_to_use_this_old_version_of_choco.palm.real.exp.PalmRealMult;
import i_want_to_use_this_old_version_of_choco.palm.real.exp.PalmRealPlus;
import i_want_to_use_this_old_version_of_choco.real.RealExp;
import i_want_to_use_this_old_version_of_choco.real.RealModeler;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.exp.RealIntervalConstant;

import java.util.HashSet;
import java.util.Set;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 24 juin 2004
 */
public class PalmRealModeler extends RealModeler {
  public PalmRealModeler(Problem pb) {
    super(pb);
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
    RealVar[] tmpVars = (RealVar[]) collectedVars.toArray();
    return new PalmEquation(problem, tmpVars, exp, cst);

  }

  /**
   * Addition of two expressions.
   */
  public RealExp plus(RealExp exp1, RealExp exp2) {
    return new PalmRealPlus(problem, exp1, exp2);
  }

  /**
   * Substraction of two expressions.
   */
  public RealExp minus(RealExp exp1, RealExp exp2) {
    return new PalmRealMinus(problem, exp1, exp2);
  }

  /**
   * Multiplication of two expressions.
   */
  public RealExp mult(RealExp exp1, RealExp exp2) {
    return new PalmRealMult(problem, exp1, exp2);
  }

  /**
   * Power of an expression.
   */
  public RealExp power(RealExp exp, int power) {
    if (power == 2) {
      return mult(exp, exp);
    }
    throw new UnsupportedOperationException();
  }

  /**
   * Cosinus of an expression.
   */
  public RealExp cos(RealExp  exp) {
    throw new UnsupportedOperationException();
  }

  /**
   * Sinus of an expression.
   */
  public RealExp sin(RealExp exp) {
    throw new UnsupportedOperationException();
  }

  /**
   * Makes a constant interval from a double d ([d,d]).
   */
  public RealIntervalConstant cst(double d) {
    return new PalmRealIntervalConstant(d, d);
  }

  /**
   * Makes a constant interval between two doubles [a,b].
   */
  public RealIntervalConstant cst(double a, double b) {
    return new PalmRealIntervalConstant(a, b);
  }
}
