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
package choco.cp.solver.constraints.global.scheduling;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.scheduling.IRTask;

/**
 * 
 * The interface represents the classical filtering rules for an unary resource.
 * @author Arnaud Malapert</br> 
 * @since 23 févr. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public interface IDisjRules {
	
	void initialize();
	
	void fireDomainChanged();
	
	boolean isActive();
	
	void overloadChecking() throws ContradictionException;

	boolean notFirst() throws ContradictionException;

	boolean notLast() throws ContradictionException;
	
	boolean notFirstNotLast() throws ContradictionException;
	
	boolean detectablePrecedenceEST() throws ContradictionException;

	boolean detectablePrecedenceLCT() throws ContradictionException;
	
	boolean detectablePrecedence() throws ContradictionException;
	
	boolean edgeFindingEST() throws ContradictionException;
	
	boolean edgeFindingLCT() throws ContradictionException;
	
	boolean edgeFinding() throws ContradictionException;
	
	/** optional operation */
	void remove(IRTask rtask);
		
}