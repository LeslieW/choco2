

package cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import parser.instances.AbstractInstanceModel;
import parser.instances.BasicSettings;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import cli.explorer.FileExplorer;
import cli.explorer.FileProcedure;
import db.DbManager;
import db.EmbeddedDbConnector;
import db.IDbConnector;
import db.RemoteDbConnector;

/**
 * The class define a command pattern designed for benchmarking.
 * It parses input/output arguments as well as global settings (timelimit, seed, verbosity).
 * A connection with an embedded/remote database could also be established.
 *  
 * @author Arnaud Malapert</br> 
 * @since 11 nov. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public abstract class AbstractBenchmarkCmd extends AbstractCmdLine implements FileProcedure {

	@Option(name="-f",aliases={"--file","-file"},usage="Instance File or directory with optional wildcard pattern arguments.",required=true)
	protected File inputFile;

	@Option(name="-u",aliases={"--url"},usage="connect to remote database at URL.")
	protected String databaseURL;

	@Option(name="-e",aliases={"--export"},usage="activate embedded database and export it to odb file")
	protected File databaseFile;

	@Option(name="-p",aliases={"--properties"},usage="user property file")
	protected File propertyFile;

	@Option(name="-o",aliases={"--output"},usage="specify output directory (logs, solutions, ...)")
	protected File outputDirectory;

	@Option(name="-s",aliases={"--seed", "-seed"},usage="global seed")
	protected long seed;

	@Option(name="-tl",aliases={"--timeLimit", "-time"},usage="time limit in seconds")
	protected Integer timeLimit;

	@Option(name="-v",aliases={"--verbosity"},usage="set the verbosity level")
	public final void setGlobalVerbosity(Verbosity verbosity) {
		ChocoLogging.setVerbosity(verbosity);
	}

	private final String defaultPropertiesResource;

	protected BasicSettings settings;
	
	protected Random seeder;

	protected Properties properties;

	private IDbConnector dbConnector;

	protected AbstractInstanceModel instance;

	public AbstractBenchmarkCmd(String defaultPropertiesResource) {
		super(true);
		this.defaultPropertiesResource = defaultPropertiesResource;
	}

	private void makeDbConnector() throws CmdLineException {
		if( databaseFile == null) {
			if( databaseURL == null) dbConnector = DISCONNECTED;
			else dbConnector = new RemoteDbConnector(databaseURL);
		} else { 
			if( databaseURL == null) dbConnector = new EmbeddedDbConnector(databaseFile);
			else throw new CmdLineException("choose between embedded (-f) and remote (-u) database.");
		}
	}

	private String initializeProperties() {
		String r ="null";
		properties =new Properties();
		if( propertyFile == null) {
			if( defaultPropertiesResource != null) {
				try {
					properties.load(new InputStreamReader(getClass().getResourceAsStream(defaultPropertiesResource)));
					r = defaultPropertiesResource;
				} catch (IOException e) {
					LOGGER.log(Level.WARNING,"cmd...[read_default_properties][FAIL]",e);
				}
			}
		}else {
			try {
				properties.load(new FileReader(propertyFile));
				r = propertyFile.getName();
			} catch (FileNotFoundException e) {
				LOGGER.log(Level.WARNING,"cmd...[read_properties][FAIL]",e);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING,"cmd...[read_properties][FAIL]",e);
			}
		}
		if(properties.isEmpty()) LOGGER.warning("cmd...[empty_properties]");
		return r;
	}

	@Override
	protected void checkData() throws CmdLineException {
		if( !inputFile.exists() || !inputFile.canRead() ) {
			throw new CmdLineException(inputFile + "is not a readble file.");
		}
		if( outputDirectory != null && ! outputDirectory.isDirectory()) {
			throw new CmdLineException(outputDirectory+" is not a directory");
		}
		makeDbConnector();
		String desc = initializeProperties();
		if( settings != null) {
			if(properties != null) settings.configure(properties);
			if( timeLimit != null) settings.setTimeLimit(timeLimit);
		}
		LOGGER.log(Level.CONFIG, "cmd...[seed:{3}][properties:{0}][db:{2}][output:{1}]", new Object[]{desc, outputDirectory, dbConnector, seed});
	}


	protected abstract AbstractInstanceModel createInstance();

	protected void configureInstance() {
		instance.setOutputDirectory(outputDirectory);
		instance.setDatabaseManager(dbConnector.getDatabaseManager());
		instance.setSeed(seed);
	}

	@Override
	protected void execute() {
		//configure
		dbConnector.setUp();
		instance = createInstance();
		configureInstance();
		FileExplorer.explore(this, inputFile, arguments); //run benchmark
		dbConnector.tearDown();
	}


	public final static IDbConnector DISCONNECTED =  new IDbConnector() {

		@Override
		public void tearDown() {}

		@Override
		public void setUp() {}

		@Override
		public DbManager getDatabaseManager() {return null;}

		@Override
		public String toString() {
			return "null";
		}


	};

}
