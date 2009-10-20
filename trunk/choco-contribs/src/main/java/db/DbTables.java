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
package db;


public final class DbTables {

	//*****************************************************************//
	//*******************  CONSTANTS  ********************************//
	//***************************************************************//
	/**
	 * Number of bytes per Mo.
	 */
	public static final int Mo = 1048576;

	public static final String ID = "ID";

	public static final String NULL = "NULL";

	public final static String CALL_IDENTITY = "CALL IDENTITY()";

	public final static Object[] EMPTY_MODEL = {0, 0, 0, 0, 0, 0};
	
	//*****************************************************************//
	//*******************  T_SOLVERS  ********************************//
	//***************************************************************//

	public final static DbTableView T_MODELS = new DbTableView(
			"T_MODELS",
			new String[]{
					"ID", "NB_CONSTRAINTS", "NB_BOOLVARS", "NB_INTVARS",
					"NB_SETVARS", "NB_TASKVARS", "NB_REALVARS"
			},
			new String[]{
					null, "nbIntConstraints", "nbBooleanVars", "nbIntVars",
					"nbSetVars", "nbTaskVars", "nbRealVars"
			}
	);

	public final static DbTableView T_CONFIGURATIONS = new DbTableView (
			"T_CONFIGURATIONS",
			new String[] {"ID","SOLVER_ID", "DESCRIPTION"}
	);
	
	public final static DbTableView T_DIAGNOSTICS = new DbTableView (
			"T_DIAGNOSTICS",
			new String[] {"ID","SOLVER_ID", "DESCRIPTION"}
	);

	public final static DbTableView T_SOLVERS = new DbTableView (
			"T_SOLVERS",
			"ID","INSTANCE_NAME", "STATUS", "RUNTIME", "SOLUTION", "MODEL_ID", "ENVIRONMENT_ID", "SEED", "TIMESTAMP"
	);


	//*****************************************************************//
	//*******************  T_MEASURES, T_LIMITS  *********************//
	//***************************************************************//

	public final static DbTableView T_LIMITS= new DbTableView (
			"T_LIMITS",
			"MEASURE_ID", "SOLVER_ID"
	);

	public final static DbTableView T_MEASURES = new DbTableView (
			"T_MEASURES",
			new String[] {
					"ID","NB_SOLUTIONS","OBJECTIVE",
					"TIME", "NODES","BACKTRACKS","FAILS", "RESTARTS"
			},
			new String[] {
					null, "solutionCount", "objectiveValue", 
					"timeCount", "nodeCount", "backTrackCount", "failCount", "restartCount"
			}
	);


	//*****************************************************************//
	//*******************  T_ENVIRONMENT  ****************************//
	//***************************************************************//

	public final static DbTableView T_RUNTIMES = new DbTableView (
			"T_RUNTIMES",
			"ID", "HOST", "USER", "MAX_MEMORY"
	);


	public final static DbTableView T_OS = new DbTableView (
			"T_OS",
			"ID", "NAME", "VERSION", "ARCH"
	);


	public final static DbTableView T_JVM = new DbTableView (
			"T_JVM",
			"ID", "NAME", "VERSION", "VENDOR"
	);

	public final static DbTableView T_ENVIRONMENTS = new DbTableView (
			"T_ENVIRONMENTS",
			"ID", "RUNTIME_ID", "OS_ID", "JVM_ID"
	);






}
