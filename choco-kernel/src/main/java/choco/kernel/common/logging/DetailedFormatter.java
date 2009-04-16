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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;


/**
 * @author Arnaud Malapert</br> 
 * @since 16 avr. 2009 version 2.1.0</br>
 * @version 2.1.0</br>
 */
public final class DetailedFormatter extends AbstractFormatter {

	/**
	 * Format the given LogRecord.
	 * @param record the log record to be formatted.
	 * @return a formatted log record
	 */
	@Override
	public synchronized String format(LogRecord record) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(record.getLevel().getLocalizedName());
		sb.append("] ");
		if (record.getSourceClassName() != null) {	
			sb.append(record.getSourceClassName());
		} else {
			sb.append(record.getLoggerName());
		}
		if (record.getSourceMethodName() != null) {	
			sb.append(" (");
			sb.append(record.getSourceMethodName());
			sb.append(")");
		}
		sb.append(" ");
		sb.append(formatMessage(record));
		sb.append(lineSeparator);
		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ex) {
			}
		}
		return sb.toString();
	}

}
