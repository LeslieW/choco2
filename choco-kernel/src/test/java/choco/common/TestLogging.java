package choco.common;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;


public class TestLogging {

	
	public static void log(int i, Level level) {
	log(ChocoLogging.CHOCO_LOGGERS[i], level);
	}
	
	public static void log(Logger logger, Level level) {
		logger.log(level, "{1} {2}", new Object[]{3,logger.getName(),level});
	}

	public static void testAll() {
		Logger myLogger = ChocoLogging.makeUserLogger("myUsrLogger");
		for (Verbosity verb : Verbosity.values()) {
			ChocoLogging.setVerbosity(verb);
			ChocoLogging.getChocoLogger().log(Level.SEVERE, "begin {1}", new Object[]{2, verb});
			for (int i = 0; i < ChocoLogging.CHOCO_LOGGERS.length; i++) {
				log(i,Level.FINEST);
				log(i,Level.INFO);
				log(i,Level.WARNING);
				log(i,Level.SEVERE);
				ChocoLogging.flushLogs();
			}
			log(myLogger,Level.FINEST);
			log(myLogger,Level.INFO);
			log(myLogger,Level.WARNING);
			log(myLogger,Level.SEVERE);
			ChocoLogging.getChocoLogger().log(Level.SEVERE, "end ${1}\n\n\n\n", new Object[]{2, verb});
			ChocoLogging.flushLogs();
		}
		
	}
	

	public static void main(String[] args) {
		ChocoLogging.getChocoLogger().severe("before file handler");
		ChocoLogging.flushLogs();
		ChocoLogging.setFileHandler(new File("/tmp/test.log"));
		testAll();
		
		//ChocoLogging.addFileHandler(new File("/tmp/choco.log"));
		//testAll();
		
	}
}
