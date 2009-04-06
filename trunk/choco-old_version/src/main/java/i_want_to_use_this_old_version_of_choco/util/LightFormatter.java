// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.util;

import i_want_to_use_this_old_version_of_choco.Entity;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A class for formatting trace messages in the lightest possible mode
 */
public class LightFormatter extends Formatter {
  // Line separator string.  This is the value of the line.separator
  // property at the moment that the SimpleFormatter was created.
  // private String lineSeparator = "\n";
  private String lineSeparator = System.getProperty("line.separator");

  /**
   * prefixes for log statements (visualize search depth)
   */
  protected static String[] logPrefix = {"", ".", "..", "...", ".....", "......", ".......","........",".........",".........."};

  protected final String getLogPrefix(int n) {
    return logPrefix[n % (logPrefix.length)];
  }

  /**
   * Format the given LogRecord.
   *
   * @param record the log record to be formatted.
   * @return a formatted log record
   */
  public synchronized String format(LogRecord record) {
    StringBuffer sb = new StringBuffer();
    Level lvl = record.getLevel();
    int lvli = Level.FINE.intValue() - lvl.intValue();
    // sb.append(record.getLoggerName());
    //for (int i=0; i<lvli; i++) {
    //  sb.append("  ");
    //}
    if (record.getLoggerName() == "choco") {
      sb.append("===");
      sb.append(record.getMessage());
    } else if (record.getLoggerName().startsWith("dev.i_want_to_use_this_old_version_of_choco.search.branching")) {
      int depth = ((Integer) record.getParameters()[0]).intValue();
      Entity x = (Entity) record.getParameters()[1];
      String op = ((String) record.getParameters()[2]);
      int i = ((Integer) record.getParameters()[3]).intValue();
      sb.append(getLogPrefix(depth) + "[" + depth + "] " + record.getMessage() + x + op + i);
    } else sb.append(record.getMessage());
    //sb.append(formatMessage(record));
    sb.append(lineSeparator);
    return sb.toString();
  }
}