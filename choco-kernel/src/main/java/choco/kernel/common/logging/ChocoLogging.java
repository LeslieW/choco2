/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package choco.kernel.common.logging;

import gnu.trove.TObjectIntHashMap;

import java.io.*;
import java.security.AccessControlException;
import java.util.Properties;
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
		"** CHOCO v2.1.1 (April, 2010), Copyleft (c) 1999-2010";

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
            setVerbosity(loadProperties());
		} catch (AccessControlException e) {
			// Do nothing if this is an applet !
			// TODO: see how to make it work with an applet !
		}
	}

    /**
     * Load the properties file and return default value to logging verbosity, if defined.
     * @return Default verbosity
     */
    private static Verbosity loadProperties() {
        try {
            Properties properties = new Properties();
            InputStream is = ChocoLogging.class.getResourceAsStream("/verbosity.properties");
            properties.load(is);
            final String key = "verbosity.level";
            if (!properties.isEmpty()
                    && properties.containsKey(key)) {
                Integer verb = Integer.parseInt(properties.getProperty(key));
                if (verb >= 0 && verb <= 6) {
                    return Verbosity.values()[verb];
                }
            }
        } catch (IOException ignored) {}
        return Verbosity.DEFAULT;
    }

	/**
	 * maximal search depth for logging statements
	 */
	private static int LOGGING_MAX_DEPTH = 25;

	/**
	 * display information about search every x nodes.
	 */
	private static int EVERY_X_NODES = 1000;

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
     * Set verbosity to SILENT <i>(syntaxic sugar)</i>
     */
    public static void toSilent(){
        setVerbosity(Verbosity.SILENT);
    }

    /**
     * Set verbosity to QUIET <i>(syntaxic sugar)</i>
     */
    public static void toQuiet(){
        setVerbosity(Verbosity.QUIET);
    }

    /**
     * Set verbosity to DEFAULT <i>(syntaxic sugar)</i>
     */
    public static void toDefault(){
        setVerbosity(Verbosity.DEFAULT);
    }

    /**
     * Set verbosity to SILENT <i>(syntaxic sugar)</i>
     */
    public static void toVerbose(){
        setVerbosity(Verbosity.VERBOSE);
    }

    /**
     * Set verbosity to SOLUTION <i>(syntaxic sugar)</i>
     */
    public static void toSolution(){
        setVerbosity(Verbosity.SOLUTION);
    }

    /**
     * Set verbosity to SEARCH <i>(syntaxic sugar)</i>
     */
    public static void toSearch(){
        setVerbosity(Verbosity.SEARCH);
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
			switch(verbosity) {
			case SILENT: {
				setLevel(Level.SEVERE,	getEngineLogger(), getSearchLogger(), getBranchingLogger());
				setLevel(Level.WARNING, getMainLogger(), getTestLogger());
				break;
			}
			case QUIET: {
				setLevel(Level.WARNING,	getEngineLogger(), getSearchLogger(), getBranchingLogger());
				setLevel(Level.INFO, getMainLogger(), getTestLogger());
				break;
			}
			case DEFAULT: {
				setLevel(Level.WARNING, getEngineLogger(), getBranchingLogger());
				setLevel(Level.INFO, getMainLogger(), getSearchLogger(), getTestLogger());
				break;
			}
			case VERBOSE: {
				setLevel(Level.INFO, getEngineLogger(), getBranchingLogger());
				setLevel(Level.CONFIG, getMainLogger(), getSearchLogger(), getTestLogger());
				break;
			}
			case SOLUTION: { 
				setLevel(Level.INFO, getBranchingLogger());
				setLevel(Level.CONFIG,getEngineLogger(), getTestLogger());
				setLevel(Level.FINER, getMainLogger(), getSearchLogger());
				break;
			}
			case SEARCH: {
				setLevel(Level.CONFIG, getBranchingLogger(), getTestLogger());
				setLevel(Level.FINER, getEngineLogger());
				setLevel(Level.FINEST, getMainLogger(), getSearchLogger());
				break;
			}
			default: {
				setVerbosity(Verbosity.VERBOSE);
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
