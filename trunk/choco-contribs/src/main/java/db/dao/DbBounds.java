package db.dao;

public interface DbBounds {
	
	String getInstanceName();
	
	double getLowerBound();
	
	double getUpperBound();
	
	boolean isOptimal();
}
