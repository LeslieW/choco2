package choco.kernel.common.util.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;

public final class PropertyUtils {

	public final static Logger LOGGER= ChocoLogging.getMainLogger();

	public static final String TOOLS_PREFIX = "chocotools.";

	private PropertyUtils() {
		super();
	}

	private static void logOnAbsence(String key) {
		LOGGER.log(Level.CONFIG, "properties...[read-property-from-key:{0}][FAIL]", key);
	}

	private static void logOnFailure(String resource) {
		LOGGER.log(Level.SEVERE, "properties...[load-properties:{0}][FAIL]", resource);
	}

	private static void logOnSuccess(String resource) {
		LOGGER.log(Level.CONFIG, "properties...[load-properties:{0}]", resource);
		ChocoLogging.flushLogs();
	}


	public final static void loadProperties(Properties properties, File... files) {
		for (File file : files) {
			try {
				properties.load(new FileReader(file));
				logOnSuccess(file.getName());
			} catch (FileNotFoundException e) {
				logOnFailure(file.getName());
			} catch (IOException e) {
				logOnFailure(file.getName());
			}
		}
	}

	public final static void loadProperties(Properties properties, String... resources) {
		for (String resource : resources) {
		try {
			properties.load(new InputStreamReader(properties.getClass().getResourceAsStream(resource)));
			logOnSuccess(resource);
		} catch (IOException e) {
			logOnFailure(resource);
		}
		}
	}

	public final static boolean readBoolean(Properties properties, final String key, boolean defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			logOnAbsence(key);
			return defaultValue;
		} else return Boolean.parseBoolean(b);
	}

	public final static int readInteger(Properties properties, final String key, int defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			logOnAbsence(key);
			return defaultValue;
		} else return Integer.parseInt(b);
	}

	public final static double readDouble(Properties properties, final String key, double defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			logOnAbsence(key);
			return defaultValue;
		} else return Double.parseDouble(b);
	}

	public final static String readString(Properties properties, final String key, String defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			logOnAbsence(key);
			return defaultValue;
		} else return b;
	}

	@SuppressWarnings("unchecked")
	public final static   <T extends Enum<T>> T readEnum(Properties properties, final String key, T defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			logOnAbsence(key);
			return defaultValue;
		} else return (T) Enum.valueOf(defaultValue.getClass(), b);
	}




}
