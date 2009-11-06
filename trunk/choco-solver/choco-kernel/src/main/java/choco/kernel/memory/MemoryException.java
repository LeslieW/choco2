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
package choco.kernel.memory;

import choco.kernel.common.logging.ChocoLogging;

/**
 * @author Arnaud Malapert
 *
 */
public class MemoryException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 7657094104323002125L;

	/**
	 * @param message
	 */
	public MemoryException(String message) {
		super(message);
		ChocoLogging.flushLogs();
	}

	/**
	 * @param cause
	 */
	public MemoryException(Throwable cause) {
		super(cause);
		ChocoLogging.flushLogs();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MemoryException(String message, Throwable cause) {
		super(message, cause);
		ChocoLogging.flushLogs();
	}

}
