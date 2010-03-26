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
package choco.cp.solver.constraints.real.exp;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.real.RealExp;
import choco.kernel.solver.constraints.real.exp.AbstractRealBinTerm;
import choco.kernel.solver.variables.real.RealInterval;
import choco.kernel.solver.variables.real.RealMath;

/**
 * An expression modelling a real addition.
 */
public final class RealPlus extends AbstractRealBinTerm {
  /**
   * Builds an addition expression for real constraint modelling.
   * @param solver is the current model
   * @param exp1 is the first expression operand
   * @param exp2 is the second expression operand
   */
  public RealPlus(final Solver solver, final RealExp exp1,
      final RealExp exp2) {
    super(solver, exp1, exp2);
  }

  public String pretty() {
    return "("+exp1.pretty() + " + " + exp2.pretty()+")";
  }

  /**
   * Tightens the expression to find the smallest interval containing values
   * the expression can equal according to operand domains.
   */
  public void tighten() {
    RealInterval res = RealMath.add(exp1, exp2);
    inf.set(res.getInf());
    sup.set(res.getSup());
  }

  /**
   * Projects domain reduction on operands according to the expression
   * domain itself (due to constraint restrictions).
   * @throws choco.kernel.solver.ContradictionException if a domain becomes empty
   */
  public void project() throws ContradictionException {
    exp1.intersect(RealMath.sub(this, exp2));
    exp2.intersect(RealMath.sub(this, exp1));
  }
}
