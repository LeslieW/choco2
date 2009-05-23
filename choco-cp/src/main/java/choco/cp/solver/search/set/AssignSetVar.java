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
import choco.kernel.solver.search.set.AbstractSetBranching;
import choco.kernel.solver.search.set.SetValSelector;
import choco.kernel.solver.search.set.SetVarSelector;
import choco.kernel.solver.variables.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class AssignSetVar extends AbstractSetBranching {

	SetVarSelector varselector;
	SetValSelector valselector;
	String[] LOG_DECISION_MSG = new String[]{"contains ", "contains not "};

	public AssignSetVar(SetVarSelector varselect, SetValSelector valselect) {
		varselector = varselect;
		valselector = valselect;
	}

	public Object selectBranchingObject() throws ContradictionException {
		Object x = varselector.selectSetVar();
		if (x == null) return null;
		return new Object[]{x, valselector.getBestVal((SetVar) x)};
	}

	@Override
	public int getFirstBranch(Object x) {
		return 1;
	}

	@Override
	public String getDecisionLogMsg(int i) {
		if (i == 1) return LOG_DECISION_MSG[0];
		else if (i == 2) return LOG_DECISION_MSG[1];
		else return "";
	}

}
