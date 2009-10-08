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

/**
 * An abstract class for all implementations of listeners over many search variables.
 */
public abstract class AbstractLargeIntSConstraint extends AbstractIntSConstraint {

  /**
   * The list of variables of the constraint.
   */

  public IntDomainVar[] vars;


  /**
   * The list, containing, for each variable, the index of the constraint among all
   * its incident listeners.
   */

  public int[] cIndices;

  /**
   * The search constant attached to the constraint.
   */
  public int cste;

  /**
   * constructor: allocates the data util for n variables
   *
   * @param n the number of variables involved in the constraint
   * @deprecated use AbstractLargeIntConstraint(IntDomainVar[] vars) instead
   */
  @Deprecated
public AbstractLargeIntSConstraint(int n) {
      super(ConstraintEvent.LOW);
    vars = new IntDomainVar[n];
    cIndices = new int[n];
  }

  public AbstractLargeIntSConstraint(IntDomainVar[] vars) {
      super(ConstraintEvent.LOW);
    this.vars = new IntDomainVar[vars.length];
    System.arraycopy(vars, 0, this.vars, 0, vars.length);
    cIndices = new int[vars.length];
  }

  @Override
public Object clone() throws CloneNotSupportedException {
    AbstractLargeIntSConstraint newc = (AbstractLargeIntSConstraint) super.clone();
    newc.vars = new IntDomainVar[this.vars.length];
    System.arraycopy(this.vars, 0, newc.vars, 0, this.vars.length);
    cIndices = new int[this.cIndices.length];
    System.arraycopy(this.cIndices, 0, newc.cIndices, 0, this.cIndices.length);
    return newc;
  }

  /**
   * Let <i>v</i> be the <i>i</i>-th var of <i>c</i>, records that <i>c</i> is the
   * <i>n</i>-th constraint involving <i>v</i>.
   */
  public void setConstraintIndex(int i, int val) {
    if (i >= 0 && i < vars.length) {
		cIndices[i] = val;
	} else {
		throw new SolverException("bug in setConstraintIndex i:" + i + " this: " + this);
	}
  }


  /**
   * Returns the index of the constraint in the specified variable.
   */

  public int getConstraintIdx(int i) {
    if (i >= 0 && i < vars.length) {
		return cIndices[i];
	} else {
		return -1;
	}
  }


  /**
   * Checks wether all the variables are instantiated.
   */

  @Override
public boolean isCompletelyInstantiated() {
    int nVariables = vars.length;
    for (int i = 0; i < nVariables; i++) {
      if (!(vars[i].isInstantiated())) {
		return false;
	}
    }
    return true;
  }


  /**
   * Returns the number of variables.
   */

  public int getNbVars() {
    return vars.length;
  }


  /**
   * Returns the <code>i</code>th variable.
   */

  public Var getVar(int i) {
    if (i >= 0 && i < vars.length) {
		return vars[i];
	} else {
		return null;
	}
  }

  public void setVar(int i, Var v) {
    if (v instanceof IntDomainVar) {
      if (i >= 0 && i < vars.length) {
		this.vars[i] = (IntDomainVar) v;
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
    if (i >= 0 && i < getNbVars()) {
		return this.vars[i];
	} else {
		return null;
	}
  }
}
