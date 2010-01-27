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

import gnu.trove.TObjectIntHashMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.logging.*;

import static java.util.logging.Logger.getLogger;


/**
 * A final class which handles choco logging statements.
 * Most of choco class propose a static final field LOGGER.
 * @author Arnaud Malapert</br> 
 * @since 16 avr. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public final class ChocoLogging {

	public final static String START_MESSAGE =
		"** CHOCO : Constraint Programming Solver\n"+
		"** CHOCO v2.1.1 (April, 2009), Copyleft (c) 1999-2010";

	public final static Formatter LIGHT_FORMATTER = new LightFormatter();

	public final static Formatter DETAILED_FORMATTER = new DetailedFormatter();

	public final static Handler DEFAULT_HANDLER = new StreamHandler(System.out, LIGHT_FORMATTER);

	public final static Handler DETAILED_HANDLER = new StreamHandler(System.out, DETAILED_FORMATTER);

	public final static Handler ERROR_HANDLER = new StreamHandler(System.err, DETAILED_FORMATTER);

	public final static Logger[] CHOCO_LOGGERS = new Logger[] {
		getLogger("choco"),

		getLogger("choco.core"),
		getLogger("choco.core.engine"),
		getLogger("choco.core.search"),
		getLogger("choco.core.search.branching"),

		getLogger("choco.api"),
		getLogger("choco.api.main"),
		getLogger("choco.api.test"),
	};

	static {
		try {
			setLevel(Level.ALL, DEFAULT_HANDLER, DETAILED_HANDLER);
			setLevel(Level.WARNING, ERROR_HANDLER);
			setDefaultHandler();
			setVerbosity(Verbosity.DEFAULT);
		} catch (AccessControlException e) {
			// Do nothing if this is an applet !
			// TODO: see how to make it work with an applet !
		}
	}

	/**
	 * maximal search depth for logging statements
	 */
	private static int LOGGING_MAX_DEPTH = 25;

	/**
	 * display information about search every x nodes.
	 */
	private static int EVERY_X_NODES = 10;

	private ChocoLogging() {
		super();
	}


	public static final int getEveryXNodes() {
		return EVERY_X_NODES;
	}


	public static final void setEveryXNodes(int everyXnodes) {
		if(everyXnodes > 0) EVERY_X_NODES = everyXnodes;
	}


	/**
	 * set the maximal search depth for logging statements
	 */
	public static final void setLoggingMaxDepth(int loggingMaxDepth) {
		if( loggingMaxDepth > 1) LOGGING_MAX_DEPTH = loggingMaxDepth;
	}

	/**
	 * get the maximal search depth for logging statements
	 */
	public final static int getLoggingMaxDepth() {
		return LOGGING_MAX_DEPTH;
	}


	/**
	 * create a new user logger with valid name
	 * @param name
	 * @return
	 */
	public static Logger makeUserLogger(String name) {
		return Logger.getLogger(getMainLogger().getName()+"."+name);
	}

	protected static Logger getChocoLogger() {
		return CHOCO_LOGGERS[0];
	}

	protected static Logger getCoreLogger() {
		return CHOCO_LOGGERS[1];
	}

	public static Logger getEngineLogger() {
		return CHOCO_LOGGERS[2];
	}

	public static Logger getSearchLogger() {
		return CHOCO_LOGGERS[3];
	}

	public static Logger getBranchingLogger() {
		return CHOCO_LOGGERS[4];
	}

	protected static Logger getAPILogger() {
		return CHOCO_LOGGERS[5];
	}

	public static Logger getMainLogger() {
		return CHOCO_LOGGERS[6];
	}

	public static Logger getTestLogger() {
		return CHOCO_LOGGERS[7];
	}

	public static Formatter getDefaultFormatter() {
		return LIGHT_FORMATTER;
	}



	public static void flushLog(Logger logger) {
		for (Handler h : logger.getHandlers()) {
			h.flush();
		}
	}


	/**
	 * flush pending logs
	 */
	public static void flushLogs() {
		for (Logger logger : CHOCO_LOGGERS) {
			flushLog(logger);
		}
	}

	protected static void clearHandlers() {
		for (Logger logger : CHOCO_LOGGERS) {
			clearHandlers(logger);
		}
	}

	protected static void clearHandlers(Logger logger) {
		logger.setUseParentHandlers(true);
		for (Handler h : logger.getHandlers()) {
			logger.removeHandler(h);
		}
	}

	/**
	 * set the default handler (out and err) for all loggers
	 */
	public static void setDefaultHandler() {
		clearHandlers();
		Logger log = getChocoLogger();
		log.setUseParentHandlers(false);
		log.addHandler(DEFAULT_HANDLER);
		log.addHandler(ERROR_HANDLER);
	}


	public static void setLevel(Level level, Handler...handlers){
		for (Handler h : handlers) {
			h.setLevel(level);
		}

	}

	public static OutputStream setFileHandler(File file, Level level, Formatter formatter) {
		try {
			final OutputStream stream = new FileOutputStream(file);
			Handler handler = new StreamHandler(stream, formatter);
			handler.setLevel(level);
			clearHandlers();
			Logger log = getChocoLogger();
			log.setUseParentHandlers(false);
			log.addHandler(handler);
			return stream;
		} catch (FileNotFoundException e) {
			getChocoLogger().log(Level.SEVERE, "cant create stream", e);
		}
		return null;
	}

	/**
	 * remove handlers and write error logs into a file (warning and severe message)
	 * @param file the error log file
	 * @return the error stream
	 */
	public static OutputStream setErrorFileHandler(File file) {
		return setFileHandler(file, Level.WARNING, DETAILED_FORMATTER);
	}

	/**
	 * remove handlers and write logs into a file
	 * @param file the log file
	 * @return the log stream
	 */
	public static OutputStream setFileHandler(File file) {
		return setFileHandler(file, Level.ALL, LIGHT_FORMATTER);
	}


	/**
	 * remove other handlers and set the only handler for this logger
	 */
	public static void setHandler(Logger logger, Handler handler) {
		clearHandlers(logger);
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);

	}

	protected static void addHandler(Handler handler, Logger...loggers) {
		for (Logger logger : loggers) {
			if( ! logger.getUseParentHandlers()) {
				logger.addHandler(handler);
			}
		}
	}

	public static OutputStream addFileHandler(File file, Level level, Formatter formatter) {
		try {
			final OutputStream stream = new FileOutputStream(file);
			Handler handler = new StreamHandler(stream, formatter);
			handler.setLevel(level);
			addHandler(handler,CHOCO_LOGGERS);
			return stream;
		} catch (FileNotFoundException e) {
			getChocoLogger().log(Level.SEVERE, "cant create stream", e);
		}
		return null;
	}
	/**
	 * add a new handler whichs receive error logs
	 * @param file the error log file
	 * @return the error stream
	 */
	public static OutputStream addErrorFileHandler(File file) {
		return addFileHandler(file, Level.WARNING, DETAILED_FORMATTER);
	}

	/**
	 * add a new handler whichs receive logs
	 * @param file the log file
	 * @return the stream
	 */
	public static OutputStream addFileHandler(File file) {
		return addFileHandler(file, Level.ALL, LIGHT_FORMATTER);
	}

	public static void setLevel(final Level level, final Logger logger) {
		logger.setLevel(level);
	}

	public static void setLevel(final Level level, final Logger... loggers) {
		for (Logger logger : loggers) {
			logger.setLevel(level);
		}
	}

	/**
	 * set the choco verbosity level
	 * @param verbosity the new verbosity level
	 */
	public static void setVerbosity(Verbosity verbosity) {
		if(verbosity == Verbosity.OFF) setLevel(Level.OFF, CHOCO_LOGGERS);
		else if(verbosity == Verbosity.FINEST) setLevel(Level.FINEST, CHOCO_LOGGERS);
		else {
			setLevel(Level.FINEST, getChocoLogger(),getCoreLogger(), getAPILogger());
			setLevel(Level.WARNING,getTestLogger());
			switch(verbosity) {
			case SILENT: {
				setLevel(Level.SEVERE,	getEngineLogger(), getSearchLogger(), getBranchingLogger());
				setLevel(Level.INFO, getMainLogger());
				break;
			}
			case DEFAULT: {
				setLevel(Level.WARNING, getEngineLogger(), getBranchingLogger());
				setLevel(Level.INFO, getMainLogger(), getSearchLogger());
				break;
			}
			case VERBOSE: {
				setLevel(Level.INFO, getEngineLogger(), getBranchingLogger());
				setLevel(Level.CONFIG, getMainLogger(), getSearchLogger());
				break;
			}
			case SOLUTION: { 
				setLevel(Level.INFO, getEngineLogger(), getBranchingLogger());
				setLevel(Level.FINER, getMainLogger(), getSearchLogger());
				break;
			}
			case SEARCH: {
				setLevel(Level.INFO, getEngineLogger());
				setLevel(Level.CONFIG, getBranchingLogger());
				setLevel(Level.FINEST, getMainLogger(), getSearchLogger());
				break;
			}
			default: {
				setVerbosity(Verbosity.DEFAULT);
			}

			}
		}
	}

	public final static String toDotty() {
		final StringBuilder b = new StringBuilder();
		final TObjectIntHashMap<Logger> indexMap = new TObjectIntHashMap<Logger>(CHOCO_LOGGERS.length);
		//Create a node for each logger
		for (int i = 0; i < CHOCO_LOGGERS.length; i++) {
			indexMap.put(CHOCO_LOGGERS[i], i);
			String name = CHOCO_LOGGERS[i].getName();
			final int idx = name.lastIndexOf('.');
			if( idx != -1) name = name.substring(idx + 1);
			b.append(i).append("[ shape=record, label=\"{");
			b.append(name).append("|").append(CHOCO_LOGGERS[i].getLevel());
			b.append("}\"]");
		}
		//Create arcs between a logger and its parent
		for (int i = 1; i < CHOCO_LOGGERS.length; i++) {
			b.append(indexMap.get(CHOCO_LOGGERS[i].getParent()));
			b.append(" -> ").append(i).append('\n');
		}
		return new String(b);
	}

	public static void main(String[] args) {
		System.out.println(toDotty());
	}
}
