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
package samples.tutorials;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoggingExample  implements Example {


	
	@Override
	public void execute() {
		for (Verbosity v : Verbosity.values()) {
			execute(v);
		}
	}
	
	@Override
	public void execute(Object parameters) {
		Verbosity verb = (Verbosity) parameters;
		ChocoLogging.flushLogs();
		ChocoLogging.setVerbosity(verb);
		ChocoLogging.getMainLogger().log(Level.SEVERE,"verbosity: {0}",verb);
		new Queen().execute();
		for (Logger logger : ChocoLogging.CHOCO_LOGGERS) {
			final Level l = logger.getLevel();
			logger.log(l, "{1}: {2}", new Object[]{-1, logger.getName(), l});
		}
		
	}

	public static void main(String[] args) {
		new LoggingExample().execute();
	}

}
