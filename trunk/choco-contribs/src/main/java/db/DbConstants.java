package db;

import choco.cp.solver.search.restart.ParametrizedRestartStrategy;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import db.beans.DbInstanceBean;
import db.beans.DbProblemBean;
import db.beans.DbStrategyBean;
import db.dao.DbInstance;
import db.dao.DbProblem;
import db.dao.DbStrategy;

public final class DbConstants {


	/**
	 * Number of bytes per Mo.
	 */
	public static final int Mo = 1048576;

	
	public static final String NULL = "NULL";
	
	public final static String CALL_IDENTITY = "CALL IDENTITY()";
	
	public final static DbProblem UNKNOWN_PROBLEM = new DbProblemBean(NULL, NULL, NULL);
	
	public final static DbInstance UNKNOWN_INSTANCE = new DbInstanceBean(NULL, UNKNOWN_PROBLEM);
	
	public final static DbStrategy NO_STRATEGY = new DbStrategyBean();
	
	
	public final static ParametrizedRestartStrategy NO_RESTARTS = new ParametrizedRestartStrategy() {
		
		@Override
		public double getGeometricalFactor() {
			return 1;
		}

		@Override
		public String getRestartPolicy() {
			return "NO_RESTARTS";
		}

		@Override
		public int getScaleFactor() {
			return 1;
		}

		@Override
		public void setGeometricalFactor(double geometricalFactor) {
			throw new DatabaseException("can set attribute: constant object");			
		}

		@Override
		public void setScaleFactor(int scaleFactor) {
			throw new DatabaseException("can set attribute: constant object");			
		}

		@Override
		public boolean shouldRestart(AbstractGlobalSearchStrategy search) {
			return false;
		}
		
		
	};


	private DbConstants() {
		super();
	}
	




}
