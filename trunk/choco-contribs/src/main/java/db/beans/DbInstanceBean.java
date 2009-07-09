package db.beans;

import db.DbConstants;
import db.dao.DbBounds;
import db.dao.DbInstance;
import db.dao.DbProblem;

public class DbInstanceBean implements DbInstance {
	
	private String name;
	
	private DbProblem problem;
	
	private int size1;
	
	private int size2;
	
	private DbBounds bounds;
	

	public DbInstanceBean() {
		this(DbConstants.NULL, DbConstants.UNKNOWN_PROBLEM);
	}



	public DbInstanceBean(String name, DbProblem problem) {
		super();
		this.name = name;
		this.problem = problem;
	}
	
	

	public DbInstanceBean(String name, DbProblem problem, int size1) {
		super();
		this.name = name;
		this.problem = problem;
		this.size1 = size1;
	}



	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}


	public final int getSize1() {
		return size1;
	}

	public final void setSize1(int size1) {
		this.size1 = size1;
	}

	public final int getSize2() {
		return size2;
	}

	public final void setSize2(int size2) {
		this.size2 = size2;
	}

	public final DbProblem getProblem() {
		return problem;
	}

	public final String getProblemLabel() {
		return getProblem().getLabel();
	}
		
	public final void setProblem(DbProblem problem) {
		this.problem = problem;
	}

	public final DbBounds getBounds() {
		return bounds;
	}

	public final void setBounds(double lowerBound, double upperBound, boolean optimal) {
		this.bounds = new DbBoundsBean(lowerBound, upperBound, optimal);
	}
	
	class DbBoundsBean extends AbstractDbBoundsBean {

		public DbBoundsBean(double lowerBound, double upperBound,
				boolean optimal) {
			super(lowerBound, upperBound, optimal);
			
		}

		@Override
		public String getInstanceName() {
			return getName();
		}
		
		
	}
	

}
