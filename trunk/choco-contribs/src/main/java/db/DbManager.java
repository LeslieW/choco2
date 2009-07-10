package db;

import static java.sql.Types.VARCHAR;
import static java.lang.System.getProperty;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import choco.cp.solver.CPSolver;
import choco.cp.solver.search.restart.ParametrizedRestartStrategy;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.measures.IMeasures;
import db.beans.DbInstanceBean;
import db.beans.DbProblemBean;
import db.dao.DbInstance;
import db.dao.DbProblem;
import db.dao.DbStrategy;


public class DbManager {

	public final static int DEFAULT_COLUMN_WIDTH = 12;

	public final static Logger LOGGER = ChocoLogging.getParserLogger();

	public static final String NULL = "NULL";

	public static final String ID = "ID";

	protected  final static int[] THREE_VARCHARS = {VARCHAR, VARCHAR, VARCHAR}; 

	public final DriverManagerDataSource dataSource = new DriverManagerDataSource();

	SingleConnectionDataSource sdataSource;

	protected final JdbcTemplate jdbcTemplate;

	protected final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private Integer environmentID;
	
	private Integer defaultExecutionID;

	public DbManager(File databaseDir, String databaseName) {
		this("jdbc:hsqldb:file:" + databaseDir.getAbsolutePath()+"/"+databaseName);
	}
	public DbManager(String url) {
		super();
		LOGGER.info("fetching data source ...");
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUrl(url);
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		sdataSource = new SingleConnectionDataSource(DataSourceUtils.getConnection(dataSource), false);
		namedParameterJdbcTemplate = new  NamedParameterJdbcTemplate(sdataSource);
		jdbcTemplate = new JdbcTemplate(sdataSource);
	}


	public final void close() {
		jdbcTemplate.execute("SHUTDOWN");
	}


	//*****************************************************************//
	//*******************  QUERIES  ********************************//
	//***************************************************************//

	protected final boolean containsPrimaryKey(final DbTableView dbTable, Object pkValue) {
		final int count = jdbcTemplate.queryForInt( dbTable.createCountPKQuery(), new Object[]{pkValue});
		if( count > 1) {
			LOGGER.warning("invalid query: find duplicate primary key.");
		}
		return count > 0;
	}

	protected final boolean containsPrimaryKey(final DbTableView dbTable, SqlParameterSource parameters) {
		final int count = namedParameterJdbcTemplate.queryForInt( dbTable.createCountPKQuery(), parameters);
		if( count > 1) {
			LOGGER.warning("invalid query: find duplicate primary key.");
		}
		return count > 0;
	}

	public void insertEntryIfAbsentPK(final DbTableView dbTable,final SqlParameterSource parameters) {
		if( ! containsPrimaryKey(dbTable, parameters)) {
			namedParameterJdbcTemplate.update(dbTable.createInsertQuery(false), parameters);
		}
	}

	public void insertEntryIfAbsentPK(final DbTableView dbTable,final Object... values) {
		if( ! containsPrimaryKey(dbTable, values[DbTableView.INDEX_PK])) {
			jdbcTemplate.update(dbTable.createInsertQuery(false), values);
		}
	}

	public final Integer insertEntryAndRetrieveGPK(final DbTableView dbTable,final SqlParameterSource parameters) {
		namedParameterJdbcTemplate.update(dbTable.createInsertQuery(true), parameters);
		return (Integer) jdbcTemplate.queryForObject(DbConstants.CALL_IDENTITY, Integer.class);
	}

	public final Integer insertEntryAndRetrieveGPK(final DbTableView dbTable,final Object... values) {
		jdbcTemplate.update(dbTable.createInsertQuery(true), values);
		return (Integer) jdbcTemplate.queryForObject(DbConstants.CALL_IDENTITY, Integer.class);
	}


	public final Integer retrieveGPKOrInsertEntry(final DbTableView dbTable,final SqlParameterSource parameters) {
		try {
			return (Integer) namedParameterJdbcTemplate.queryForObject(dbTable.createfindPrimaryKeyQuery(), parameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return insertEntryAndRetrieveGPK(dbTable, parameters);
		}

	}

	public final Integer retrieveGPKOrInsertEntry(final DbTableView dbTable,final Object... values) {
		try {
			return (Integer) jdbcTemplate.queryForObject(dbTable.createfindPrimaryKeyQuery(), values, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return insertEntryAndRetrieveGPK(dbTable, values);
		}

	}




	//*****************************************************************//
	//*******************  T_INSTANCES  ********************************//
	//***************************************************************//


	public void safeProblemInsertion(DbProblem problem) {
		insertEntryIfAbsentPK(DbTables.T_PROBLEMS, new BeanPropertySqlParameterSource(problem));
	}

	public void safeInstanceInsertion(DbInstance instance) {
		safeProblemInsertion(instance.getProblem());
		insertEntryIfAbsentPK(DbTables.T_INSTANCES, new BeanPropertySqlParameterSource(instance));
		if(instance.getBounds() != null) {
			insertEntryIfAbsentPK(DbTables.T_BOUNDS, new BeanPropertySqlParameterSource(instance.getBounds()));
		}

	}


	//*****************************************************************//
	//*******************  T_SOLVERS  *********************************//
	//***************************************************************//


	protected final Integer getModelID(Solver solver) {
		return retrieveGPKOrInsertEntry(DbTables.T_MODELS, new BeanPropertySqlParameterSource(solver));
	}


	protected final Integer getStrategyID(Solver solver) {
		DbStrategy strat = DbConstants.NO_STRATEGY;
		return retrieveGPKOrInsertEntry(DbTables.T_STRATEGIES, new BeanPropertySqlParameterSource(strat));
	}

	protected final Integer getRestartStrategyID(Solver solver) {
		ParametrizedRestartStrategy restarts = DbConstants.NO_RESTARTS;
		if (solver instanceof CPSolver) {
			final CPSolver cps = (CPSolver) solver;
			if ( cps.getRestartStrategy() != null && cps.getRestartStrategy() instanceof ParametrizedRestartStrategy) {
				restarts = (ParametrizedRestartStrategy) cps.getRestartStrategy();
			}
		}
		return retrieveGPKOrInsertEntry(DbTables.T_RESTARTS, new BeanPropertySqlParameterSource(restarts));
	}

	protected final void insertMeasures(Integer solverID, IMeasures m) {
		Integer measuresID = insertEntryAndRetrieveGPK(DbTables.T_MEASURES, new BeanPropertySqlParameterSource(m));
		jdbcTemplate.update(DbTables.T_LIMITS.createInsertQuery(false), new Object[] { measuresID, solverID});
	}

	public final Integer safeSolverInsertion(Solver solver,  DbInstance instance) {
		return safeSolverInsertion(solver, null, instance, null, null);
	}

	public final Integer safeSolverInsertion(Solver solver,  DbInstance instance, Integer seed) {
		return safeSolverInsertion(solver, null, instance, null, seed);
	}
	
	public final Integer safeSolverInsertion(Solver solver,  DbInstance instance, String description, Integer seed) {
		return safeSolverInsertion(solver, null, instance, description, seed);
	}
	
	public final Integer safeSolverInsertion(Solver solver,  Integer executionID, DbInstance instance, String description, Integer seed) {
		//find execution
		if( executionID == null) {
			executionID = getExecutionID(null);
		}
		//insert solver
		safeInstanceInsertion(instance);
		int solverID = insertEntryAndRetrieveGPK(DbTables.T_SOLVERS, new Object[]{
				executionID, instance.getName(), solver.isEncounteredLimit(),
				getModelID(solver), getStrategyID(solver), getRestartStrategyID(solver), description, seed
		});
		//insert measures
		for (Solution sol : solver.getSearchStrategy().getStoredSolutions()) {
			insertMeasures(solverID, sol.getMeasures());
		}
		insertMeasures(solverID, solver);
		return solverID;
	}






	//*****************************************************************//
	//*******************  EXECUTION  ********************************//
	//***************************************************************//

	/**
	 * return the maximum memory in Mo.
	 */
	protected final static Integer getMaxMemory() {
		return Integer.valueOf( (int) (Runtime.getRuntime().maxMemory()/DbConstants.Mo));
	}


	
	protected final Integer getRuntimeID() {
		String host;	
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			host = "unknwon-host";
		}
		return retrieveGPKOrInsertEntry(
				DbTables.T_RUNTIMES, 
				host, getProperty("user.name"), getMaxMemory()
		);
	}




	public final Integer getOperatingSystemID() {
		return retrieveGPKOrInsertEntry(
				DbTables.T_OS, 
				getProperty("os.name"), getProperty("os.version"), getProperty("os.arch")
		);
	}

	protected final Integer getJvmID() {
		return retrieveGPKOrInsertEntry(
				DbTables.T_JVM, 
				getProperty("java.runtime.name"), getProperty("java.version"), getProperty("java.vendor")
		);
	}

	public final Integer getEnvironmentID() {
		if(environmentID == null) {
			environmentID = retrieveGPKOrInsertEntry(
					DbTables.T_ENVIRONMENTS, 
					getRuntimeID(), getOperatingSystemID(), getJvmID()
			);
		}
		return environmentID;
	}

	protected Integer insertExecution(Date date, Integer seed) {
		return insertEntryAndRetrieveGPK(DbTables.T_EXECUTIONS, 
				new Object[]{ getEnvironmentID(), new Date(System.currentTimeMillis()), seed});
	}
	
	public final Integer getExecutionID(Integer seed) {
		final Date date = new Date(System.currentTimeMillis());
		if( seed == null) {
			if(defaultExecutionID == null) {
				defaultExecutionID = insertExecution(date, null);
			}
			return defaultExecutionID;
		}else {
			return insertEntryAndRetrieveGPK(DbTables.T_EXECUTIONS, 
					new Object[]{ getEnvironmentID(), date, seed});
		}
	}


	//*****************************************************************//
	//*******************  DISPLAY  ********************************//
	//***************************************************************//

	private final static String format(Object o, int width) {
		String str = o == null ? "null" : o instanceof String ? (String) o : o.toString();
		return str.length() <= width ? String.format("%1$"+ width +"s", str) : str.substring(0, width);
	}

	public final void printTable(final DbTableView dbTable) {
		if(LOGGER.isLoggable(Level.INFO) ) {
			LOGGER.info(displayTable(dbTable));
		}
	}

	public final void printTable(final String dbTable) {
		if(LOGGER.isLoggable(Level.INFO) ) {
			LOGGER.info(displayTable(dbTable));
		}
	}

	public final String displayTable(final String dbTable) {
		return displayTable(dbTable, DEFAULT_COLUMN_WIDTH);
	}

	public final String displayTable(final DbTableView dbTable) {
		return displayTable(dbTable.getName());
	}


	public final String displayTable(final String dbTable, final int columnWidth) {
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
		jdbcTemplate.query( "SELECT * FROM "+dbTable,rch);
		return new String(b);

	}

	public final void test() {

		getEnvironmentID();		
		LOGGER.info(displayTable("T_JVM"));
		LOGGER.info(displayTable("T_OS"));
		LOGGER.info(displayTable("T_RUNTIMES"));
		LOGGER.info(displayTable("T_ENVIRONMENTS"));

		getExecutionID(null);
		getExecutionID(1);
		LOGGER.info(displayTable(DbTables.T_EXECUTIONS));

		safeInstanceInsertion(DbConstants.UNKNOWN_INSTANCE);
		DbProblem os = new DbProblemBean("OS","Open-Shop", "scheduling");
		DbProblem js = new DbProblemBean("JS","Job-Shop", "scheduling");
		DbInstanceBean inst = new DbInstanceBean("osp1", os);
		inst.setBounds(0, 1, false);
		safeInstanceInsertion(inst);
		safeInstanceInsertion(inst);
		//
		//	safeProblemInsertion("OS", "Open-Shop", "Scheduling");
		//		safeProblemInsertion("OS", "Open-sHop", "scheduling");
		//		safeProblemInsertion("JS", "Job-Shop", "scheduling");
		//		
		//safeInstanceInsertion("osp1", "OS", 20, 20);
		//		safeInstanceInsertion("osp1", "OS", 20, 20); //doublons
		//		safeInstanceInsertion("osp2", "OS", 20, 20);
		//		safeInstanceInsertion("jsp1", "JS", 10, 20);
		//		safeInstanceInsertion("jsp1", "JS", 10, 20);//doublons
		//		safeInstanceInsertion("jsp2", "JS", 10, 30); 
		LOGGER.info(displayTable("T_PROBLEMS"));
		LOGGER.info(displayTable("T_INSTANCES"));
		LOGGER.info(displayTable("T_BOUNDS"));
		//
		//		safeBoundsInsertion("osp1", 1000, 1010, false);
		//		safeBoundsInsertion("jsp1", 1000, 1010, false);
		//		safeBoundsInsertion("jsp1", 1000, 1010, false);
		//		safeBoundsInsertion("jsp2", 1000, 1000, false);
		//		//insertBounds("jsp2345", 1000, 1000, false);

		//
		Solver s = new CPSolver();
		LOGGER.log(Level.INFO,"MODEL ID: {0}",getModelID(s));
		s.createBoundIntVar("v1", 0, 5);
		getModelID(s);
		LOGGER.info(displayTable("T_MODELS"));
		LOGGER.log(Level.INFO,"MODEL ID: {0}",getModelID(s));
		s.createBoundIntVar("v2", 0, 5);
		LOGGER.info(displayTable("T_MODELS"));
		//getModelID(s);
		//jdbcTemplate.execute("COMMIT");
		LOGGER.log(Level.INFO,"MODEL ID: {0}",getModelID(s));
		LOGGER.info(displayTable("T_MODELS"));



		s.solveAll();
		safeSolverInsertion(s, inst);
		printTable(DbTables.T_SOLVERS);
		printTable(DbTables.T_MEASURES);
		//		LOGGER.info(""+insertMeasures(s));
		//		for (Solution sol : s.getSearchStrategy().getStoredSolutions()) {
		//			LOGGER.info(""+insertMeasures(sol));
		//		}
		//		
		//
		//		String sql = "select count(0) from T_MEASURES where OBJECTIVE = :objectiveValue";
		//
		//		SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(s);
		//
		//		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new  NamedParameterJdbcTemplate(dataSource);
		//		LOGGER.info(displayTable("T_MEASURES"));
		//
		//		//LOGGER.info("contains PK :"+containsPrimaryKey("T_MEASURES", "ID", 1));
		//		//LOGGER.info("contains PK :"+containsPrimaryKey("T_MEASURES", "ID", 10));
		//		//LOGGER.info("contains PK :"+jdbcTemplate.queryForInt("SELECT COUNT(ID) FROM T_MEASURES WHERE ID=7"));
		//		ChocoLogging.flushLogs();
		//		//		LOGGER.info(getLastGeneratedKey().toString());
		//		//		
		//		//		Integer execID = getExecutionID("osp1", "SAT");
		//		//		//insertSolver(s, execID, "");
		//		//		
		//		//		LOGGER.log(Level.INFO, "meas ID = {0}", insertMeasures(s));
		//		//		LOGGER.info(displayTable("T_MEASURES"));
		//		//		LOGGER.log(Level.INFO, "meas ID = {0}", insertMeasures(s));
		//		//		LOGGER.info(displayTable("T_MEASURES"));
		//		//		LOGGER.log(Level.INFO, "meas ID = {0}", insertMeasures(s));
		//		//		LOGGER.info(displayTable("T_MEASURES"));
		//		//getSolverID(execID, s);
		//		//		getExecutionID("jsp1", "MIN", 2);
		//		//		LOGGER.info(displayTable("T_RESTARTS"));
		//		//		LOGGER.info(displayTable("T_STRATEGIES"));
		//		//		LOGGER.info(displayTable("T_MODELS"));
		//		//		LOGGER.info(displayTable("T_SOLVERS"));
		//		//		LOGGER.info(displayTable("T_EXECUTIONS"));
		//		//		LOGGER.info(displayTable("T_LIMITS"));
		//

		//jdbcTemplate.queryForObject("SELECT LABEL FROM T_PROBLEMS WHERE CATEGORY=NULL" , new Object[]{null}, new int[]{VARCHAR}, String.class);
		close();

	}

}
