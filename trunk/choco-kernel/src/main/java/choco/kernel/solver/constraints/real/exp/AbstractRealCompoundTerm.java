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

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateDouble;
import choco.kernel.solver.ContradictionException;
import static choco.kernel.solver.ContradictionException.Type.DOMAIN;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.real.RealExp;
import choco.kernel.solver.propagation.VarEvent;
import choco.kernel.solver.variables.real.RealInterval;

/**
 * A compound expression depending on other terms.
 */
public abstract class AbstractRealCompoundTerm implements RealExp {
  protected IStateDouble inf;
  protected IStateDouble sup;
    /**
     * The (optimization or decision) model to which the entity belongs.
     */

    public Solver solver;


  public AbstractRealCompoundTerm(Solver solver) {
    this.solver =solver;
    IEnvironment env = solver.getEnvironment();
    inf = env.makeFloat(Double.NEGATIVE_INFINITY);
    sup = env.makeFloat(Double.POSITIVE_INFINITY);
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

  public String toString() {
    return "[" + inf.get() + "," + sup.get() + "]";
  }

  public double getInf() {
    return inf.get();
  }

  public double getSup() {
    return sup.get();
  }

  public void intersect(RealInterval interval) throws ContradictionException {
    intersect(interval, VarEvent.NOCAUSE);
  }

  public void intersect(RealInterval interval, int index) throws ContradictionException {
    if (interval.getInf() > inf.get()) inf.set(interval.getInf());
    if (interval.getSup() < sup.get()) sup.set(interval.getSup());
    if (inf.get() > sup.get()) {
      this.solver.getPropagationEngine().raiseContradiction(this, DOMAIN);
    }
  }


  public String pretty() {
    return toString();
  }
}
