package choco.kernel.common.logging;

import java.util.logging.LogRecord;

import choco.kernel.solver.Solver;



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
		int depth =-1;
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
		}else {
			sb.append("[?]");
		}
		setWarningSign(record, sb);
		sb.append(formatMessage(record));			
		sb.append(lineSeparator);
		return sb.toString();
	}
}


