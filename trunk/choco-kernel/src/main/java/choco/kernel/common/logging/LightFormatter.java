package choco.kernel.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

abstract class AbstractFormatter extends Formatter {

	// Line separator string.  This is the value of the line.separator
	// property at the moment that the SimpleFormatter was created.
	protected static final String lineSeparator = (String) java.security.AccessController.doPrivileged(
			new sun.security.action.GetPropertyAction("line.separator"));
	
	protected final void setWarningSign(LogRecord record, StringBuilder buffer) {
		if(record.getLevel().intValue()> Level.INFO.intValue()) {
			buffer.append("/!\\ ");
		}
	}
}

public final class LightFormatter extends AbstractFormatter {

	/**
	 * Format the given LogRecord.
	 * @param record the log record to be formatted.
	 * @return a formatted log record
	 */
	@Override
	public synchronized String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		setWarningSign(record, sb);
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

