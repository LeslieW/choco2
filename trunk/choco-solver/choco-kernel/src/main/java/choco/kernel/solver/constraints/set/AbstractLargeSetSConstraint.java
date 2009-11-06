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
package choco.kernel.solver.constraints.set;

import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.set.SetVar;

/* 
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 14 janv. 2008
 * Since : Choco 2.0.0
 *
 */
public abstract class AbstractLargeSetSConstraint extends AbstractSetSConstraint {

  /**
   * The set variables representing the scope of the constraint.
   */
  public SetVar[] vars;

  /**
   * The index of the constraint among all listeners of its first variable.
   */
  public int[] cIndices;

    public AbstractLargeSetSConstraint(SetVar[] vars) {
    this.vars = new SetVar[vars.length];
    System.arraycopy(vars, 0, this.vars, 0, vars.length);
    cIndices = new int[vars.length];
  }

 /**
   * Let v be the i-th var of c, records that c is the n-th constraint involving v.
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
  public int getConstraintIdx(int idx) {
    if (idx >= 0 && idx < vars.length) {
		return cIndices[idx];
	} else {
		return -1;
	}
  }

  /**
   * Checks if all the variables are instantiated.
   */
  @Override
public boolean isCompletelyInstantiated() {
      for (SetVar var : vars) {
          if (!var.isInstantiated()) {
			return false;
		}
      }
    return  true;
  }

  /**
   * Returns the number of variables.
   */
  public int getNbVars() {
    return vars.length;
  }

  /**
   * Gets the specified variable.
   */
  public Var getVar(int i) {
	if (i >= 0 && i < vars.length) {
		return vars[i];
	} else {
		return null;
	}
  }

  public void setVar(int i, Var v) {
    if (v instanceof SetVar) {
	  if (i >= 0 && i < vars.length) {
		vars[i] = (SetVar) v;
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
  public SetVar getSetVar(int i) {
    if (i >= 0 && i < vars.length) {
		return vars[i];
	} else {
		return null;
	}
  }


}
