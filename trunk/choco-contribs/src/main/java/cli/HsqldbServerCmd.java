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
package cli;

import static db.OdbHsqldbBridge.DBNAME;
import static db.OdbHsqldbBridge.exportDatabase;
import static db.OdbHsqldbBridge.getDefaultOdbPattern;
import static db.OdbHsqldbBridge.makeEmbeddedURL;
import static db.OdbHsqldbBridge.makeNetworkURL;
import static db.OdbHsqldbBridge.uncompressDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;

import org.hsqldb.Server;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

/**
 * The command extract a database into a given directory or compress a database into a given file.
 * @author Arnaud Malapert</br> 
 * @since 20 oct. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public class HsqldbServerCmd extends AbstractCmdLine {

	@Option(name="-d",aliases={"--dir"},usage="compress/uncompress to/from the given directory",required=true)
	protected File dbDir;	

	@Option(name="-n",aliases={"--name"},usage="Database name")
	protected String dbName= DBNAME;

	@Option(name="-t",aliases={"--target"},usage="compress the database into the given file.")
	protected File target;

	@Option(name="-p",aliases={"--pattern"},usage="Database pattern (open office database .odb)")
	protected File odb;

	@Option(name="-ns",aliases={"--noserver"},usage="Do not start the hsqldb server (extraction only). you should stop the server manually.")
	protected boolean noServer;

	@Option(name="-v",aliases={"--verbose"},usage="higher verbosity.")
	protected boolean verbose;


	private InputStream odbStream;

	public HsqldbServerCmd() {
		super(false);
	}



	@Override
	public void help() {
		System.err.println("\nThe command follows two modes according to the presence of --target argument:");
		System.err.println("\t-If absent, extract the database from the odb file and start a server.");
		System.err.println("\t-If present, compress the database from the given directory and create a new file with a given pattern.");
		super.help();
	}



	@Override
	protected void checkData() throws CmdLineException {
		if(verbose) LOGGER.setLevel(Level.ALL);
		//check directory and file
		if( target == null ) {
			//extraction to dbdir
			if( ! dbDir.exists() && ! dbDir.mkdirs()) throw new CmdLineException("cant create directory "+dbDir);
			if( ! dbDir.canWrite()) throw new CmdLineException(dbDir+" is not writable.");
		} else {
			if ( ! dbDir.canRead()) throw new CmdLineException(dbDir+" is not readable.");	//compression
			if( target.exists()) throw new CmdLineException(target+" already exists.");
		}

		//check odb pattern
		if( odb == null) odbStream = getDefaultOdbPattern(this);
		else {
			try {
				odbStream = new FileInputStream(odb);
			} catch (FileNotFoundException e) {
				throw new CmdLineException("invalid odb pattern file.", e);
			}
		}
	}

	protected void displayConnectionUrls() {
		if(LOGGER.isLoggable(Level.INFO)) {
			final StringBuilder b  =new StringBuilder();
			b.append("\nconnection to the database using jdbc:");
			b.append("\n\t-Embedded: ").append(makeEmbeddedURL(dbDir, dbName));
			b.append("\n\t-Server:\n\t\t").append(makeNetworkURL("localhost", dbName));
			try {
				Enumeration<NetworkInterface> en =	NetworkInterface.getNetworkInterfaces();
				while(en.hasMoreElements()) {
					final String inet_adress = en.nextElement().getInterfaceAddresses().get(1).getAddress().toString();
					b.append("\n\t\t").append(makeNetworkURL(inet_adress, dbName));
				}
			} catch (SocketException e) {
				LOGGER.log(Level.WARNING, "cant display network connection urls",e);
			}
			LOGGER.info(b.toString());
		}
	}

	protected Server startHsqldbServer() {
		Server server = new Server();
		server.setDatabaseName(0, dbName);
		server.setDatabasePath( 0, dbDir.getAbsolutePath()+"/"+dbName);
		server.setLogWriter(null);
		server.setErrWriter(null);
		server.start();
		return server;
	}

	@Override
	protected void execute() {
		try {
			if( target == null) {
				//extract
				try {
					uncompressDatabase( odbStream, dbDir, dbName);
					if( ! noServer ) {
						startHsqldbServer();
					}else LOGGER.info("hsqldb...[NO_SERVER]");
					displayConnectionUrls();
				} catch (FileNotFoundException e) {
					exitOnException(e);
				}
			} else {
				//compress
				exportDatabase(odbStream, dbDir, dbName, target);
			}
		} catch (IOException e) {
			exitOnException(e);
		}
	}

	public static void main(String[] args) {
		new HsqldbServerCmd().doMain(args);
	}
}