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
package choco.cp.solver.search.set;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.ValSelector;
import choco.kernel.solver.search.set.AbstractSetBranchingStrategy;
import choco.kernel.solver.variables.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public final class AssignSetVar extends AbstractSetBranchingStrategy {

	VarSelector<SetVar> varselector;
	ValSelector<SetVar> valselector;

	public AssignSetVar(VarSelector<SetVar> varselect, ValSelector<SetVar> valselect) {
		varselector = varselect;
		valselector = valselect;
	}

	public Object selectBranchingObject() throws ContradictionException {
		return varselector.selectVar();
	}
                                                                               
	
	@Override
	public void setFirstBranch(final IntBranchingDecision decision) {
		decision.setBranchingValue( valselector.getBestVal(decision.getBranchingSetVar()));
	}
}
