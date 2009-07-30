package db;

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
	
	private DbConstants() {
		super();
	}
	




}
