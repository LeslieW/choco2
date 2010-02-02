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
	
	public final static boolean ALIAA_FLAG = true;
	
	void fireDomainChanged();
	
	boolean isActive();
	
	int getMakespanLB();
	
	boolean overloadChecking();

	public boolean notFirst() throws ContradictionException;

	public boolean notLast() throws ContradictionException;
	
	public boolean notFirstNotLast() throws ContradictionException;
	
	public boolean detectablePrecedenceEST() throws ContradictionException;

	public boolean detectablePrecedenceLCT() throws ContradictionException;
	
	public boolean detectablePrecedence() throws ContradictionException;
	
	public boolean edgeFindingEST() throws ContradictionException;
	
	public boolean edgeFindingLCT() throws ContradictionException;
	
	public boolean edgeFinding() throws ContradictionException;
	
	/** optional operation */
	void remove(IRTask rtask);
		
}