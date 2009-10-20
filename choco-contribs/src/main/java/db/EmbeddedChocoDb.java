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
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import static db.OdbHsqldbBridge.*;
import choco.kernel.common.logging.ChocoLogging;

public class EmbeddedChocoDb {

	private File dbDir;

	private DbManager dbManager;

	public final static Logger LOGGER= ChocoLogging.getParserLogger();

	
	
	public final File getDirectory() {
		return dbDir;
	}


	public final DbManager getManager() {
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
		}
	}

	public final void tearDown() {
		tearDown(null);
	}
	
	public void tearDown(File output) {
		if( isSetup()) {
			dbManager.shutdown();
			if( output != null) {
				try {
					exportDatabase(getDefaultOdbPattern(this), dbDir,DBNAME, output);
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE,"hsqldb...[export-embedded-database][FAIL]", e);
				}
			}
		}else  LOGGER.config("hsqldb...[no-embedded-database]");
		dbDir = null;
		dbManager = null;
	}
}
