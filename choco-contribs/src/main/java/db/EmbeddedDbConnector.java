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

import static db.OdbHsqldbBridge.DBNAME;
import static db.OdbHsqldbBridge.exportDatabase;
import static db.OdbHsqldbBridge.getDefaultOdbPattern;
import static db.OdbHsqldbBridge.makeEmbeddedURL;
import static db.OdbHsqldbBridge.uncompressDatabase;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;




public class EmbeddedDbConnector implements IDbConnector {

	private File dbDir;
	
	private DbManager dbManager;
	
	private File odbFile;
	
	


	public EmbeddedDbConnector() {
		super();
	}


	public EmbeddedDbConnector(File odbFile) {
		super();
		this.odbFile = odbFile;
	}


	public final File getDirectory() {
		return dbDir;
	}


	public final File getOdbFile() {
		return odbFile;
	}


	public final void setOdbFile(File odbFile) {
		this.odbFile = odbFile;
	}


	public final DbManager getDatabaseManager() {
		return dbManager;
	}

	public boolean isSetup() {
		return dbManager != null;
	}

	public void setUp() {
		try {
			dbDir = File.createTempFile("hsqldb-","");
			if(  ! dbDir.delete() || ! dbDir.mkdirs()) {
				LOGGER.log(Level.SEVERE,"hsqldb...[init-embedded-database][FAIL]");
				dbDir = null;
			} else {
				uncompressDatabase(getDefaultOdbPattern(this), dbDir, DBNAME);
				dbManager = new DbManager( makeEmbeddedURL(dbDir, DBNAME));
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,"hsqldb...[init-embedded-database][FAIL]", e);
			dbDir = null;
			dbManager = null;
		}
	}


	public void tearDown() {
		if( dbManager != null) {
			dbManager.shutdown();
			if( odbFile != null) {
				try {
					exportDatabase(getDefaultOdbPattern(this), dbDir,DBNAME, odbFile);
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE,"hsqldb...[export-embedded-database][FAIL]", e);
				}
			}
		}else  LOGGER.config("hsqldb...[no-embedded-database]");
		dbDir = null;
		dbManager = null;
	}
}
