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
package choco.cp.solver.variables.real;

import choco.cp.solver.propagation.RealVarEvent;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.real.RealExp;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.real.*;

import java.util.List;
import java.util.Set;
/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */
/**
 * An implementation of real variables using RealDomain domains.
 */
public class RealVarImpl extends AbstractVar implements RealVar {
  protected RealDomain domain;

  public RealVarImpl(Solver solver, String name, double a, double b, int domaintype) {
    super(solver, name);
    if (domaintype == RealVar.BOUNDS) {
     this.domain = new RealDomainImpl(this, a, b);
    } else throw new SolverException("Unknown real domain");
    this.event = new RealVarEvent(this);
  }

  @Override
public String toString() {
    return this.name + domain.toString();
  }

  public String pretty() {
    return this.toString();
  }

  public RealInterval getValue() {
    return new RealIntervalConstant(getInf(), getSup());
  }

  public RealDomain getDomain() {
    return domain;
  }

  public void silentlyAssign(RealInterval i) {
    domain.silentlyAssign(i);
  }

  public double getInf() {
    return domain.getInf();
  }

  public double getSup() {
    return domain.getSup();
  }

  public void intersect(RealInterval interval) throws ContradictionException {
    this.domain.intersect(interval);
  }

  public void intersect(RealInterval interval, int index) throws ContradictionException {
    this.domain.intersect(interval, index);
  }

  public boolean isInstantiated() {
    return RealMath.isCanonical(this, this.solver.getPrecision());
  }

  public void tighten() {
  }

  public void project() {
  }

  public List<RealExp> subExps(List<RealExp> l) {
    l.add(this);
    return l;
  }

  public Set<RealVar> collectVars(Set<RealVar> s) {
    s.add(this);
    return s;
  }

  public boolean isolate(RealVar var, List<RealExp> wx, List<RealExp> wox) {
      return this == var;
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
