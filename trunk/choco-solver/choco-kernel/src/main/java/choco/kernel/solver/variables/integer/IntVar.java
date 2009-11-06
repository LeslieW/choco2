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
package choco.kernel.solver.variables.integer;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.IntExp;
import choco.kernel.solver.variables.Var;

public interface IntVar extends Var, IntExp {
	
	/**
	 * <b>Public user API:</b>
	 * <i>Propagation events</i> assigning a value to a variable
	 * (ie: removing all other values from its domain).
	 *
	 * @param x the value that is assigned to the variable
	 * @throws choco.kernel.solver.ContradictionException contradiction exception
	 */
	void setVal(int x) throws ContradictionException;
	
	
	/**
	 * Returns the value of the variable if instantiated.
	 *
	 * @return the value of the variable
	 */

	int getVal();
	
	/**
	 * <b>Public user API:</b>
	 * <i>Domains :</i> testing whether the value of an instantiated variable
	 * is equal to a specific value.
	 * @param x the tested value
	 * @return wether the value of an instantiated variables is equal to a x.
	 */
	public boolean isInstantiatedTo(int x);
}
