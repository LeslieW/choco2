package db;

import static java.lang.System.getProperty;
import static java.sql.Types.*;
import static java.sql.Types.VARCHAR;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import choco.cp.solver.CPSolver;
import choco.cp.solver.search.restart.ParametrizedRestartStrategy;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.AbstractGlobalSearchLimit;





public class DatabaseManager {

	public final static int DEFAULT_COLUMN_WIDTH = 12;

	public final static Logger LOGGER = ChocoLogging.getParserLogger();

	public static final Object NULL = "NULL";

	public static final String ID = "ID";

	/**
	 * Number of bytes per Mo.
	 */
	public static final int Mo = 1048576;

	public final DriverManagerDataSource dataSource = new DriverManagerDataSource();

	protected final JdbcTemplate jdbcTemplate;

	//ID to reuse

	private Integer noRestartStrategyID;
	
	private Integer unknownStrategyID;

	private Integer environmentID;

	public DatabaseManager(File databaseDir, String databaseName) {
		this("jdbc:hsqldb:file:" + databaseDir.getAbsolutePath()+"/"+databaseName);
	}
	public DatabaseManager(String url) {
		super();
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUrl(url);
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		jdbcTemplate = new JdbcTemplate(dataSource);
	}


	public final void close() {
		jdbcTemplate.execute("SHUTDOWN");
	}


	//*****************************************************************//
	//*******************  QUERIES  ********************************//
	//***************************************************************//
	public final static int[] createTypeArray(int length, int type) {
		final int[] r = new int[length];
		Arrays.fill(r, type);
		return r;
	}


	public final Integer getLastGeneratedKey() {
		return jdbcTemplate.queryForInt("CALL IDENTITY()");
	}

	public static final String getSelectPattern(String table, String selection, String... fields) {
		if(fields == null) return null;
		else {
			StringBuilder b = new StringBuilder();
			b.append("SELECT ").append(selection);
			b.append(" FROM ").append(table);
			if(fields != null) {
				b.append(" WHERE ");
				int i;
				for ( i = 0; i < fields.length - 1; i++) {
					b.append(fields[i]).append("=? AND ");
				}
				b.append(fields[i]).append("=? ");
			}
			return new String(b);
		}
	}

	public static final String getInsertPattern(String table, String... fields) {
		if(fields == null) return null;
		else {
			StringBuilder b = new StringBuilder();
			b.append("INSERT INTO ").append(table);
			b.append(" (");
			int i;
			for ( i = 0; i < fields.length - 1; i++) {
				b.append(fields[i]).append(',');
			}
			b.append(fields[i]).append(") VALUES (");
			for (i = 0; i < fields.length - 1; i++) {
				b.append("?,");
			}
			b.append("?)");
			return new String(b);
		}
	}

	public final void insert(final String dbTable, final String[] fields,final Object[] values,final int[] argTypes) {
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(getInsertPattern(dbTable, fields));
				for (int i = 0; i < values.length; i++) {
					ps.setObject( i + 1, values[i], argTypes[i]);	
				}
				return ps;
			}
		};
		//TODO waiting for hsqldb 1.9 to implement getGeneratedkeys
		//avoid one select statement
		//			KeyHolder keyHolder = new GeneratedKeyHolder();
		//			jdbcTemplate.update(psc, keyHolder);
		//			return keyHolder.getKey();
		jdbcTemplate.update(psc);
	}
	/**
	 * 
	 * return the primary key of the entry. the function adds the entry to the table if needed.
	 * @param dbTable
	 * @param primaryKey only one field, the primary key, must be selected
	 * @param fields all required fields for insertion must belong to the query
	 * @param values no values is null
	 * @return the primary key of the entry.
	 */
	public final Integer retrieveKeyOrInsertEntry(final String dbTable, final String primaryKey, 
			final String[] fields,final Object[] values,final int[] argTypes) {
		final String select = getSelectPattern(dbTable, primaryKey, fields);
		try {
			return jdbcTemplate.queryForInt(select, values, argTypes);
		} catch (EmptyResultDataAccessException e) {
			insert(dbTable, fields, values, argTypes);
			return getLastGeneratedKey();
		}
	}

	public final static int INDEX_PK = 0;

	public void insertEntryIfAbsentPK(final String dbTable, final String[] fields,final Object[] values,
			final int[] argTypes) {
		final String select =getSelectPattern(dbTable, "*", fields[INDEX_PK]);
		List<?> query =jdbcTemplate.queryForList(select, new Object[]{ values[INDEX_PK]});
		if( query.isEmpty() ) {
			insert(dbTable, fields, values, argTypes);
		} else {
			LOGGER.log(Level.INFO, "Primary Key {0} is already associated with entry {1} in table {2}", new Object[]{values[INDEX_PK], query,dbTable});
		}
	}



	public final Integer insertEntryAndRetrieveKey(final String dbTable,  
			final String[] fields,final Object[] values,final int[] argTypes) {
		insert(dbTable, fields, values, argTypes);
		return getLastGeneratedKey();
	}

	public final void checkSelectValues(Object[] values) {
		for (int i = 0; i < values.length; i++) {
			if(values[i] == null) {
				LOGGER.info("the {0}th value of a SELECT query is null ! try to repair query by changing to String \"NULL\"");
				values[i] = NULL;
			}
		}
	}


	//*****************************************************************//
	//*******************  T_ENVIRONMENTS  ********************************//
	//***************************************************************//
	/**
	 * return the maximum memory in Mo.
	 */
	protected final static Integer getMaxMemory() {
		return Integer.valueOf( (int) (Runtime.getRuntime().maxMemory()/Mo));
	}


	protected final Integer getRuntimeID() {
		String host;	
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			host = "unknwon-host";
		}
		return retrieveKeyOrInsertEntry(
				"T_RUNTIMES",
				ID,
				new String[]{"HOST", "USER", "MAX_MEMORY"}, 
				new Object[]{host, getProperty("user.name"), getMaxMemory()},
				new int[]{VARCHAR,VARCHAR,INTEGER}
		);
	}




	protected final Integer getOperatingSystemID() {
		return retrieveKeyOrInsertEntry(
				"T_OS", 
				ID,
				new String[]{"NAME", "VERSION", "ARCH"}, 
				new String[]{getProperty("os.name"), getProperty("os.version"), getProperty("os.arch")},
				THREE_VARCHARS
		);
	}

	protected final Integer getJvmID() {
		return retrieveKeyOrInsertEntry(
				"T_JVM", 
				ID,
				new String[]{"NAME", "VERSION", "VENDOR"}, 
				new String[]{getProperty("java.runtime.name"), getProperty("java.version"), getProperty("java.vendor")},
				THREE_VARCHARS
		);
	}

	protected final Integer getEnvironmentID() {
		if(environmentID == null) {
			environmentID = retrieveKeyOrInsertEntry(
					"T_ENVIRONMENTS",
					ID,
					new String[]{ "RUNTIME_ID", "OS_ID", "JVM_ID"}, 
					new Integer[]{ getRuntimeID(), getOperatingSystemID(), getJvmID()},
					new int[]{INTEGER, INTEGER, INTEGER}
			);
		}
		return environmentID;
	}


	//*****************************************************************//
	//*******************  T_INSTANCES  ********************************//
	//***************************************************************//

	protected  final static String[] T_PROBLEMS = {"LABEL","NAME", "CATEGORY"};
	protected  final static int[] THREE_VARCHARS = {VARCHAR, VARCHAR, VARCHAR}; 

	public void safeProblemInsertion(String label, String name, String category) {
		insertEntryIfAbsentPK(
				"T_PROBLEMS",
				T_PROBLEMS,  
				new String[]{label, name, category},
				THREE_VARCHARS
		);
	}

	protected  final static String[] T_INSTANCES = new String[]{"NAME", "PROBLEM_LABEL", "SIZE1","SIZE2"};
	protected  final static int[] T_INSTANCES_TYPES = {VARCHAR, VARCHAR, INTEGER, INTEGER};

	public void safeInstanceInsertion(String instanceName, String problemLabel, int size1, int size2) {
		insertEntryIfAbsentPK(
				"T_INSTANCES",
				T_INSTANCES, 
				new Object[]{ instanceName, problemLabel, Integer.valueOf(size1), Integer.valueOf(size2)},
				T_INSTANCES_TYPES
		);
	}

	protected  final static String[] T_BOUNDS = {"INSTANCE_NAME","LOWER_BOUND", "UPPER_BOUND","IS_OPTIMAL"};
	protected  final static int[] T_BOUNDS_TYPE = {VARCHAR, REAL, REAL, BOOLEAN};

	public void safeBoundsInsertion(final String instanceName,final double lowerBound, final double upperBound, final boolean isOptimal) {
		insertEntryIfAbsentPK(
				"T_BOUNDS",
				T_BOUNDS,
				new Object[]{instanceName, lowerBound, upperBound, isOptimal || lowerBound == upperBound},
				T_BOUNDS_TYPE 
		);
	}

	//*****************************************************************//
	//*******************  T_SOLVERS  *********************************//
	//***************************************************************//

	protected  final static String[] T_MODELS = {
		"NB_CONSTRAINTS", "NB_BOOLVARS", "NB_INTVARS",
		"NB_SETVARS", "NB_TASKVARS", "NB_REALVARS"
	};
	protected  final static int[] T_MODELS_TYPES = {INTEGER,INTEGER,INTEGER,INTEGER,INTEGER,INTEGER};

	public final Integer getModelID(Solver s) {
		return retrieveKeyOrInsertEntry(
				"T_MODELS", 
				ID,
				T_MODELS, 
				new Integer[]{s.getNbIntConstraints(), s.getNbBooleanVars(), s.getNbIntVars(),
						s.getNbSetVars(), s.getNbTaskVars(), s.getNbRealVars()},
						T_MODELS_TYPES
		);
	}

	protected  final static String[] T_MESURES = {
		"STATE_LABEL","NB_SOLUTIONS","OBJECTIVE",
		"TIME",	"CPU_TIME","NODES","BACKTRACKS","NB_ITERATIONS"
	};

	protected  final static int[] T_MESURES_TYPES = {
		VARCHAR, INTEGER, DOUBLE, 
		INTEGER,INTEGER, INTEGER, INTEGER, INTEGER
	};


	public static Double getOptVal(Solver s) {
		final Number obj = s.getOptimumValue();
		return obj == null ? 0 : obj.doubleValue();
	}

	public Integer insertSolverMesure(Solver s) {
		return insertEntryAndRetrieveKey(
				"T_MESURES", 
				T_MESURES, 
				new Object[]{ 
						"SAT", s.getNbSolutions(), getOptVal(s),
						s.getTimeCount(),s.getCpuTimeCount(),s.getNodeCount(),s.getBackTrackCount(), 1
				},
				T_MESURES_TYPES
		);
	}

	public Integer insertSolutionMesure(Solution s) {
		return null;
	}

	protected  final static String[] T_RESTARTS = { "POLICY", "SCALE_FACTOR", "GEOM_FACTOR"};

	protected  final static int[] T_RESTARTS_TYPES = { VARCHAR, DOUBLE, DOUBLE};


	private final Integer getRestartStrategyID(Object[] values) {
		return retrieveKeyOrInsertEntry(
				"T_RESTARTS", 
				"ID",
				T_RESTARTS, 
				values, 
				T_RESTARTS_TYPES
		);
	}

	public final Integer getNoRestartStrategyID() {
		if( noRestartStrategyID == null) {
			noRestartStrategyID = getRestartStrategyID(new Object[]{ NULL, 1, 1});
		}
		return noRestartStrategyID;
	}

		
	public final Integer getRestartStrategyID(Solver s) {
		if (s instanceof CPSolver) {
			final CPSolver cps = (CPSolver) s;
			if ( cps.getRestartStrategy() != null && cps.getRestartStrategy() instanceof ParametrizedRestartStrategy) {
				final ParametrizedRestartStrategy prs = (ParametrizedRestartStrategy) cps.getRestartStrategy();
				return getRestartStrategyID( new Object[]{ prs.getRestartPolicy(), prs.getScaleFactor(), prs.getGeometricalFactor()});
			}
		}
		return getNoRestartStrategyID();
	}
	
	protected  final static String[] T_STRATEGIES = { "BRANCHING", "VAR_SELECTOR", "VAL_SELECTOR"};
	
	private final Integer getStrategyID(Object[] values) {
		return retrieveKeyOrInsertEntry(
				"T_STRATEGIES", 
				"ID",
				T_STRATEGIES, 
				values, 
				THREE_VARCHARS
		);
	}
	
	public final Integer getUnknwonStrategyID() {
		if( unknownStrategyID == null) {
			unknownStrategyID = getStrategyID(new Object[]{NULL,NULL,NULL});
		}
		return unknownStrategyID;
	}
	
	public final Integer getStrategyID(Solver s) {
		return getUnknwonStrategyID();
	}
	
	protected  final static String[] T_SOLVERS = { "EXECUTION_ID", "MODEL_ID", "STRATEGY_ID", "RESTART_ID"};
	protected  final static int[] T_SOLVERS_TYPES = {INTEGER, INTEGER, INTEGER, INTEGER};
	
	public final Integer getSolverID(Integer executionID, Solver s) {
		return insertEntryAndRetrieveKey("T_SOLVERS", 
					T_SOLVERS, 
					new Object[]{ executionID, getModelID(s), getStrategyID(s), getRestartStrategyID(s)},
					T_SOLVERS_TYPES);
	}
	
	//*****************************************************************//
	//*******************  T_LIMITS  ********************************//
	//***************************************************************//
	
	protected  final static String[] T_LIMITS = { "SOLVER_ID", "MESURE_ID"};
	
	protected  final static int[] T_LIMITS_TYPES = { INTEGER, INTEGER};
	
	public final void insertLimits(Integer solverID, Integer measureID) {
		insert("T_LIMITS", T_LIMITS, new Object[]{solverID, measureID}, T_LIMITS_TYPES);
	}
	
	//*****************************************************************//
	//*******************  EXECUTION  ********************************//
	//***************************************************************//
	
	protected  final static String[] T_EXECUTIONS = {"TIMESTAMP", "INSTANCE_NAME", "MODE_LABEL", "ENVIRONMENT_ID", "SEED"};
	
	protected  final static int[] T_EXECUTIONS_TYPES = { TIMESTAMP, VARCHAR, VARCHAR, INTEGER, INTEGER};
	
	public final Integer getExecutionID(String instanceName, String modeLabel) {
		return getExecutionID(instanceName, modeLabel, 0);
	}
	
	public final Integer getExecutionID(String instanceName, String modeLabel, int seed) {
		return insertEntryAndRetrieveKey("T_EXECUTIONS",
				T_EXECUTIONS, 
				new Object[]{ new Timestamp(System.currentTimeMillis()), instanceName, modeLabel, getEnvironmentID(), seed}, 
				T_EXECUTIONS_TYPES);
	}
	//*****************************************************************//
	//*******************  DISPLAY  ********************************//
	//***************************************************************//

	private final static String format(Object o, int width) {
		String str = o == null ? "null" : o instanceof String ? (String) o : o.toString();
		return str.length() <= width ? String.format("%1$"+ width +"s", str) : str.substring(0, width);
	}

	public String displayTable(final String dbTable) {
		return displayTable(dbTable, DEFAULT_COLUMN_WIDTH);
	}
	public String displayTable(final String dbTable, final int columnWidth) {
		final StringBuilder b =new StringBuilder();
		b.append("TABLE: ").append(dbTable).append('\n');
		RowCallbackHandler rch = new RowCallbackHandler() {
			public void processRow(ResultSet res) throws SQLException {
				final ResultSetMetaData rmd = res.getMetaData();
				if(res.getRow() == 1) {
					//insert header
					b.append('|');
					for (int i = 1; i < rmd.getColumnCount() + 1; i++) {
						b.append(format(rmd.getColumnName(i), columnWidth)).append('|');
					}
					b.append('\n');
				}
				//display row
				b.append('|');
				for (int i = 1; i < rmd.getColumnCount() + 1; i++) {
					b.append(format(res.getObject(i),columnWidth)).append('|');
				}
				b.append('\n');
			}
		};
		jdbcTemplate.query("SELECT * FROM "+dbTable, rch);
		return new String(b);

	}

	public final void test() {
		//		LOGGER.log(Level.INFO, "display modes: {0}",jdbcTemplate.queryForList("select LABEL from T_MODES",String.class));
		//		LOGGER.log(Level.INFO, "count modes: {0}",jdbcTemplate.queryForInt("select count(0) from T_MODES"));
		//		LOGGER.log(Level.INFO, "count null mode: {0}",jdbcTemplate.queryForInt("select count(0) from T_MODES WHERE LABEL = ?", new Object[]{null}, new int[]{VARCHAR}));
		//		LOGGER.log(Level.INFO, "insert MULTI in table: {0}",jdbcTemplate.update("insert into T_MODES (LABEL) VALUES ('MULTI')"));
		//		LOGGER.log(Level.INFO, "display modes: {0}",jdbcTemplate.queryForList("select LABEL from T_MODES",String.class));
		//		LOGGER.log(Level.INFO, "count modes: {0}",jdbcTemplate.queryForInt("select count(0) from T_MODES"));
		//		getOperatingSystemID();
		//		getJvmID();
		//LOGGER.info(displayTable("T_JVM"));
		//

		getEnvironmentID();
		getJvmID();
		getOperatingSystemID();
		getRuntimeID();
		LOGGER.info(displayTable("T_JVM"));
		LOGGER.info(displayTable("T_OS"));
		LOGGER.info(displayTable("T_RUNTIMES"));
		LOGGER.info(displayTable("T_ENVIRONMENTS"));

		safeProblemInsertion("OS", "Open-Shop", "Scheduling");
		safeProblemInsertion("OS", "Open-sHop", "scheduling");
		safeProblemInsertion("JS", "Job-Shop", "scheduling");
		LOGGER.info(displayTable("T_PROBLEMS"));
		safeInstanceInsertion("osp1", "OS", 20, 20);
		safeInstanceInsertion("osp1", "OS", 20, 20); //doublons
		safeInstanceInsertion("osp2", "OS", 20, 20);
		safeInstanceInsertion("jsp1", "JS", 10, 20);
		safeInstanceInsertion("jsp1", "JS", 10, 20);//doublons
		safeInstanceInsertion("jsp2", "JS", 10, 30); 
		LOGGER.info(displayTable("T_INSTANCES"));

		safeBoundsInsertion("osp1", 1000, 1010, false);
		safeBoundsInsertion("jsp1", 1000, 1010, false);
		safeBoundsInsertion("jsp1", 1000, 1010, false);
		safeBoundsInsertion("jsp2", 1000, 1000, false);
		//insertBounds("jsp2345", 1000, 1000, false);
		LOGGER.info(displayTable("T_BOUNDS"));

		Solver s = new CPSolver();
		LOGGER.log(Level.INFO,"MODEL ID: {0}",getModelID(s));
		s.createBoundIntVar("v", 0, 5);
		getModelID(s);
		LOGGER.log(Level.INFO,"MODEL ID: {0}",getModelID(s));
		LOGGER.info(displayTable("T_MODELS"));
		s.solve();
		ChocoLogging.flushLogs();
		;
		
		Integer measureID = insertSolverMesure(s);
//		insertSolverMesure(s);
//		LOGGER.info(displayTable("T_MESURES"));
//		
		Integer execID = getExecutionID("osp1", "SAT");
		Integer solverID = getSolverID(execID, s);
		getSolverID(execID, s);
		//getSolverID(execID, s);
		getExecutionID("jsp1", "MIN", 2);
		LOGGER.info(displayTable("T_RESTARTS"));
		LOGGER.info(displayTable("T_STRATEGIES"));
		LOGGER.info(displayTable("T_MODELS"));
		LOGGER.info(displayTable("T_SOLVERS"));
		LOGGER.info(displayTable("T_EXECUTIONS"));
		
		insertLimits(solverID, measureID);
		//insertLimits(solverID, measureID);
		LOGGER.info(displayTable("T_LIMITS"));
		
		
		//jdbcTemplate.queryForObject("SELECT LABEL FROM T_PROBLEMS WHERE CATEGORY=NULL" , new Object[]{null}, new int[]{VARCHAR}, String.class);
		close();

	}

}
