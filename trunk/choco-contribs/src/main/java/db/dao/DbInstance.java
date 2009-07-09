package db.dao;



public interface DbInstance {

	String getName();

	String getProblemLabel();
	
	int getSize1();

	int getSize2();

	DbProblem getProblem();

	DbBounds getBounds();

}

