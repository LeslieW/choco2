package choco.kernel.common.logging;

import java.util.logging.LogRecord;



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
		final int depth = (Integer) record.getParameters()[0];
		sb.append(getLogPrefix(depth));
		sb.append("[").append(depth).append("] ");
		setWarningSign(record, sb);
		sb.append(formatMessage(record));			
		sb.append(lineSeparator);
		return sb.toString();
	}
}


