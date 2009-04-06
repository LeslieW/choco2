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


package choco.cp.solver.constraints.integer;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractUnIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.logging.Level;

/**
 * Implements a constraint X >= C, with X a variable and C a constant.
 */
public class GreaterOrEqualXC extends AbstractUnIntSConstraint {

  /**
   * The search constant of the constraint
   */
  protected final int cste;

  /**
   * Constructs the constraint with the specified variables and constant.
   *
   * @param x0 the search valued domain variable
   * @param c  the search constant used in the inequality.
   */

  public GreaterOrEqualXC(IntDomainVar x0, int c) {
      super(x0);
      this.v0 = x0;
    this.cste = c;
  }

    @Override
    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
    }

    public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Pretty print of the constraint.
   */

  public String pretty() {
    return this.v0 + " >= " + cste;
  }

  /**
   * The one and only propagation method. <br>
   * Note that after the first propagation, the constraint is set passive
   * (to prevent from further calls to propagation methods)
   */

  public void propagate() throws ContradictionException {
    if (logger.isLoggable(Level.FINEST))
      logger.finest("VAL(" + v0.toString() + ") >= " + this.cste);
    v0.updateInf(this.cste, this.cIdx0);
    this.setEntailed();
  }


  public void awakeOnInf(int idx) throws ContradictionException {
    ;
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    ;
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    assert(idx == 0);
    if (logger.isLoggable(Level.FINEST))
      logger.finest("VAL(" + v0.toString() + ") >= " + this.cste);
    if (v0.getVal() < this.cste)
      this.fail();
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    ;
  }

  /**
   * When the whole domain of <code>v0</code> is below or above <code>cste</code>,
   * we know for sure whether the constraint will be satisfied or not
   */

  public Boolean isEntailed() {
    if (v0.getInf() >= this.cste)
      return Boolean.TRUE;
    else if (v0.getSup() < this.cste)
      return Boolean.FALSE;
    else
      return null;
  }

  /**
   * tests if the constraint is satisfied when the variables are instantiated.
   */

  public boolean isSatisfied(int[] tuple) {
    assert(v0.isInstantiated());
    return (tuple[0] >= this.cste);
  }

  /**
   * tests if the constraint is consistent with respect to the current state of domains
   *
   * @return true iff the constraint is bound consistent (same as arc consistent)
   */
  public boolean isConsistent() {
    return (v0.getInf() >= this.cste);
  }

  public AbstractSConstraint opposite() {
    return (AbstractSConstraint) getSolver().lt(v0, cste);
//    return new LessOrEqualXC(v0, cste - 1);
  }


}
