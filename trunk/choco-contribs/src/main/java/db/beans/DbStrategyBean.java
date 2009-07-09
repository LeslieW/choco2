package db.beans;

import db.DbConstants;
import db.dao.DbStrategy;

public class DbStrategyBean implements DbStrategy {

	private String branching;
	
	private String varSelector;
	
	private String valSelector;

	
	public DbStrategyBean() {
		this(DbConstants.NULL, DbConstants.NULL, DbConstants.NULL);
	}

	public DbStrategyBean(String branching, String varSelector,
			String valSelector) {
		super();
		this.branching = branching;
		this.varSelector = varSelector;
		this.valSelector = valSelector;
	}

	public final String getBranching() {
		return branching;
	}

	public final void setBranching(String branching) {
		this.branching = branching;
	}

	public final String getVarSelector() {
		return varSelector;
	}

	public final void setVarSelector(String varSelector) {
		this.varSelector = varSelector;
	}

	public final String getValSelector() {
		return valSelector;
	}

	public final void setValSelector(String valSelector) {
		this.valSelector = valSelector;
	}
	
	
	
}
