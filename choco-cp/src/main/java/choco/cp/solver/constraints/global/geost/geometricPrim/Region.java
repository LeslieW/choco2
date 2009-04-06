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

/**
 * This class represents a k dimensional Region (where k is specified as a global constant in the global.Constants class).
 * Also each region should attached to an object therefore the Object id  should be specified in the constructor
 */
public class Region {
	
	private int oid;   //object id
	private int min[]; //boundary minimum for each dimension
	private int max[]; //boundary maximum for each dimension
	private int dim;
	
	/**
	 * @param objectId The object id that this region attached to.
	 * @param minimumBoundary an array of the minimum boundary of this region in every dimension
	 * @param maximumBoundary an array of the maximum boundary of this region in every dimension
	 */
	public Region(int dim, int objectId, int minimumBoundary[], int maximumBoundary[])
	{
		this.dim = dim;
		this.oid = objectId;
		this.min = new int[this.dim];
		this.max = new int[this.dim];
		this.min = minimumBoundary;
		this.max =  maximumBoundary;
	}
	
	/**
	 * Constructs an empty region for this object id.
	 * @param objectId The object id that this region belong to.
	 */
	public Region(int dim, int objectId)
	{
		this.dim = dim;
		this.oid = objectId;
		this.min = new int[this.dim];
		this.max = new int[this.dim];
	}
	
	public void setObjectId(int objectId)
	{
		this.oid = objectId;
	}
	
	public int getObjectId()
	{
		return this.oid;
	}
	
	public void setMinimumBoundary(int index, int value)
	{
		this.min[index] = value;
	}
	
	public void setMinimumBoundary(int minimumBoundary[])
	{
		this.min = minimumBoundary.clone();
	}
	
	public int getMinimumBoundary(int index)
	{
		return this.min[index];
	}
	
	public void setMaximumBoundary(int index, int value)
	{
		this.max[index] = value;
	}
	
	public void setMaximumBoundary(int maximumBoundary[])
	{
		this.max = maximumBoundary.clone();
	}
	
	public int getMaximumBoundary(int index)
	{
		return this.max[index];
	}
	

}
