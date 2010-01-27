package parser.instances;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solver;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BasicSettings {

	public final static Logger LOGGER= ChocoLogging.getMainLogger();

	protected static final String PP = "chocotools.";
	/**
	 * time limit of the solver in seconds
	 */
	private int timeLimit = 24*3600; //24h

	/**
	 * time limit of the preprocessor in seconds
	 */
	private int timeLimitPP = 24*3600; //24h


	/**
	 * indicates that the model use light propagation algorithms (for example, it decomposes some glabla constraints).
	 */
	public boolean lightModel = false;

	/**
	 * indicates if selection is random in value-selection heuristics.
	 */
	public boolean randomValue = false;

	/**
	 * indicates if the ties are broken randomly in variable-selection or value-selection heuristics.
	 */
	public boolean randomBreakTies = true;



	public BasicSettings() {
		super();
	}



	protected BasicSettings(BasicSettings set) {
		super();
		this.timeLimit = set.timeLimit;
		this.timeLimitPP = set.timeLimitPP;
		this.randomValue = set.randomValue;
		this.randomBreakTies = set.randomBreakTies;
		this.lightModel = set.lightModel;
	}




	public final boolean isRandomValue() {
		return randomValue;
	}



	public final void setRandomValue(boolean randomValue) {
		this.randomValue = randomValue;
	}



	public final boolean isRandomBreakTies() {
		return randomBreakTies;
	}


	public final void setRandomBreakTies(boolean randomBreakTies) {
		this.randomBreakTies = randomBreakTies;
	}


	public final boolean isLightModel() {
		return lightModel;
	}

	public final void setLightModel(boolean lightModel) {
		this.lightModel = lightModel;
	}

	public final int getTimeLimit() {
		return timeLimit;
	}


	/**
	 * set a positive time limit for the heuristics.
	 */
	public final void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public final int getTimeLimitPP() {
		return timeLimitPP;
	}


	/**
	 * set a positive time limit for the heuristics.
	 */
	public  final void setTimeLimitPP(int timeLimitPP) {
		this.timeLimitPP = timeLimitPP;
	}


	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if(timeLimit > 0) b.append(timeLimit).append(" TIMELIMIT");
		if( lightModel) b.append("    LIGHT_MODEL");
		return b.toString();
	}



	public final void applyTimeLimit(Solver s) {
		if( timeLimit > 0) s.setTimeLimit(timeLimit * 1000);
	}


	public void configure(Properties properties) {
		timeLimit = readInteger(properties, PP+"timelimit.cp", timeLimit);
		timeLimitPP = readInteger(properties, PP+"timelimit.pp", timeLimit);
		lightModel = readBoolean(properties, PP+"lightmodel", lightModel);
		randomValue = readBoolean(properties, PP+"random.value", randomValue);
		randomValue = readBoolean(properties, PP+"random.breaktie", randomValue);
	}

	protected final static boolean readBoolean(Properties properties, final String key, boolean defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			LOGGER.log(Level.CONFIG, "properties...[key:{0}][MISSING]", key);
			return defaultValue;
		} else return Boolean.parseBoolean(b);
	}


	protected final static int readInteger(Properties properties, final String key, int defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			LOGGER.log(Level.CONFIG, "properties...[key:{0}][MISSING]", key);
			return defaultValue;
		} else return Integer.parseInt(b);
	}

	protected final static double readDouble(Properties properties, final String key, double defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			LOGGER.log(Level.CONFIG, "properties...[key:{0}][MISSING]", key);
			return defaultValue;
		} else return Double.parseDouble(b);
	}

	protected final static String readString(Properties properties, final String key, String defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			LOGGER.log(Level.CONFIG, "properties...[key:{0}][MISSING]", key);
			return defaultValue;
		} else return b;
	}
	
	@SuppressWarnings("unchecked")
	protected final static   <T extends Enum<T>> T readEnum(Properties properties, final String key, T defaultValue) {
		final String b = properties.getProperty(key);
		if( b == null ) {
			LOGGER.log(Level.CONFIG, "properties...[key:{0}][MISSING]", key);
			return defaultValue;
		} else return (T) Enum.valueOf(defaultValue.getClass(), b);
	}




}