package choco.kernel.common.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.logging.*;
import static java.util.logging.Logger.getLogger;

public final class ChocoLogging {


	public final static Formatter LIGHT_FORMATTER = new LightFormatter();

	public final static Formatter DETAILED_FORMATTER = new DetailedFormatter();

	public final static Formatter SEARCH_FORMATTER = new SearchFormatter();

	public final static Handler DEFAULT_HANDLER = new StreamHandler(System.out, LIGHT_FORMATTER);

	public final static Handler DETAILED_HANDLER = new StreamHandler(System.out, DETAILED_FORMATTER);

	public final static Handler ERROR_HANDLER = new StreamHandler(System.err, DETAILED_FORMATTER);

	private final static Handler SEARCH_DEFAULT_HANDLER = new StreamHandler(System.out, SEARCH_FORMATTER);

	private final static Handler SEARCH_ERROR_HANDLER = new StreamHandler(System.err, SEARCH_FORMATTER);	


	public final static Logger[] CHOCO_LOGGERS = new Logger[] {
		getLogger("choco"),
		getLogger("choco.kernel"),
		getLogger("choco.kernel.search"),
		getLogger("choco.kernel.search.branching"),
		getLogger("choco.kernel.propagation"),
		getLogger("choco.kernel.memory"),
		getLogger("choco.api"),
		getLogger("choco.api.model"),
        getLogger("choco.api.solver"),
		getLogger("choco.api.parser"),
		getLogger("choco.dev.debug"),	
		getLogger("choco.dev.test"),
		getLogger("choco.user"),
		getLogger("choco.samples"),
	};

	static {
		try {
			setLevel(Level.ALL, DEFAULT_HANDLER, DETAILED_HANDLER, SEARCH_DEFAULT_HANDLER);
			setLevel(Level.WARNING, ERROR_HANDLER, SEARCH_ERROR_HANDLER);
			setDefaultHandler();
			setVerbosity(Verbosity.SILENT);
		} catch (AccessControlException e) {
			// Do nothing if this is an applet !
			// TODO: see how to make it work with an applet !
		}
	}

	private ChocoLogging() {
		super();
	}

	public static Logger makeUserLogger(String suffix) {
		return Logger.getLogger("choco.user."+suffix);
	}

	public static Logger getChocoLogger() {
		return CHOCO_LOGGERS[0];
	}

	public static Logger getKernelLogger() {
		return CHOCO_LOGGERS[1];
	}

	public static Logger getSearchLogger() {
		return CHOCO_LOGGERS[2];
	}

	public static Logger getBranchingLogger() {
		return CHOCO_LOGGERS[3];
	}

	public static Logger getPropagationLogger() {
		return CHOCO_LOGGERS[4];
	}


	public static Logger getMemoryLogger() {
		return CHOCO_LOGGERS[5];
	}

		
	public static Logger getAPILogger() {
		return CHOCO_LOGGERS[6];
	}

	public static Logger getModelLogger() {
		return CHOCO_LOGGERS[7];
	}

    public static Logger getSolverLogger() {
		return CHOCO_LOGGERS[8];
	}

	public static Logger getParserLogger() {
		return CHOCO_LOGGERS[9];
	}


	public static Logger getDebugLogger() {
		return CHOCO_LOGGERS[10];
	}

	public static Logger getTestLogger() {
		return CHOCO_LOGGERS[11];
	}

	public static Logger getUserLogger() {
		return CHOCO_LOGGERS[12];
	}
	
	public static Logger getSamplesLogger() {
		return CHOCO_LOGGERS[13];
	}

	public static Formatter getDefaultFormatter() {
		return LIGHT_FORMATTER;
	}


	public static Formatter getDefaultSearchFormatter() {
		return SEARCH_FORMATTER;
	}

	public static void flushLog(Logger logger) {
		for (Handler h : logger.getHandlers()) {
			h.flush();
		}
	}


	public static void flushLogs() {
		//CPSolver.flushLogs();
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

	public static void setDefaultHandler() {
		clearHandlers();
		Logger log = getChocoLogger();
		log.setUseParentHandlers(false);
		log.addHandler(DEFAULT_HANDLER);
		log.addHandler(ERROR_HANDLER);
		//search handlers
		log = getSearchLogger();
		log.setUseParentHandlers(false);
		log.addHandler(SEARCH_DEFAULT_HANDLER);
		log.addHandler(SEARCH_ERROR_HANDLER);
		//debug handlers
		log = getDebugLogger();
		log.setUseParentHandlers(false);
		log.addHandler(DETAILED_HANDLER);
		log.addHandler(ERROR_HANDLER);
	}


	public static void setLevel(Level level, Handler...handlers){
		for (Handler h : handlers) {
			h.setLevel(level);
		}

	}

	private static Handler[] createHandlers( OutputStream stream, Level level) {
		Handler[] handlers = new Handler[] {
				new StreamHandler(stream, LIGHT_FORMATTER),
				new StreamHandler(stream, DETAILED_FORMATTER),
				new StreamHandler(stream, SEARCH_FORMATTER)
		};
		setLevel(level, handlers);
		return handlers;
	}

	
	
	public static OutputStream setErrorFileHandler(File file) {
		try {
			final OutputStream stream = new FileOutputStream(file);
			final Handler[] h = createHandlers(stream, Level.WARNING);
			clearHandlers();

			Logger log = getChocoLogger();
			log.setUseParentHandlers(false);
			log.addHandler(h[1]);
			//search 	
			log = getSearchLogger();
			log.setUseParentHandlers(false);
			log.addHandler(h[2]);
			return stream;
		} catch (FileNotFoundException e) {
			getChocoLogger().log(Level.SEVERE, "cant create stream", e);
		}
		return null;
	}
	
	public static OutputStream setFileHandler(File file) {
		return setFileHandler(file, Level.ALL);
	}
	/**
	 * replace handler by a file handler
	 */
	public static OutputStream setFileHandler(File file, Level level) {
		try {
			final OutputStream stream = new FileOutputStream(file);
			final Handler[] h = createHandlers(stream, level);

			clearHandlers();

			Logger log = getChocoLogger();
			log.setUseParentHandlers(false);
			log.addHandler(h[0]);
			//debug
			log = getDebugLogger();
			log.setUseParentHandlers(false);
			log.addHandler(h[1]);
			//search 
			log = getSearchLogger();
			log.setUseParentHandlers(false);
			log.addHandler(h[2]);
			return stream;
		} catch (FileNotFoundException e) {
			getChocoLogger().log(Level.SEVERE, "cant create stream", e);
		}
		return null;
	}



	/**
	 * remove other handlers.
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

	public static OutputStream addErrorFileHandler(File file) {
		try {
			final OutputStream stream = new FileOutputStream(file);
			final Handler[] h = createHandlers(stream, Level.WARNING);
			getChocoLogger().addHandler(h[1]);
			getSearchLogger().addHandler(h[2]);
			addHandler(h[2], getBranchingLogger());
			//others
			addHandler(h[1], getKernelLogger(), getPropagationLogger(), getMemoryLogger(), 
					getAPILogger(), getParserLogger(), getModelLogger(), getUserLogger(), getTestLogger());
			return stream;
		} catch (FileNotFoundException e) {
			getChocoLogger().log(Level.SEVERE, "cant create stream", e);
		}
		return null;
	}

	public static OutputStream addFileHandler(File file) {
		return addFileHandler(file, Level.ALL);
	}

	public static OutputStream addFileHandler(File file, Level level) {
		try {
			final OutputStream stream = new FileOutputStream(file);
			final Handler[] h = createHandlers(stream, level);
			getChocoLogger().addHandler(h[0]);
			getDebugLogger().addHandler(h[1]);
			getSearchLogger().addHandler(h[2]);
			addHandler(h[2], getBranchingLogger());
			//others
			addHandler(h[0], getKernelLogger(), getPropagationLogger(), getMemoryLogger(), 
					getAPILogger(), getParserLogger(), getModelLogger(), getUserLogger(), getTestLogger());
			return stream;
		} catch (FileNotFoundException e) {
			getChocoLogger().log(Level.SEVERE, "cant create stream", e);
		}
		return null;
	}



	public static void setLevel(Level level, Logger... loggers) {
		for (Logger logger : loggers) {
			logger.setLevel(level);
		}
	}

	
	private static void setPattern(Level solLevel) {
		setLevel(Level.WARNING, getBranchingLogger(), getPropagationLogger(), getMemoryLogger(),
				getParserLogger(), getDebugLogger(), getTestLogger());
		setLevel(Level.INFO, getAPILogger(), getModelLogger(), getSamplesLogger(), getUserLogger(), getSolverLogger());
		setLevel(solLevel, getChocoLogger(), getKernelLogger(),getSearchLogger());
	}
	
	public static void setVerbosity(Verbosity verbosity) {
		switch(verbosity) {
		case OFF: {
			setLevel(Level.OFF, CHOCO_LOGGERS);
			break;
		}
		case SILENT: {
			setLevel(Level.SEVERE, CHOCO_LOGGERS);
			setLevel(Level.OFF, getDebugLogger());
			break;
		}

		case VERBOSE: {
			setPattern(Level.INFO); //output only best solution
			break;
		}
		case SOLUTION: { 
			setPattern(Level.FINEST); //output all solutions
			break;
		}
		case SEARCH: {
			setPattern(Level.FINE);
			setLevel(Level.INFO, getBranchingLogger()); //ouput search logs (branching)
			break;
		}
		case DEBUG: {
			setPattern(Level.FINEST); 
			setLevel(Level.FINEST, 
					getBranchingLogger(), getPropagationLogger(), getMemoryLogger(),  //ouput search, propgation and memory logs
					getParserLogger(), getAPILogger(), getUserLogger(), getSolverLogger(), getModelLogger()); //output model logs
			break;
		}
		case FINEST: {
			for (Logger logger : CHOCO_LOGGERS) {
				logger.setLevel(Level.FINEST);
				//modify also handlers
//				for (Handler h : logger.getHandlers()) {
//					h.setLevel(Level.FINEST);
//				}
			}
			break;
		}
		default: {
			getChocoLogger().log(Level.WARNING,"cant set logger verbosity: ${0}",verbosity);
		}
		}


	}




}
