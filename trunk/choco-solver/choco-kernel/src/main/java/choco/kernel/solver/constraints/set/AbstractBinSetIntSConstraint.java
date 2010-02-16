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
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public abstract class AbstractBinSetIntSConstraint extends AbstractMixedSetIntSConstraint {

  /**
   * The first variable of the constraint.
   */

  public IntDomainVar v0;


  /**
   * The second variable of the constraint.
   */
  public SetVar v1;

  /**
   * The index of the constraint among all listeners of its first variable.
   */
  public int cIdx0;

  /**
   * The index of the constraint among all listeners of its second variable.
   */

  public int cIdx1;

  /**
   * default constructor
   * @param X the integer variable
   * @param S the set variable
   */
  public AbstractBinSetIntSConstraint(IntDomainVar X, SetVar S) {
      super(new Var[]{});
      this.v0 = X;
    this.v1 = S;
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
    if (i == 0) {
      if (v instanceof IntDomainVar) {
        this.v0 = (IntDomainVar) v;
      } else {
        throw new SolverException("BUG in CSP network management: wrong type of Var for setVar");
      }
    } else if (i == 1) {
      if (v instanceof SetVar) {
        this.v1 = (SetVar) v;
      } else {
        throw new SolverException("BUG in CSP network management: wrong type of Var for setVar");
      }
    } else {
      throw new SolverException("BUG in CSP network management: too large index for setVar");
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

  /**
   * Gets the <code>i</code>th search valued variable.
   */

  public SetVar getSetVar(int i) {
    if (i == 1) {
		return v1;
	} else {
		return null;
	}
  }
}
