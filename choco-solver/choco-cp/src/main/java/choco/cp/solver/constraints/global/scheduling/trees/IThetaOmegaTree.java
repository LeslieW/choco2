/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  Â°(..)  |                           *
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
package choco.cp.solver.constraints.global.scheduling.trees;

import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

public interface IThetaOmegaTree extends IThetaLambdaTree{

	/**
	 * 
	 * @return ECT(Theta,Omega)
	 */
	int getTOTime();
	/**
	 * 
	 * @return task responsible for highest ECT(Theta,Omega)
	 */
	Object getResponsibleTOTask();
	/**
	 * 
	 * @param task
	 * @return true if the insertion is successful.
	 */
	boolean insertInOmega(IRTask task);
	/**
	 * 
	 * @param task
	 * @return true if the removal is sucessful.
	 */
	boolean removeFromOmega(IRTask task);
	/**
	 * Removes task from Omega, and insert it in Lambda.
	 * @param rtask
	 * @return true if the operation is successful.
	 */
	boolean removeFromOmegaAndInsertInLambda(IRTask rtask);
}
