package samples;

import static samples.Examples.GolombRuler.OPTIMAL_RULER;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import samples.Examples.Example;
import samples.Examples.GolombRuler;
import samples.Examples.MinimumEdgeDeletion;
import samples.Examples.PatternExample;
import samples.Examples.Queen;
import choco.kernel.common.logging.ChocoLogging;
import db.DbTables;
import db.EmbeddedDbConnector;
import db.IDbConnector;
import db.RemoteDbConnector;

public class DbExample implements Example {

	public final static int NB_RUNS = 3; //5 seconds

	public final static int TIME_LIMIT = 5000; //5 seconds

	public final static TimeLimitWrapper EX_WRAPPER = new TimeLimitWrapper();

	public IDbConnector dbConnector;

	public void executeEx(String name, Object args) {
		EX_WRAPPER.execute(args);
		dbConnector.getDatabaseManager().insertSolver(EX_WRAPPER._s, name);
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

	protected void executeBenchmark() {
		solveGolombRulers(); //solve instances
		solveQueens();
		solveMED();
	}

	@Override
	public void execute(Object parameters) {
		if (parameters instanceof String) {
			//network database
			dbConnector = new RemoteDbConnector( (String) parameters);
		}else {
			try {
				File output = File.createTempFile("testdb-", ".odb");
				dbConnector = new EmbeddedDbConnector(output);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "dbex...[export:FAIL]", e);
				dbConnector = new EmbeddedDbConnector();
			}
		}
		dbConnector.setUp();
		executeBenchmark();
		dbConnector.getDatabaseManager().printTable(DbTables.T_SOLVERS);
		dbConnector.tearDown();
		ChocoLogging.flushLogs();
	}

	public static void main(String[] args) {
		final DbExample dbex = new DbExample();
		if( args.length == 0) dbex.execute();
		else dbex.execute(args[0]);
	}


	static class TimeLimitWrapper extends PatternExample {

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
