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
package choco.kernel.model;

import choco.kernel.common.logging.ChocoLogging;

/**
 * @author Arnaud Malapert
 *
 */
public class ModelException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -3328606447676273304L;


	/**
	 * @param message
	 */
	public ModelException(String message) {
		super(message);
		ChocoLogging.flushLogs();
	}

	/**
	 * @param cause
	 */
	public ModelException(Throwable cause) {
		super(cause);
		ChocoLogging.flushLogs();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ModelException(String message, Throwable cause) {
		super(message, cause);
		ChocoLogging.flushLogs();
	}

}
