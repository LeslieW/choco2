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
	 * default constructor
	 * @param X the integer variable
	 * @param S the set variable
	 */
	public AbstractBinSetIntSConstraint(IntDomainVar v0, SetVar v1) {
		super(new Var[]{v0, v1});
		this.v0 = v0;
		this.v1 = v1;
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
