package samples;

import static samples.Examples.GolombRuler.OPTIMAL_RULER;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.apache.xmlbeans.impl.xb.xsdschema.PatternDocument.Pattern;

import samples.Examples.Example;
import samples.Examples.GolombRuler;
import samples.Examples.MinimumEdgeDeletion;
import samples.Examples.PatternExample;
import samples.Examples.Queen;
import choco.kernel.common.logging.ChocoLogging;
import db.DbManager;
import db.DbTables;
import db.OdbConnector;

public class DbExample implements Example {

	public final static int NB_RUNS = 3; //5 seconds
	
	public final static int TIME_LIMIT = 5000; //5 seconds

	public final static ExampleWrapper EX_WRAPPER = new ExampleWrapper();
	
	public DbManager manager;
	
	public void executeEx(String name, Object args) {
		EX_WRAPPER.execute(args);
		manager.insertSolver(EX_WRAPPER._s, name);
	}
	
	public void solveGolombRulers() {
		EX_WRAPPER.setSource(new GolombRuler());
		for (int i = 0; i < OPTIMAL_RULER.length-1; i++) {
			executeEx(
					"Golomb-"+OPTIMAL_RULER[i][0], 
					new Object[]{OPTIMAL_RULER[i][0], OPTIMAL_RULER[i][1], true}
			);
		}
	}

	public void solveQueens() {
		EX_WRAPPER.setSource(new Queen());
		for (int i = 2; i < 5; i++) {
			executeEx("N-Queens-"+i, i);
		}
		for (int i = 40; i < 45; i++) {
			executeEx("N-Queens-"+i, i);
		}
	}

	public void solveMED() {
		EX_WRAPPER.setSource( new MinimumEdgeDeletion());
		for (int i = 12; i < 15; i++) {
			executeEx("med-"+i, new Object[]{i,0.5,i});
		}
	}

	@Override
	public void execute() {
		execute(null);
	}

	@Override
	public void execute(Object parameters) {
		LOGGER.info("extract database from internal resource");
		try {
			InputStream odbStream = getClass().getResourceAsStream("/chocodb.odb");
			File dbDir = File.createTempFile("database-", "");
			//File dbDir = new File("/tmp/database");
			dbDir.delete();
			dbDir.mkdir();
			dbDir.deleteOnExit();
			String dbName = "testdb";
			OdbConnector.extractDatabaseHSQLDB( odbStream, dbDir, dbName);
			LOGGER.info("request connection to database.");
			manager = new DbManager(dbDir, dbName);
			LOGGER.info("solving instances ...");
			for (int i = 0; i < 3; i++) {
				solveGolombRulers(); //solve instances
				solveQueens();
				solveMED();
			}
			manager.printTable(DbTables.T_SOLVERS);
			manager.shutdown();
			odbStream = getClass().getResourceAsStream("/chocodb.odb");
			OdbConnector.exportDatabase(odbStream, dbDir, dbName,File.createTempFile(dbName+"-", ".odb")); //export database to odb
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "IO failure", e);
		}
		ChocoLogging.flushLogs();
	}

	public static void main(String[] args) {
		new DbExample().execute();
	}


	static class ExampleWrapper extends PatternExample {

		public PatternExample source;


		public final PatternExample getSource() {
			return source;
		}

		public final void setSource(PatternExample source) {
			this.source = source;
		}

		
		@Override
		public void buildModel() {
			source.buildModel();
			_m = source._m;

		}

		@Override
		public void buildSolver() {
			source.buildSolver();
			_s = source._s;
			_s.setTimeLimit(TIME_LIMIT);

		}

		@Override
		public void prettyOut() {
			source.prettyOut();

		}

		@Override
		public void setUp(Object parameters) {
			source.setUp(parameters);
		}

		@Override
		public void solve() {
			source.solve();

		}




	}
}