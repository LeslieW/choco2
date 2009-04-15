package choco.kernel.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;

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
