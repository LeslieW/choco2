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

/**
 * @author Arnaud Malapert</br> 
 * @since 10 févr. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public interface IThetaLambdaTree extends IThetaTree {

	int getGrayTime();

	Object getResponsibleTask();

	
	boolean insertInLambda(IRTask task);
	
	boolean removeFromLambda(ITask task);
	/**
	 * remove from the set thea and insert the atsk into lambda (optional operation).
	 * If some additional information is needed, it throws an {@link UnsupportedOperationException} and you should use {@link IThetaLambdaTree#removeFromThetaAndInsertInLambda(IRTask)}
	 * @param task
	 * @return <code>true</code> if it succeeds
	 */
	boolean removeFromThetaAndInsertInLambda(ITask task);
	
	
	boolean removeFromThetaAndInsertInLambda(IRTask task);
	
}