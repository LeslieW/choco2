package samples;

import static samples.Examples.GolombRuler.OPTIMAL_RULER;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

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


	public void solveGolombRulers(DbManager manager) {
		final GolombRuler ruler = new GolombRuler();
		for (int i = 0; i < OPTIMAL_RULER.length - 2; i++) {
			ruler.execute(new Object[]{OPTIMAL_RULER[i][0], OPTIMAL_RULER[i][1], true});
			manager.insertSolver(ruler._s, "Golomb Ruler "+OPTIMAL_RULER[i][0]);
		}
	}

	public void solveQueens(DbManager manager) {
		PatternExample queens = new Queen();
		for (int i = 5; i < 10; i++) {
			queens.execute(i);
			manager.insertSolver(queens._s, "N-Queens-"+i);
		}
	}

	public void solveMED(DbManager manager) {
		PatternExample med = new MinimumEdgeDeletion();
		for (int i = 5; i < 10; i++) {
			med.execute(new Object[]{i,0.5,i});
			manager.insertSolver(med._s, "Minimum Edge Deletion "+i);
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
			DbManager manager = new DbManager(dbDir, dbName);
			LOGGER.info("solving instances ...");
			for (int i = 0; i < 3; i++) {
				solveGolombRulers(manager); //solve instances
				solveQueens(manager);
				solveMED(manager);
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


}
