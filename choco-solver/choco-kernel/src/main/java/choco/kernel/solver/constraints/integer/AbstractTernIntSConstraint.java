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

package choco.kernel.solver.constraints.integer;

import choco.kernel.solver.SolverException;
import choco.kernel.solver.propagation.event.ConstraintEvent;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;


/**
 * An abstract class for all implementations of (unary) listeners over one
 * search variable.
 */
public abstract class AbstractTernIntSConstraint extends AbstractIntSConstraint {

  /**
   * The first variable of the constraint.
   */
  protected IntDomainVar v0;

  /**
   * The second variable of the constraint.
   */
  protected IntDomainVar v1;

  /**
   * The third variable of the constraint.
   */
  protected IntDomainVar v2;

  /**
   * The index of the constraint among all listeners of its first variable.
   */
  protected int cIdx0;

  /**
   * The index of the constraint among all listeners of its second variable.
   */
  protected int cIdx1;

  /**
   * The index of the constraint among all listeners of its third variable.
   */
  protected int cIdx2;

  /**
   * Builds a ternary constraint with the specified variables.
   * @param x0 the first variable
   * @param x1 the second variable
   * @param x2 the third variable
   */
  public AbstractTernIntSConstraint(final IntDomainVar x0,
      final IntDomainVar x1, final IntDomainVar x2) {
      super(ConstraintEvent.HIGH);
    v0 = x0;
    v1 = x1;
    v2 = x2;
  }

  /**
   * Let v be the i-th var of c,
   * records that c is the constraint n according to v.
   * @param i the variable index
   * @param val the constraint index according to the variable
   */
  public void setConstraintIndex(final int i, final int val) {
    if (i == 0) {
      cIdx0 = val;
    } else if (i == 1) {
      cIdx1 = val;
    } else if (i == 2) {
      cIdx2 = val;
    } else {
      throw new SolverException("bug in setConstraintIndex i:" + i + " this: " + this);
    }
  }

  /**
   * Returns the index of the constraint in the specified variable.
   * @param idx the variable index
   * @return the constraint index according to the variable
   */
  public int getConstraintIdx(final int idx) {
    if (idx == 0) {
      return cIdx0;
    } else if (idx == 1) {
      return cIdx1;
    } else if (idx == 2) {
      return cIdx2;
    } else {
      return -1;
    }
  }

  /**
   * Checks if all the variables are instantiated.
   * @return true if all variables are sintantiated
   */
  @Override
public boolean isCompletelyInstantiated() {
    return (v0.isInstantiated() && v1.isInstantiated() && v2.isInstantiated());
  }

  /**
   * Returns the number of variables.
   * @return the number of variables, here always 3.
   */
  public int getNbVars() {
    return 3;
  }

  /**
   * Gets the specified variable.
   * @param i the variable index
   * @return the variable with the specified index according to this constraint
   */
  public Var getVar(final int i) {
    if (i == 0) {
      return v0;
    } else if (i == 1) {
      return v1;
    } else if (i == 2) {
      return v2;
    } else {
      return null;
    }
  }

  /**
   * Sets the association between variable and index of this variable.
   * @param i the variable index
   * @param v the variable
   */
  public void setVar(final int i, final Var v) {
    if (v instanceof IntDomainVar) {
      if (i == 0) {
        this.v0 = (IntDomainVar) v;
      } else if (i == 1) {
        this.v1 = (IntDomainVar) v;
      } else if (i == 2) {
        this.v2 = (IntDomainVar) v;
      } else {
        throw new SolverException("BUG in CSP network management: "
            + "too large index for setVar");
      }
    } else {
      throw new SolverException("BUG in CSP network management: "
          + "wrong type of Var for setVar");
    }
  }


  /**
   * Gets the <code>i</code>th search valued variable.
   * @param i the variable index
   * @return the variable with index i
   */
  public IntDomainVar getIntVar(final int i) {
    if (i == 0) {
      return v0;
    } else if (i == 1) {
      return v1;
    } else if (i == 2) {
      return v2;
    } else {
      return null;
    }
  }
}
