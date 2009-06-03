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
package samples;

import java.util.logging.Level;
import java.util.logging.Logger;

import samples.Examples.Queen;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;

public final class LoggingExample  {


	public static void main(String[] args) {
		for (Verbosity v : Verbosity.values()) {
			ChocoLogging.flushLogs();
			ChocoLogging.setVerbosity(v);
			ChocoLogging.getSamplesLogger().log(Level.SEVERE,"verbosity: {0}",v);
			new Queen().execute(5);
			for (Logger logger : ChocoLogging.CHOCO_LOGGERS) {
				final Level l = logger.getLevel();
				logger.log(l, "{1}: {2}", new Object[]{-1, logger.getName(), l});
			}
		}
	}

}
