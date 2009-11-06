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
import choco.kernel.solver.propagation.VarEvent;
import choco.kernel.solver.variables.real.RealInterval;

/**
 * @deprecated
 */
public class RealIntervalImpl implements RealInterval {
  protected double inf;
  protected double sup;

    Solver solver;

  public RealIntervalImpl(double inf, double sup) {
    this.inf = inf;
    this.sup = sup;
  }

  public RealIntervalImpl() {
    this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
  }

  public RealIntervalImpl(RealInterval i) {
    this(i.getInf(), i.getSup());
  }

  public String toString() {
    return "[" + inf + "," + sup + "]";
  }

  public String pretty() {
    return this.toString();
  }

  public double getInf() {
    return inf;
  }

  public double getSup() {
    return sup;
  }

  public void intersect(RealInterval interval) throws ContradictionException {
    intersect(interval, VarEvent.NOCAUSE);
  }

  public void intersect(RealInterval interval, int index) throws ContradictionException {
    if (interval.getInf() > inf) inf = interval.getInf();
    if (interval.getSup() < sup) sup = interval.getSup();
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
}
