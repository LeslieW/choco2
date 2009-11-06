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


import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.real.RealVar;

public abstract class AbstractUnRealSConstraint extends AbstractSConstraint implements RealSConstraint {
  /**
   * The unique variable of the constraint.
   */
  protected RealVar v0;

  /**
   * The index of this constraints w.r.t. the variable v0.
   */
  protected int cIdx0;

    /**
     * Constraucts a constraint with the priority 0.
     */
    protected AbstractUnRealSConstraint(RealVar v0) {
        this.v0 = v0;
    }

    /**
   * Returns the only one variable if i=0, null otherwise.
   */
  public RealVar getRealVar(int i) {
    if (i == 0) {
		return v0;
	} else {
		return null;
	}
  }

  /**
   * Returns the number of real variables.
   */
  public int getRealVarNb() {
    return 1;
  }

  /**
   * Let v0 be the i-th var of c, records that c is the idx-th constraint involving v0.
   */
  public void setConstraintIndex(int i, int idx) {
    if (i == 0) {
		cIdx0 = idx;
	} else {
		throw new SolverException("bug in setConstraintIndex i:" + i + " this: " + this);
	}
  }

  /**
   * Returns the index of this constraints in all constraints involving v0.
   */
  public int getConstraintIdx(int idx) {
    if (idx == 0) {
		return cIdx0;
	} else {
		return -1;
	}
  }

  /**
   * Returns the number of variables.
   */
  public int getNbVars() {
    return 1;
  }

  /**
   * Returns the only one variable v0 if i==0, null otherwise.
   */
  public Var getVar(int i) {
    if (i == 0) {
		return v0;
	} else {
		return null;
	}
  }

  public void setVar(int i, Var v) {
    if (v instanceof RealVar) {
      if (i == 0) {
		this.v0 = (RealVar) v;
	}
      throw new SolverException("BUG in CSP network management: too large index for setVar");
    } else {
      throw new SolverException("BUG in CSP network management: wrong type of Var for setVar");
    }
  }

  /**
   * Checks if the only one variable of the constraint is instantiated.
   */
  public boolean isCompletelyInstantiated() {
    return v0.isInstantiated();
  }

}
