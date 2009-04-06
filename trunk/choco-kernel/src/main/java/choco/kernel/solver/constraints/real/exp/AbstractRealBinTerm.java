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
package choco.kernel.solver.constraints.real.exp;

import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.real.RealExp;
import choco.kernel.solver.variables.real.RealVar;

import java.util.List;
import java.util.Set;

/**
 * A binary real expression.
 */
public abstract class AbstractRealBinTerm extends AbstractRealCompoundTerm {
  protected RealExp exp1, exp2;

  public AbstractRealBinTerm(Solver solver, RealExp exp1, RealExp exp2) {
    super(solver);
    this.exp1 = exp1;
    this.exp2 = exp2;
  }

  public List<RealExp> subExps(List<RealExp> l) {
    exp1.subExps(l);
    exp2.subExps(l);
    l.add(this);
    return l;
  }

  public Set<RealVar> collectVars(Set<RealVar> s) {
    exp1.collectVars(s);
    exp2.collectVars(s);
    return s;
  }

  public boolean isolate(RealVar var, List<RealExp> wx, List<RealExp> wox) {
    boolean dependsOnX = exp1.isolate(var, wx, wox) | exp2.isolate(var, wx, wox);
    if (dependsOnX) wx.add(this); else wox.add(this);
    return dependsOnX;
  }
}
