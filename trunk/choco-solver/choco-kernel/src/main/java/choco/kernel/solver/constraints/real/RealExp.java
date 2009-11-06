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
package choco.kernel.solver.constraints.real;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.real.RealInterval;
import choco.kernel.solver.variables.real.RealVar;

import java.util.List;
import java.util.Set;

/**
 * An interface for real expressions.
 */
public interface RealExp extends RealInterval {
  /**
   * Computes the narrowest bounds with respect to sub terms.
   */
  public void tighten();

  /**
   * Projects computed bounds to the sub expressions.
   *
   * @throws choco.kernel.solver.ContradictionException
   */
  public void project() throws ContradictionException;

  /**
   * Computes recursively the sub expressions (avoids to tighten and project recursively).
   *
   * @return the flattened list of subexpressions
   */
  public List<RealExp> subExps(List<RealExp> l);

  /**
   * Collects recursively all the variable this expression depends on.
   *
   * @return the collected set
   */
  public Set<RealVar> collectVars(Set<RealVar> s);

  /**
   * Isolates sub terms depending or not on a variable x.
   *
   * @param var
   * @param wx
   * @param wox
   * @return TODO
   */
  public boolean isolate(RealVar var, List<RealExp> wx, List<RealExp> wox);


}
