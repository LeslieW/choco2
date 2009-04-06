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
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.real.RealVar;

/**
 * A real constraint with an undetermined number of variables.
 */
public abstract class AbstractLargeRealSConstraint
    extends AbstractSConstraint implements RealSConstraint {
  /**
   * The variables involved in the constraint.
   */
  protected RealVar[] vars;

  /**
   * Indices associated with this contraint in each variable.
   */
  protected int[] cIndices;

  /**
   * Builds such a constraint with the specified variables.
   * @param vars the variables involved by this constraint
   */
  public AbstractLargeRealSConstraint(final RealVar[] vars) {
    this.vars = new RealVar[vars.length];
    System.arraycopy(vars, 0, this.vars, 0, vars.length);
    cIndices = new int[vars.length];
  }

  /**
   * Makes a copy of this constraint.
   * @throws CloneNotSupportedException thrown if this constraint cannot be
   * cloned.
   * @return a copy of this constraint
   */
  @Override
public Object clone() throws CloneNotSupportedException {
    AbstractLargeRealSConstraint newc =
        (AbstractLargeRealSConstraint) super.clone();
    newc.vars = new RealVar[this.vars.length];
    System.arraycopy(this.vars, 0, newc.vars, 0, this.vars.length);
    cIndices = new int[this.cIndices.length];
    System.arraycopy(this.cIndices, 0, newc.cIndices, 0, this.cIndices.length);
    return newc;
  }

  /**
   * Sets this constraint index according to the variable i.
   * @param i the variable index
   * @param idx this constraint index according to the variable i
   */
  public void setConstraintIndex(final int i, final int idx) {
    if (i >= 0 && i < vars.length) {
      cIndices[i] = idx;
    } else {
      throw new SolverException("bug in setConstraintIndex i:" + i + " this: " + this);
    }
  }

  /**
   * Returns this constraint index according to the variable i.
   * @param i the variable index
   * @return this constraint index
   */
  public int getConstraintIdx(final int i) {
    if (i >= 0 && i < vars.length) {
      return cIndices[i];
    } else {
      return -1;
    }
  }

  /**
   * Returns the number of variables.
   * @return the number of variables involved by this constraint
   */
  public int getNbVars() {
    return vars.length;
  }

  /**
   * Returns the variable with the index i.
   * @param i the requested index
   * @return the required variable
   */
  public Var getVar(final int i) {
    if (0 <= i && i < vars.length) {
      return vars[i];
    }
    return null;
  }

  /**
   * Sets a variable involved by the constraint.
   * @param i the index to set
   * @param v the variable
   */
  public void setVar(final int i, final Var v) {
    if (v instanceof RealVar) {
      if (i >= 0 && i < vars.length) {
        this.vars[i] = (RealVar) v;
      } else {
        throw new SolverException("BUG in CSP network management: too large "
            + "index for setVar");
      }
    } else {
      throw new SolverException("BUG in CSP network management: wrong type of "
          + "Var for setVar");
    }
  }

  /**
   * Returns the variable with the index i.
   * @param i the requested index
   * @return the required variable
   */
  public RealVar getRealVar(final int i) {
    if (0 <= i && i < vars.length) {
      return vars[i];
    }
    return null;
  }

  /**
   * Returns the number of real variables. In this case it equals the number
   * of variables since there are only real variables.
   * @return the number of real variables
   */
  public int getRealVarNb() {
    return vars.length;
  }


  /**
   * Checks if the constrait is completely instantiated.
   * @return true if all variables are instantiated
   */
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
   * Returns the index associated with this constraint by the model.
   * @return this constraint index according to the model
   */
  public int getSelfIndex() {
    // TODO
    return 0;
  }

    @Override
    public SConstraintType getConstraintType() {
        return SConstraintType.REAL;
    }

    public int getFineDegree(int idx) {
        return 1;
    }
}
