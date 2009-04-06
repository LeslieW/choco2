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

package choco.kernel.common.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A class for formatting trace messages in the lightest possible mode
 */
public class LightFormatter extends Formatter {
	// Line separator string. This is the value of the line.separator
	// property at the moment that the SimpleFormatter was created.
	// private String lineSeparator = "\n";
	private final static String lineSeparator = System
			.getProperty("line.separator");

	/**
	 * prefixes for log statements (visualize search depth)
	 */
	private final static String[] logPrefix = { "", ".", "..", "...", "....",
			".....", "......", ".......", "........", ".........", ".........." };

	private final static String getLogPrefix(int n) {
		return logPrefix[n % (logPrefix.length)];
	}

	public synchronized String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		// Level lvl = record.getLevel();
		// int lvli = Level.FINE.intValue() - lvl.intValue();
		// sb.append(record.getLoggerName());
		// for (int i=0; i<lvli; i++) {
		// sb.append("  ");
		// }
		if ("choco".equals(record.getLoggerName())) {
			sb.append("===").append(record.getMessage());

		} else if (record.getLoggerName().startsWith(
				"choco.kernel.solver.search.branching")) {
			final Object[] parameters = record.getParameters();
			int depth = (Integer) parameters[0];
			// Object x = parameters[1];
			// String op = (String) parameters[2];
			// int i = (Integer) parameters[3];
			sb.append(getLogPrefix(depth)).append('[').append(depth).append(
					"] ").append(record.getMessage()).append(parameters[1])
					.append(parameters[2]).append(parameters[3]);
		} else
			sb.append(record.getMessage());
		// sb.append(formatMessage(record));
		sb.append(lineSeparator);
		return sb.toString();
	}
}
