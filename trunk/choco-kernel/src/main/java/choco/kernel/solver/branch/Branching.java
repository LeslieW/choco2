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

package choco.kernel.solver.branch;

import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.ContradictionException;

/**
 * Branching objects are responsible for controlling the execution of the program at a point where
 * the control flow may be split between different branches
 */
public interface Branching {

	/**
	 * an object for logging trace statements
	 */
	public final static Logger LOGGER = ChocoLogging.getBranchingLogger();
	
	/**
	 * selecting the object under scrutiny (that object on which an alternative will be set)
	 *
	 * @return the object on which an alternative will be set (often  a variable)
	 */
	public Object selectBranchingObject() throws ContradictionException;
}
