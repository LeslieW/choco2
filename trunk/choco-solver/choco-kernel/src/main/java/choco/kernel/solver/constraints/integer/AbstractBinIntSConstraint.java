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

// import choco.kernel.solver.search.AbstractIntConstraint;

/**
 * An abstract class for all implementations of (binary) constraints over two search variable.
 */
public abstract class AbstractBinIntSConstraint extends AbstractIntSConstraint {

  /**
   * The first variable of the constraint.
   */

  public IntDomainVar v0;


  /**
   * The second variable of the constraint.
   */

  public IntDomainVar v1;


  /**
   * The index of the constraint among all listeners of its first variable.
   */

  public int cIdx0;


  /**
   * The index of the constraint among all listeners of its second variable.
   */

  public int cIdx1;

  public AbstractBinIntSConstraint(IntDomainVar x0, IntDomainVar x1) {
      super(ConstraintEvent.HIGH);
    v0 = x0;
    v1 = x1;
  }

  /**
   * Let v be the i-th var of c, records that c is the n-th constraint involving v.
   */

  public void setConstraintIndex(int i, int val) {
    if (i == 0) {
		cIdx0 = val;
	} else if (i == 1) {
		cIdx1 = val;
	} else {
		throw new SolverException("bug in setConstraintIndex i:" + i + " this: " + this);
	}
  }


  /**
   * Returns the index of this constraint for the specified variables.
   */

  public int getConstraintIdx(int idx) {
    if (idx == 0) {
		return cIdx0;
	} else if (idx == 1) {
		return cIdx1;
	} else {
		return -1;
	}
  }


  /**
   * Checks if all the variables are instantiated.
   */

  @Override
public boolean isCompletelyInstantiated() {
    return (v0.isInstantiated() && v1.isInstantiated());
  }


  /**
   * Returns the number of varibles.
   */

  public int getNbVars() {
    return (2);
  }


  /**
   * Returns the specified variable.
   */

  public Var getVar(int i) {
    if (i == 0) {
		return v0;
	} else if (i == 1) {
		return v1;
	} else {
		return null;
	}
  }

  public void setVar(int i, Var v) {
    if (v instanceof IntDomainVar) {
      if (i == 0) {
		this.v0 = (IntDomainVar) v;
	} else if (i == 1) {
		this.v1 = (IntDomainVar) v;
	} else {
		throw new SolverException("BUG in CSP network management: too large index for setVar");
	}
    } else {
      throw new SolverException("BUG in CSP network management: wrong type of Var for setVar");
    }
  }


  /**
   * Gets the <code>i</code>th search valued variable.
   */

  public IntDomainVar getIntVar(int i) {
    if (i == 0) {
		return v0;
	} else if (i == 1) {
		return v1;
	} else {
		return null;
	}
  }
}
