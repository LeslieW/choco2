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
package choco.cp.solver.search.task;

import choco.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import choco.kernel.common.IDotty;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.ITask;

public interface ITemporalStore extends IDotty {

	ITemporalSRelation getTemporalRelation(ITask t1, ITask t2);
	
	void addPrecedence(ITask t1, ITask t2, IntDomainVar direction);

	boolean isReified(ITask t1, ITask t2);
	
	int getNbReifiedPrecedence();
	
	boolean containsReifiedPrecedence();

	ITemporalSRelation[] getValues();
}

