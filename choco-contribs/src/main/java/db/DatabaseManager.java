package db;

import static java.lang.System.getProperty;
import static java.sql.Types.INTEGER;
import static java.sql.Types.VARCHAR;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solver;





public class DatabaseManager {

	public final static int DEFAULT_COLUMN_WIDTH = 15;

	public final static Logger LOGGER = ChocoLogging.getParserLogger();

	public static final Object NULL = "NULL";

	/**
	 * Number of bytes per Mo.
	 */
	public static final int Mo = 1048576;

	public final DriverManagerDataSource dataSource = new DriverManagerDataSource();

	protected final JdbcTemplate jdbcTemplate;

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


	public static final String getSelectPattern(String table, String selection, String[] fields) {
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


	/**
	 * 
	 * return the primary key of the entry. the function adds the entry to the table if needed.
	 * @param dbTable
	 * @param selectedField only one field, the primary key, must be selected
	 * @param fields all required fields for insertion must belong to the query
	 * @param values no values is null
	 * @return the primary key of the entry.
	 */
	public final Object retrieveKeyOrInsertEntry(final String dbTable, final String selectedField, 
			final String[] fields,final Object[] values,final int[] argTypes, Class<?> elementType) {
		final String select = getSelectPattern(dbTable, selectedField, fields);
		try {
			return jdbcTemplate.queryForObject(select, values, argTypes, elementType);
		} catch (EmptyResultDataAccessException e) {
			//no associated entry
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
			ChocoLogging.flushLogs();
			return jdbcTemplate.queryForObject(select, values, argTypes, elementType);
		}
	}

	/**
	 * 
	 * return the primary key of the entry. the function adds the entry to the table if needed.
	 * @param dbTable
	 * @param primaryKeyField the field associated with the primary key
	 * @param fields all required fields for insertion must belong to the query
	 * @param values no values is null
	 * @return the primary key of the entry.
	 */
	public final Object retrieveKeyOrInsertEntry2(final String dbTable, final String primaryKeyField, 
			final String[] fields,final Object[] values,final int[] argTypes, Class<?> elementType) {
		final String select = getSelectPattern(dbTable, primaryKeyField, fields);
		try {
			return jdbcTemplate.queryForObject(select, values, argTypes, elementType);
		} catch (EmptyResultDataAccessException e) {
			//no associated entry
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
			ChocoLogging.flushLogs();
			return jdbcTemplate.queryForObject(select, values, argTypes, elementType);
		}
	}

	//	public final Object insertEntryThenRetrieveKey(final String dbTable, final String selectedField, 
	//			final String[] fields,final Object[] values,final int[] argTypes, Class<?> elementType) {
	//		return null;
	//	}

	public final void checkSelectValues(Object[] values) {
		for (int i = 0; i < values.length; i++) {
			if(values[i] == null) {
				LOGGER.info("the {0}th value of a SELECT query is null ! try to repair query by changing to String \"NULL\"");
				values[i] = NULL;
			}
		}
	}

	public final Integer getEntryID(final String dbTable, final String[] fields, final String[] values) {
		checkSelectValues(values);
		return (Integer) retrieveKeyOrInsertEntry(dbTable,"ID", fields, values,createTypeArray(fields.length, VARCHAR), Integer.class);
	}


	public final Object getEntryKey(final String dbTable, final String selectedField, final String[] fields, final int[] values, Class<?> elementType) {
		Integer[] objValues = new Integer[values.length];
		final int[] types = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			types[i] = INTEGER;
			objValues[i] = Integer.valueOf(values[i]);
		}
		return retrieveKeyOrInsertEntry(dbTable,"ID", fields, objValues,types,elementType);
	}

	public final Integer getEntryID(final String dbTable, final String[] fields, final int[] values) {
		return (Integer) getEntryKey(dbTable,"ID", fields, values, Integer.class);
	}


	//*****************************************************************//
	//*******************  T_ENVIRONMENTS  ********************************//
	//***************************************************************//
	/**
	 * return the maximum memory in Mo.
	 */
	public final static Integer getMaxMemory() {
		return Integer.valueOf( (int) (Runtime.getRuntime().maxMemory()/Mo));
	}


	protected final Integer getRuntimeID() {
		String host;	
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			host = "unknwon-host";
		}
		return (Integer) retrieveKeyOrInsertEntry(
				"T_RUNTIMES",
				"ID",
				new String[]{"HOST", "USER", "MAX_MEMORY"}, 
				new Object[]{host, getProperty("user.name"), getMaxMemory()},
				new int[]{VARCHAR,VARCHAR,INTEGER},
				Integer.class
		);
	}




	protected final Integer getOperatingSystemID() {
		return getEntryID(
				"T_OS", 
				new String[]{"NAME", "VERSION", "ARCH"}, 
				new String[]{getProperty("os.name"), getProperty("os.version"), getProperty("os.arch")}
		);
	}

	protected final Integer getJvmID() {
		return getEntryID(
				"T_JVM", 
				new String[]{"NAME", "VERSION", "VENDOR"}, 
				new String[]{getProperty("java.runtime.name"), getProperty("java.version"), getProperty("java.vendor")}
		);
	}

	protected final Integer getEnvironmentID() {
		if(environmentID == null) {
			environmentID =getEntryID(
					"T_ENVIRONMENTS",
					new String[]{ "RUNTIME_ID", "OS_ID", "JVM_ID"}, 
					new int[]{ getRuntimeID(), getOperatingSystemID(), getJvmID()}
			);
		}
		return environmentID;
	}


	//*****************************************************************//
	//*******************  T_INSTANCES  ********************************//
	//***************************************************************//

	protected  final static String[] T_PROBLEMS = {"LABEL","NAME", "CATEGORY"};

	public String getProblemLabel(String label, String name, String category) {
		return (String) retrieveKeyOrInsertEntry(
				"T_PROBLEMS",
				"LABEL",
				T_PROBLEMS, 
				new String[]{label, name, category},
				new int[]{VARCHAR, VARCHAR, VARCHAR},
				String.class
		);
	}

	protected  final static String[] T_INSTANCES = new String[]{"NAME", "PROBLEM_LABEL", "SIZE1","SIZE2"};

	public String getInstanceName(String instanceName, String problemLabel, int size1, int size2) {
		return (String) retrieveKeyOrInsertEntry(
				"T_INSTANCES",
				"NAME",
				T_INSTANCES, 
				new Object[]{ instanceName, problemLabel, Integer.valueOf(size1), Integer.valueOf(size2)},
				new int[]{VARCHAR, VARCHAR, INTEGER, INTEGER},
				String.class
		);
	}

	protected  final static String[] T_BOUNDS = {"INSTANCE_NAME","LOWER_BOUND", "UPPER_BOUND","IS_OPTIMAL"};

	public void insertBounds(final String instanceName,final double lowerBound, final double upperBound, final boolean isOptimal) {
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(getInsertPattern("T_BOUNDS", T_BOUNDS));
				ps.setString(1, instanceName);
				ps.setDouble(2, lowerBound);
				ps.setDouble(3, upperBound);
				ps.setBoolean(4, isOptimal || lowerBound == upperBound);
				return ps;
			}
		};
		try {
			jdbcTemplate.update(psc);
		} catch (DataIntegrityViolationException e) {
			LOGGER.log(Level.WARNING, "duplicate entry: update entry instead of insert it.",e);
		}
	}

	//*****************************************************************//
	//*******************  T_SOLVERS  *********************************//
	//***************************************************************//

	protected  final static String[] T_MODELS = {
		"NB_CONSTRAINTS", "NB_BOOLVARS", "NB_INTVARS",
		"NB_SETVARS", "NB_TASKVARS", "NB_REALVARS"
	};

	public final Integer getModelID(Solver s) {
		return getEntryID(
				"T_MODELS", T_MODELS, 
				new int[]{s.getNbIntConstraints(), s.getNbBooleanVars(), s.getNbIntVars(),
						s.getNbSetVars(), s.getNbTaskVars(), s.getNbRealVars()}
		);
	}


	protected int insertMesure() {
		return 0;
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

		getProblemLabel("OS", "Open-Shop", "Scheduling");
		getProblemLabel("OS", "Open-sHop", "scheduling");
		getProblemLabel("JS", "Job-Shop", "scheduling");
		LOGGER.info(displayTable("T_PROBLEMS"));
		getInstanceName("osp1", "OS", 20, 20);
		getInstanceName("osp1", "OS", 20, 20); //doublons
		getInstanceName("osp2", "OS", 20, 20);
		getInstanceName("jsp1", "JS", 10, 20);
		getInstanceName("jsp1", "JS", 10, 20);//doublons
		getInstanceName("jsp2", "JS", 10, 30); 
		LOGGER.info(displayTable("T_INSTANCES"));
		Solver s = new CPSolver();
		getModelID(s);
		LOGGER.info(jdbcTemplate.queryForList("SELECT IDENTITY() FROM T_MODELS").toString());
		s.createBoundIntVar("v", 0, 5);
		getModelID(s);
		LOGGER.info(jdbcTemplate.queryForList("SELECT @@IDENTITY FROM T_MODELS").toString());
		LOGGER.info(displayTable("T_MODELS"));


		//		insertBounds("osp1", 1000, 1010, false);
		//		insertBounds("jsp1", 1000, 1010, false);
		//		insertBounds("jsp1", 1000, 1010, false);
		//insertBounds("jsp2", 1000, 1000, false);
		//insertBounds("jsp2345", 1000, 1000, false);
		LOGGER.info(displayTable("T_BOUNDS"));
		ChocoLogging.flushLogs();
		//jdbcTemplate.queryForObject("SELECT LABEL FROM T_PROBLEMS WHERE CATEGORY=NULL" , new Object[]{null}, new int[]{VARCHAR}, String.class);
		close();

	}

}
