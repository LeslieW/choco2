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


	public final static Formatter LIGHT_FORMATTER = new LightFormatter();

	public final static Formatter DETAILED_FORMATTER = new DetailedFormatter();

	public final static Handler DEFAULT_HANDLER = new StreamHandler(System.out, LIGHT_FORMATTER);

	public final static Handler DETAILED_HANDLER = new StreamHandler(System.out, DETAILED_FORMATTER);

	public final static Handler ERROR_HANDLER = new StreamHandler(System.err, DETAILED_FORMATTER);


	public final static Logger[] CHOCO_LOGGERS = new Logger[] {
		getLogger("choco"),

		getLogger("choco.kernel"),
		getLogger("choco.kernel.search"),
		getLogger("choco.kernel.search.branching"),
		getLogger("choco.kernel.engine"),

		getLogger("choco.api"),
		getLogger("choco.api.model"),
		getLogger("choco.api.solver"),
		getLogger("choco.api.parser"),

		getLogger("choco.user"),
		getLogger("choco.user.samples"),

		getLogger("choco.test"),
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

	private ChocoLogging() {
		super();
	}

	/**
	 * create a new user logger with valid name
	 * @param suffix
	 * @return
	 */
	public static Logger makeUserLogger(String suffix) {
		return Logger.getLogger("choco.user."+suffix);
	}
	
	protected static Logger getChocoLogger() {
		return CHOCO_LOGGERS[0];
	}

	protected static Logger getKernelLogger() {
		return CHOCO_LOGGERS[1];
	}

	public static Logger getSearchLogger() {
		return CHOCO_LOGGERS[2];
	}

	public static Logger getBranchingLogger() {
		return CHOCO_LOGGERS[3];
	}

	public static Logger getEngineLogger() {
		return CHOCO_LOGGERS[4];
	}

	protected static Logger getAPILogger() {
		return CHOCO_LOGGERS[5];
	}

	public static Logger getModelLogger() {
		return CHOCO_LOGGERS[6];
	}

	public static Logger getSolverLogger() {
		return CHOCO_LOGGERS[7];
	}

	public static Logger getParserLogger() {
		return CHOCO_LOGGERS[8];
	}


	public static Logger getUserLogger() {
		return CHOCO_LOGGERS[9];
	}

	public static Logger getSamplesLogger() {
		return CHOCO_LOGGERS[10];
	}

	public static Logger getTestLogger() {
		return CHOCO_LOGGERS[11];
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


	public static void setLevel(Level level, Logger... loggers) {
		for (Logger logger : loggers) {
			logger.setLevel(level);
		}
	}


	private static void setCommon() {
		setLevel(Level.FINEST, getChocoLogger(),getKernelLogger(),getAPILogger());
		setLevel(Level.WARNING, getBranchingLogger(), getTestLogger(), getEngineLogger());
		setLevel(Level.INFO, 
				getSearchLogger(), 
				getUserLogger(), getSamplesLogger(), 
				getAPILogger(),getModelLogger(),getSolverLogger(),getParserLogger()
		);
	}
	

	/**
	 * set the choco verbosity level
	 * @param verbosity the new verbosity level
	 */
	public static void setVerbosity(Verbosity verbosity) {
		switch(verbosity) {
		case OFF: setLevel(Level.OFF, CHOCO_LOGGERS);break;
		case SILENT: setLevel(Level.SEVERE, CHOCO_LOGGERS);break;
		case DEFAULT: {
			setCommon();
			setLevel(Level.WARNING,getModelLogger(), getSolverLogger());
			break;
		}
		case VERBOSE: {
			setCommon();
			setLevel(Level.CONFIG,getSearchLogger());
			break;
		}
		case SOLUTION: { 
			setCommon();
			setLevel(Level.FINER,getSearchLogger());
			break;
		}
		case SEARCH: {
			setCommon();
			setLevel(Level.FINEST,getSearchLogger());
			setLevel(Level.INFO,getBranchingLogger());
			break;
		}
		case FINEST: setLevel(Level.FINEST, CHOCO_LOGGERS);break;
		default: {
			getAPILogger().log(Level.WARNING,"cant set logger verbosity: ${0}\n Set default verbosity.",verbosity);
			setVerbosity(Verbosity.DEFAULT);
		}

		}
	}

}
