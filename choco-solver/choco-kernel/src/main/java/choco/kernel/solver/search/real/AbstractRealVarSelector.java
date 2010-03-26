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
package choco.kernel.solver.search.real;

import choco.kernel.solver.Solver;
import choco.kernel.solver.search.AbstractSearchHeuristic;
import choco.kernel.solver.variables.AbstractVar;

/**
 * An interface for real variable selector during a braching assigning intervals.
 */
public abstract class AbstractRealVarSelector extends AbstractSearchHeuristic implements RealVarSelector {
  
	
	public AbstractRealVarSelector(Solver solver) {
		super(solver);
	}

	public final AbstractVar selectVar() {
    return (AbstractVar) selectRealVar();
  }
}
