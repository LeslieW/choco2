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
 * An expression modelling a multiplication.
 */
public final class RealMult extends AbstractRealBinTerm {
  public RealMult(Solver solver, RealExp exp1, RealExp exp2) {
    super(solver, exp1, exp2);
  }

  public String pretty() {
    return "("+exp1.pretty() + " * " + exp2.pretty()+")";
  }

  public void tighten() {
    RealInterval res = RealMath.mul(exp1, exp2);
    inf.set(res.getInf());
    sup.set(res.getSup());
  }

  public void project() throws ContradictionException {
    RealInterval res = RealMath.odiv_wrt(this, exp2, exp1);
    if (res.getInf() > res.getSup()) {
      this.solver.getPropagationEngine().raiseContradiction(this);
    }
    exp1.intersect(res);

    res = RealMath.odiv_wrt(this, exp1, exp2);
    if (res.getInf() > res.getSup()) {
      this.solver.getPropagationEngine().raiseContradiction(this);
    }
    exp2.intersect(res);
  }
}
