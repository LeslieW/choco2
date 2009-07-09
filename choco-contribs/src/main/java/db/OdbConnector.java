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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.filechooser.FileNameExtensionFilter;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;

public final class OdbConnector {

	public final static int BUFFER=4048;

	public final static String DIRECTORY_DB_OO = "database/";

	public final static Logger LOGGER = ChocoLogging.getParserLogger();

	private OdbConnector() {
		super();
	}

	public InputStream getDefaultDatabaseStream(Object o) {
		return o.getClass().getResourceAsStream("/chocodb.odb");
	}

	public static void copy(InputStream inStream, OutputStream outStream, byte[] buffer) throws IOException {
		int nrBytesRead = 0;
		while ((nrBytesRead = inStream.read(buffer)) > 0) {
			outStream.write(buffer, 0, nrBytesRead);
		}
	}

	private static void validate(File outputDir) {
		if(outputDir.exists()) {
			if(outputDir.isDirectory() && outputDir.canRead() && outputDir.canWrite()) {
				LOGGER.log(Level.INFO, "check directory {0} [OK]",outputDir);
			}else {
				LOGGER.log(Level.INFO, "check file {0} [FAIL]", outputDir);
				ChocoLogging.flushLogs();
				throw new DatabaseException("invalid file "+outputDir);
			}
		}else if( outputDir.mkdir()) {
			LOGGER.log(Level.INFO, "create directory {0} [OK]", outputDir);
		}else {
			LOGGER.log(Level.SEVERE, "create or read directory {0} [FAIL]", outputDir);
			ChocoLogging.flushLogs();
			throw new DatabaseException("cant create or read directory "+outputDir);
		}
	}

	public static void extractDatabaseHSQLDB(InputStream odbStream, File databaseDir, String databaseName) {
		validate(databaseDir);
		try {
			final ZipInputStream inStream = new ZipInputStream(odbStream);
			ZipEntry entry;
			final byte[] buffer = new byte[BUFFER];
			while( (entry = inStream.getNextEntry()) != null) {
				if(entry.getName().startsWith(DIRECTORY_DB_OO) ) {
					final FileOutputStream outStream = new FileOutputStream(new File(databaseDir, databaseName+ "." + entry.getName().substring(DIRECTORY_DB_OO.length())));
					copy(inStream, outStream, buffer);
					outStream.close();	
					LOGGER.log(Level.FINE, "uncompress {0} [OK]",entry);
				}
			}
			inStream.close();
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, "uncompress database into {0} [FAIL]", databaseDir);
			ex.printStackTrace();
		}
		LOGGER.log(Level.INFO, "uncompress odb database into {0} [OK]", databaseDir);
	}

	static class DatabaseFilter implements FilenameFilter{

		private final String databasePrefix;


		private DatabaseFilter(String databaseName) {
			super();
			this.databasePrefix = databaseName + ".";
		}


		@Override
		public boolean accept(File dir, String name) {
			if(name.startsWith(databasePrefix)) {
				if(name.endsWith(".log") || name.endsWith(".lck")) return false;
				else return true;
			}
			return false;
		}		 

	}

	private static void copyDatabase(ZipOutputStream odbStream, File databaseDir, String databaseName, byte[] buffer) throws IOException  {
		final File[] dbFiles = databaseDir.listFiles(new DatabaseFilter(databaseName));
		for (File dbf : dbFiles) {
			final ZipEntry entry = new ZipEntry(DIRECTORY_DB_OO+dbf.getName().substring(databaseName.length()+1));
			odbStream.putNextEntry(entry);
			final FileInputStream inStream = new FileInputStream(dbf);
			copy(inStream, odbStream, buffer);
			inStream.close();
			LOGGER.log(Level.FINE, "compress {0} [OK]", entry);
		}

	}


	private static void copyOdbFiles(ZipInputStream inStream, ZipOutputStream outStream, byte[] buffer) throws IOException {
		ZipEntry entry;
		while( (entry = inStream.getNextEntry()) != null) {
			if( ! entry.getName().startsWith(DIRECTORY_DB_OO) ) {
				outStream.putNextEntry(entry);
				copy(inStream, outStream, buffer);
				LOGGER.log(Level.FINE, "compress {0} [OK]",entry);
			}
		}
	}

	public static void exportDatabase(InputStream odbStream, File databaseDir, String databaseName, File odbOutput)  {
		try {
			LOGGER.info("exporting database ...");
			final ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(odbOutput));
			LOGGER.log(Level.INFO, "creating file {0} [OK]", odbOutput);
			final byte[] buffer = new byte[BUFFER];
			try {
				//copy Db
				copyDatabase(outStream, databaseDir, databaseName, buffer);
				LOGGER.log(Level.INFO, "compress database {0} [OK]", databaseDir);

				//insert odb files
				final ZipInputStream inStream = new ZipInputStream(odbStream);
				LOGGER.info("read odb stream [OK]");
				copyOdbFiles(inStream, outStream, buffer);
				LOGGER.info("compress odb files [OK]");
				inStream.close();
				outStream.close();
				LOGGER.log(Level.INFO, "export database in {0} [OK]", odbOutput);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public static void main(String[] args) throws SQLException {
		ChocoLogging.setVerbosity(Verbosity.VERBOSE);
		Object o = new Object();

		//extraction
		InputStream odbStream = o.getClass().getResourceAsStream("/chocodb.odb");
		File dbDir = new File("/tmp/database");
		String dbName = "testdb";
		extractDatabaseHSQLDB( odbStream, dbDir, dbName);
		//connection
		DbManager manager = new DbManager(dbDir, dbName);
		manager.test();
		//export
//		odbStream = o.getClass().getResourceAsStream("/chocodb.odb");
//		exportDatabase(odbStream, dbDir, dbName, new File("/tmp/test.odb"));
//		ChocoLogging.flushLogs();
	}
}
