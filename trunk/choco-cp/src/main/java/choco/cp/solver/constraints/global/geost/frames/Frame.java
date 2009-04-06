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
package choco.cp.solver.constraints.global.geost.frames;


import choco.cp.solver.constraints.global.geost.geometricPrim.Region;

import java.util.Hashtable;
import java.util.Vector;

/**
 * A class that all Frames should extend. It contains info and functionality common to all frames.
 */
public class Frame {
	
	/**
	 * Integer for the object id and the vector is the relative Forbidden Regions of every shifted box of the shapes of the object
	 */
	private Hashtable<Integer, Vector<Region>> RelForbidRegions;
	
	public Frame()
	{
		RelForbidRegions = new Hashtable<Integer,  Vector<Region>>();
	}

	/**
	 * Gets the Relative forbidden regions of this frame. It return a hash table where the key is an Integer object representing the shape id and the value a vector of Region object.
	 */
	public Hashtable<Integer,  Vector<Region>> getRelForbidRegions()
	{
		return RelForbidRegions;
	}
	
	/**
	 * Adds a given shape id and a Vector of regions to the Frame.
	 */
	public void addForbidRegions(int oid, Vector<Region> regions)
	{
		this.RelForbidRegions.put(oid, regions);
	}
	
	/**
	 * Gets the Relative forbidden regions of a certain shape id. It returns Vector of Region object.
	 */
	public Vector<Region> getRelForbidRegions(int oid)
	{
		return this.RelForbidRegions.get(Integer.valueOf(oid));
	}
	
	/**
	 * Returns the size of the frame.
	 */
	public int size()
	{
		return RelForbidRegions.size();
	}
	
	
}
