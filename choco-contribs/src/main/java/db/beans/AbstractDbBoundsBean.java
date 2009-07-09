package db.beans;

import db.dao.DbBounds;

public abstract class AbstractDbBoundsBean implements DbBounds {

	private double lowerBound;
	
	private double upperBound;
	
	private boolean optimal;
	
	public AbstractDbBoundsBean() {
		this(Double.MIN_VALUE, Double.MAX_VALUE);
	}

	public AbstractDbBoundsBean(double lowerBound, double upperBound) {
		this(lowerBound, upperBound, lowerBound == upperBound);
	}

	public AbstractDbBoundsBean(double lowerBound, double upperBound, boolean optimal) {
		super();
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.optimal = optimal;
	}

	

	public final double getLowerBound() {
		return lowerBound;
	}

	public final void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public final double getUpperBound() {
		return upperBound;
	}

	public final void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

	public final boolean isOptimal() {
		return optimal || (lowerBound == upperBound);
	}

	public final void setOptimal(boolean optimal) {
		this.optimal = optimal;
	}
	
	
}
