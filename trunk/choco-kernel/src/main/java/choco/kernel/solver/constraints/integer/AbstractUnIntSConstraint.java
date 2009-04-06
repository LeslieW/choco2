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
import choco.kernel.solver.propagation.ConstraintEvent;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

//import choco.choco.*;
//import choco.kernel.solver.search.AbstractIntConstraint;

/**
 * An abstract class for all implementations of (unary) listeners over
 * one search variable.
 */
public abstract class AbstractUnIntSConstraint extends AbstractIntSConstraint {

  /**
   * The unique variable of the constraint.
   */

  public IntDomainVar v0;


  /**
   * The index of the constraint among all listeners of its first (and unique)
   * variable.
   */

  public int cIdx0;


    protected AbstractUnIntSConstraint(IntDomainVar v0) {
        super(ConstraintEvent.HIGH);
        this.v0 = v0;
    }

    /**
   * Let v be the i-th var of c, records that c is the n-th constraint involving v.
   */

  public void setConstraintIndex(int i, int val) {
    if (i == 0) {
		cIdx0 = val;
	} else {
		throw new SolverException("bug in setConstraintIndex i:" + i + " this: " + this);
	}
  }


  /**
   * Returns the index of this listeners in the variable <code>idx</code>.
   *
   * @param idx Index of the variable.
   */

  public int getConstraintIdx(int idx) {
    if (idx == 0) {
		return cIdx0;
	} else {
		return -1;
	}
  }


  /**
   * Checks if all the variables of the constraint are instantiated.
   */

  @Override
public boolean isCompletelyInstantiated() {
    return v0.isInstantiated();
  }


  /**
   * Returns the number of variables: 1 for an unIntConstraint.
   */

  public int getNbVars() {
    return (1);
  }


  /**
   * Returns the variable number <code>i</code>. Here, <code>i</code>
   * should be 0.
   */

  public Var getVar(int i) {
    if (i == 0) {
		return v0;
	} else {
		return null;
	}
  }

  public void setVar(int i, Var v) {
    if (v instanceof IntDomainVar) {
      if (i == 0) {
		this.v0 = (IntDomainVar) v;
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
	} else {
		return null;
	}
  }
}
