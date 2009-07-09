package db.beans;

import db.DbConstants;
import db.dao.DbProblem;

public class DbProblemBean implements DbProblem {

	private String label;
	
	private String name;
	
	private String category;
	
	
	public DbProblemBean() {
		this(DbConstants.NULL, DbConstants.NULL, DbConstants.NULL);
	}

	public DbProblemBean(String label, String name, String category) {
		super();
		setLabel(label);
		setName(label);
		setCategory(category);
	}

	@Override
	public final String getCategory() {
		return category;
	}

	@Override
	public final String getLabel() {
		return label;
	}

	@Override
	public final String getName() {
		return name;
	}

	public final void setLabel(String label) {
		this.label = label;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void setCategory(String category) {
		this.category = category;
	}
	
	

}
