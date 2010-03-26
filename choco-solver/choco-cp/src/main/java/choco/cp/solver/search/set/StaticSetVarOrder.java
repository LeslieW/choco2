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

import choco.kernel.memory.IStateInt;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.set.AbstractSetVarSelector;
import choco.kernel.solver.variables.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class StaticSetVarOrder extends AbstractSetVarSelector {

	private final IStateInt last;

	public StaticSetVarOrder(Solver solver, SetVar[] vars) {
		super(solver, vars);
		this.last = solver.getEnvironment().makeInt(0);
	}

	@Override
	public SetVar selectSetVar() {
		//<hca> it starts at last.get() and not last.get() +1 to be
	    //robust to restart search loop
	    for (int i = last.get(); i < vars.length; i++) {
	      if (!vars[i].isInstantiated()) {
	          last.set(i);
	          return vars[i];

	      }
	    }
	    return null;
	}

	@Override
	public int getHeuristic(SetVar v) {
		return 0;
	}
}
