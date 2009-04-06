/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.global.geost.geometricPrim;

import static java.lang.System.arraycopy;

/**
 * This class represent a k dimensional Point.
 */
public class Point {
	
	//this class serves as Point object. The dimension of the point is d Dimensional.
	
	private int coords[];
	private int dim;
	
	/**
	 * Creates a point of dimension dim at the origin of the coordinate base.
	 * @param dim The dimension of the point object to create
	 */
	public Point(int dim)
	{
		this.dim = dim;
		coords =  new int[this.dim];
		for (int i = 0; i < this.dim; i++)
			this.coords[i] = 0;
	}
	
	/**
	 * Creates a point object from an array of integers. The index of the array is actually the dimension number 
	 * and the value this index holds is the coordinate value of the point at that specified dimension. The length of the array 
	 * represents the total dimension of the point.
	 * @param coordinates An array of integers of length the dimension of the point.
	 */
	public Point(int coordinates[])
	{
		//creates a point from an array of integers.
		coords =  new int[coordinates.length];
		this.setCoords(coordinates);
	}
	
	/**
	 * Creates a point identical to the point given as parameter.
	 * @param p A point object
	 */
	public Point(Point p)
	{
		//creates a point from another point.
		coords =  new int[this.dim];
		for(int i = 0; i < p.getCoords().length; i++)
			this.coords[i] = p.getCoord(i);
	}
	
	public int[] getCoords()
	{
		return this.coords;
	}
	
	public void setCoords(int coordinates[])
	{
        arraycopy(coordinates, 0, this.coords, 0, coordinates.length);
	}
	
	public int getCoord(int index)
	{
		return this.coords[index];
	}
	
	public void setCoord(int index, int value)
	{
		this.coords[index] = value;
	}
	
	/**
	 * Tests whether this point is lexicographically greater than or equal to the other point (passed by parameter). The lexicographic ordering starts at dimension d (passed by parameter).
	 */
	public boolean lexGreaterThanOrEqual(Point other, int d) {
		int jPrime = 0;
		
		for(int j = 0; j < this.dim; j++)
		{
			jPrime = (j + d) % this.dim;
			if(this.getCoord(jPrime) != other.getCoord(jPrime))
			{
                return this.getCoord(jPrime) >= other.getCoord(jPrime);
			}
		}
		return true;
	}
	
	/**
	 * Tests whether this point is lexicographically strictly greater than to the other point (passed by parameter). The lexicographic ordering starts at dimension d (passed by parameter).
	 */
	public boolean lexGreaterThan(Point other, int d) {
		int jPrime = 0;
		
		for(int j = 0; j < this.dim; j++)
		{
			jPrime = (j + d) % this.dim;
			if(this.getCoord(jPrime) != other.getCoord(jPrime))
			{
                return this.getCoord(jPrime) >= other.getCoord(jPrime);
			}
		}
		return false; //since they are equal
	}
	
	/**
	 * Tests whether this point is lexicographically smaller than or equal to the other point (passed by parameter). The lexicographic ordering starts at dimension d (passed by parameter).
	 */
	public boolean lexLessThanOrEqual(Point other, int d) {
		int jPrime = 0;
		
		for(int j = 0; j < this.dim; j++)
		{
			jPrime = (j + d) % this.dim;
			if(this.getCoord(jPrime) != other.getCoord(jPrime))
			{
                return this.getCoord(jPrime) <= other.getCoord(jPrime);
			}
		}
		return true;
	}
	
	/**
	 * Tests whether this point is lexicographically strictly smaller than the other point (passed by parameter). The lexicographic ordering starts at dimension d (passed by parameter).
	 */
	public boolean lexLessThan(Point other, int d) {
		int jPrime = 0;
		
		for(int j = 0; j < this.dim; j++)
		{
			jPrime = (j + d) % this.dim;
			if(this.getCoord(jPrime) != other.getCoord(jPrime))
			{
                return this.getCoord(jPrime) <= other.getCoord(jPrime);
			}
		}
		return false; //since they are equal
	}
	
	
}
