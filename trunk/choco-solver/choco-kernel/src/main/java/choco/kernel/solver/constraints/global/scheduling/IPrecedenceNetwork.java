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
package choco.kernel.solver.constraints.global.scheduling;

import choco.kernel.common.IDotty;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 * @author Arnaud Malapert</br> 
 * @since 16 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public interface IPrecedenceNetwork extends IDotty {

	boolean isConnected(TaskVar t1, TaskVar t2);

	boolean isOrdered(TaskVar t1, TaskVar t2);
		
	void firePrecedenceAdded(TaskVar t1, TaskVar t2) throws ContradictionException;
	
	void addStaticPrecedence(Solver solver, TaskVar t1, TaskVar t2);
}

