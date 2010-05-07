package parser.instances;

import choco.cp.solver.preprocessor.PreProcessConfiguration;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.search.limit.Limit;

import java.io.File;


public class BasicSettings extends PreProcessConfiguration{

	private static final long serialVersionUID = 7557235241412627008L;

	/**
	 * <br/><b>Goal</b>: time limit of a preprocessing step.
	 * <br/><b>Type</b>: int
	 * <br/><b>Default value</b>: 15
	 */
	@Default(value = "15")
	public static final String TIME_LIMIT_PREPROCESSING = "tools.preprocessing.limit.time.value";

	/**
	 * <br/><b>Goal</b>: indicates that the constraint model use light propagation algorithms (for example, it decomposes some global constraints).
	 * <br/><b>Type</b>: boolean
	 * <br/><b>Default value</b>: false
	 */
	@Default(value = VALUE_FALSE)
	public static final String LIGHT_MODEL = "tools.cp.model.light";

	/**
	 * <br/><b>Goal</b>: indicates if selection is random in value-selection heuristics.
	 * <br/><b>Type</b>: boolean
	 * <br/><b>Default value</b>: false
	 */
	@Default(value = VALUE_FALSE)
	public static final String RANDOM_VALUE = "tools.random.value";

	/**
	 * <br/><b>Goal</b>: indicates if the ties are broken randomly in variable-selection or value-selection heuristics.
	 * <br/><b>Type</b>: boolean
	 * <br/><b>Default value</b>: false
	 */
	@Default(value = VALUE_FALSE)
	public static final String RANDOM_TIE_BREAKING = "tools.random.break_tie";

	/**
	 * <br/><b>Goal</b>: indicates that the constraint programming step is cancelled (only preprocessing step).
	 * <br/><b>Type</b>: boolean
	 * <br/><b>Default value</b>: false
	 */
	@Default(value = VALUE_FALSE)
	public static final String CANCEL_CP_SOLVE= "tools.cp.cancel";


	/**
	 * <br/><b>Goal</b>: indicates that the best solution is reported.
	 * <br/><b>Type</b>: boolean
	 * <br/><b>Default value</b>: false
	 */
	@Default(value = VALUE_FALSE)
	public static final String SOLUTION_REPORT = "tools.solution.report";

	/**
	 * <br/><b>Goal</b>: indicates that the best solution is exported.
	 * <br/><b>Type</b>: boolean
	 * <br/><b>Default value</b>: false
	 */
	@Default(value = VALUE_FALSE)
	public static final String SOLUTION_EXPORT = "tools.solution.export";


	private static final String TMPDIR_PROPPERTY = "java.io.tmpdir";
	/**
	 * <br/><b>Goal</b>: indicates that the best solution is exported.
	 * <br/><b>Type</b>: File
	 * <br/><b>Default value</b>: TMP (codename of property java.io.tmpdir"
	 */
	@Default(value = TMPDIR_PROPPERTY)
	public static final String OUTPUT_DIRECTORY = "tools.output.directory";


	public BasicSettings() {
		super();
	}

	public static File getOutputDirectory(Configuration conf) {
		final String path = conf.readString(OUTPUT_DIRECTORY);
		return new File( path.equals(TMPDIR_PROPPERTY) ?
				System.getProperty(TMPDIR_PROPPERTY) : path 
		);
	}
	public static void updateTimeLimit(Configuration conf, long delta) {
		final Limit lim = conf.readEnum(Configuration.SEARCH_LIMIT, Limit.class);
		if( Limit.TIME.equals(lim)) {
			final int limVal = (int) (conf.readInt(SEARCH_LIMIT_BOUND) + delta);
			if(limVal > 0) conf.putInt(SEARCH_LIMIT_BOUND, limVal);
			else conf.putInt(SEARCH_LIMIT_BOUND, 0);
		}
	}
	

	public static String getInstModelMsg(Configuration conf) {
		final StringBuilder b = new StringBuilder(32);
		if( conf.readBoolean(LIGHT_MODEL)) b.append("LIGHT_MODEL    ");
		if( conf.readBoolean(RANDOM_VALUE)) b.append("RAND_VAL    ");
		if( conf.readBoolean(RANDOM_TIE_BREAKING)) b.append("RAND_TIE_BREAKING");
		return new String(b);
	}


}