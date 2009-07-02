package db;

import static java.lang.System.getProperty;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import choco.kernel.common.logging.ChocoLogging;

public class DatabaseManager {

	public final static Logger LOGGER = ChocoLogging.getParserLogger();

	public final DriverManagerDataSource dataSource = new DriverManagerDataSource();

	protected final JdbcTemplate jdbcTemplate;

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
	
	public int getEntryID(final String dbTable, final String[] fields, final String[] values) {
		try {
			return jdbcTemplate.queryForInt(getSelectPattern(dbTable, "ID", fields), values);
		} catch (EmptyResultDataAccessException e) {
			//no associated entry
			PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(getInsertPattern(dbTable, fields));
					for (int i = 0; i < values.length; i++) {
						ps.setString( i + 1, values[i]);	
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
			return jdbcTemplate.queryForInt(getSelectPattern(dbTable, "ID", fields), values);
		}
	}

	public Number getOperatingSystemID() {
		return getEntryID(
				"T_OS", 
				new String[]{"NAME", "VERSION", "ARCH"}, 
				new String[]{getProperty("os.name"), getProperty("os.version"), getProperty("os.arch")}
		);
	}

	public int getJvmID() {
		return getEntryID(
					"T_JVM", 
					new String[]{"NAME", "VERSION", "VENDOR"}, 
					new String[]{getProperty("java.runtime.name"), getProperty("java.version"), getProperty("java.vendor")}
			);
		}


	public int getRuntimeID() {
		return 0;
//		String host;
//		try {
//			host = InetAddress.getLocalHost().getHostName();
//		} catch (UnknownHostException e) {
//			host = "U";
//		}
//		return getEntryID(
//				"T_RUNTIMES", 
//				new String[]{"HOST", "USER", "MAX_MEMORY"}, 
//				new String[]{getProperty("os.name"), getProperty("user.name"), getProperty("os.arch")}
//		);
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


	public final void test() {
		LOGGER.log(Level.INFO, "display modes: {0}",jdbcTemplate.queryForList("select NAME from T_MODES",String.class));
		LOGGER.log(Level.INFO, "count modes: {0}",jdbcTemplate.queryForInt("select count(0) from T_MODES"));
		LOGGER.log(Level.INFO, "insert MULTI in table: {0}",jdbcTemplate.update("insert into T_MODES (NAME) VALUES ('MULTI')"));
		LOGGER.log(Level.INFO, "display modes: {0}",jdbcTemplate.queryForList("select NAME from T_MODES",String.class));
		LOGGER.log(Level.INFO, "count modes: {0}",jdbcTemplate.queryForInt("select count(0) from T_MODES"));

		getOperatingSystemID();
		getJvmID();
		close();
		
	}

}
