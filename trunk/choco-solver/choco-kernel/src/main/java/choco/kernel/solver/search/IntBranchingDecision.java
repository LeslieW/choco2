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
package choco.kernel.solver.search;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.set.SetVar;

public interface IntBranchingDecision {
	
	/**
	 * get the branching object. It is often a variable
	 */
	Object getBranchingObject();
	
	/**
	 * get the next value to try, if any (optional).
	 */
	int getBranchingValue();
	
	/**
	 * set the next value to try.
	 */
	void setBranchingValue(final int branchingValue);
	
	/**
	 * get the index of the current alternative (branch).
	 * @return
	 */
	int getBranchIndex();
	
	/**
	 * get and cast the branching object.
	 */
	IntDomainVar getBranchingIntVar();
	
	/**
	 * get and cast the branching object.
	 */
	SetVar getBranchingSetVar();
	/**
	 * get and cast the branching object.
	 */
	RealVar getBranchingRealVar();
	
	//utility functions
	/**
	 * apply the integer assignment decision, i.e. assign the branching value to the branching int var.
	 */
	void setIntVal() throws ContradictionException;
	
	/**
	 * apply the integer removal decision, i.e. remove the branching value from the domain of the branching int var.
	 */
	void remIntVal() throws ContradictionException;
	
	/**
	 * apply the set assignment decision, i.e. put the value into the kernel.
	 */
	void setValInSet() throws ContradictionException;

	/**
	 * apply the set removal decision, i.e. remove the value from the enveloppe.
	 */
	void setValOutSet() throws ContradictionException;
}
