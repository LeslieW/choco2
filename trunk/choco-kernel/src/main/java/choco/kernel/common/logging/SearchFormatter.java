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
package choco.kernel.common.logging;

import java.util.logging.LogRecord;

import choco.kernel.solver.Solver;



/**
 * @author Arnaud Malapert</br> 
 * @since 16 avr. 2009 version 2.1.0</br>
 * @version 2.1.0</br>
 */
public final class SearchFormatter extends AbstractFormatter {

	/**
	 * prefixes for log statements (visualize search depth)
	 */
	private final static String[] logPrefix = { "", ".", "..", "...", "....",
		".....", "......", ".......", "........", ".........", ".........." };

	private final static String getLogPrefix(int n) {
		return logPrefix[n % (logPrefix.length)];
	}

	@Override
	public synchronized String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		int depth =-2;
		if(record.getParameters() != null) {
			if (record.getParameters()[0] instanceof Integer) {
				depth = (Integer) record.getParameters()[0];
			}else if(record.getParameters()[0] instanceof Solver) {
				depth = ( (Solver) record.getParameters()[0]).getEnvironment().getWorldIndex();
			}
		}
		if(depth>=0) {
			sb.append(getLogPrefix(depth));
			sb.append("[").append(depth).append("] ");
		}else if(depth == -1) {
			sb.append("=== ");
		}else {
			sb.append("[?] ");
		}
		setWarningSign(record, sb);
		sb.append(formatMessage(record));			
		sb.append(lineSeparator);
		return sb.toString();
	}
}


