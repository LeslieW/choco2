package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementSetter;

import choco.kernel.solver.search.measures.IMeasures;

public final class DbTables {

	
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

	public final static DbTableView T_RESTARTS = new DbTableView (
			"T_RESTARTS",
			new String[]{"ID", "POLICY", "SCALE_FACTOR", "GEOM_FACTOR"},
			new String[]{null, "name", "scaleFactor", "geometricalFactor"}
	);


	public final static DbTableView T_STRATEGIES = new DbTableView (
			"T_STRATEGIES",
			new String[]{"ID","BRANCHING", "VAR_SELECTOR", "VAL_SELECTOR"},
			new String[]{ null,"branching", "varSelector", "valSelector"}
	);

	public final static DbTableView T_SOLVERS = new DbTableView (
			"T_SOLVERS",
			"ID", "EXECUTION_ID","INSTANCE_NAME", "HAS_ENCOUNTERED_LIMIT", "MODEL_ID", "STRATEGY_ID", "RESTART_ID", "DESCRIPTION", "SEED"
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
					"TIME", "NODES","BACKTRACKS","FAILS", "NB_ITERATIONS"
			},
			new String[] {
					null, "solutionCount", "objectiveValue", 
					"timeCount", "nodeCount", "backTrackCount", "failCount", "iterationCount"
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

	//*****************************************************************//
	//*******************  T_EXECUTIONS  *****************************//
	//***************************************************************//
	
	public final static DbTableView T_EXECUTIONS = new DbTableView (
			"T_EXECUTIONS",
			"ID", "ENVIRONMENT_ID", "TIMESTAMP", "SEED"
	);
	
	//*****************************************************************//
	//*******************  T_INSTANCES  ******************//
	//***************************************************************//
	
	public final static DbTableView T_PROBLEMS = new DbTableView (
			"T_PROBLEMS",
			new String[] {"LABEL","NAME", "CATEGORY"},
			new String[] {"label","name", "category"}
	);
	
	
	public final static DbTableView T_BOUNDS = new DbTableView (
			"T_BOUNDS",
			new String[] {"INSTANCE_NAME","LOWER_BOUND", "UPPER_BOUND","IS_OPTIMAL"},
			new String[]{ "instanceName", "lowerBound", "upperBound", "optimal"}
	);
	
	
	public final static DbTableView T_INSTANCES = new DbTableView (
			"T_INSTANCES",
			new String[] {"NAME", "PROBLEM_LABEL", "SIZE1","SIZE2"},
			new String[] {"name", "problemLabel", "size1", "size2"}
	);
	
	



}
