package choco.kernel.common.util.tools;

import choco.kernel.common.logging.ChocoLogging;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PropertyUtils {

	private final static Logger LOGGER= ChocoLogging.getMainLogger();

	public static final String TOOLS_PREFIX = "chocotools.";

	private PropertyUtils() {
		super();
	}

	private static void logOnAbsence(String key) {
		LOGGER.log(Level.CONFIG, "properties...[read-property-from-key:{0}][FAIL]", key);
	}

	private static void logOnFailure(String resource) {
		System.err.println("properties...[load-properties:"+resource+"][FAIL]");
	}

	private static void logOnSuccess(String resource) {
		LOGGER.log(Level.CONFIG, "properties...[load-properties:{0}]", resource);
	}


	public static void loadProperties(Properties properties, File... files) {
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

	public static void loadProperties(Properties properties, String... resources) {
		for (String resource : resources) {
		try {
			properties.load(new InputStreamReader(properties.getClass().getResourceAsStream(resource), "ISO-8859-1"));
			logOnSuccess(resource);
		} catch (IOException e) {
			logOnFailure(resource);
		}
		}
	}

	public static boolean readBoolean(Properties properties, final String key, boolean defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			logOnAbsence(key);
			return defaultValue;
		} else return Boolean.parseBoolean(b);
	}

	public static int readInteger(Properties properties, final String key, int defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			logOnAbsence(key);
			return defaultValue;
		} else return Integer.parseInt(b);
	}

	public static double readDouble(Properties properties, final String key, double defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			logOnAbsence(key);
			return defaultValue;
		} else return Double.parseDouble(b);
	}

	public static String readString(Properties properties, final String key, String defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			logOnAbsence(key);
			return defaultValue;
		} else return b;
	}

	@SuppressWarnings("unchecked")
	public static   <T extends Enum<T>> T readEnum(Properties properties, final String key, T defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			logOnAbsence(key);
			return defaultValue;
		} else return (T) Enum.valueOf(defaultValue.getClass(), b);
	}




        }
