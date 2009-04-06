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
package choco.cp.solver.constraints.global.geost.internalConstraints;

import choco.cp.solver.constraints.global.geost.Constants;

/**
 * A class that represent the Inbox internal constraint. If this constraint is applied to an object, it forces the origin of the object to be inside 
 * its box defined by an offset and a size in each dimension
 */
public class Inbox extends InternalConstraint {
	
	private int[] t;
	private int[] l;
		
	public Inbox(int[] t, int[] l)
	{
		super(Constants.INBOX);
		this.t = t;
		this.l = l;
	}


	public int[] getL() {
		return l;
	}
	
	public int getL(int index) {
		return this.l[index];
	}


	public void setL(int[] l) {
		this.l = l;
	}


	public int[] getT() {
		return t;
	}
	
	public int getT(int index)
	{
		return this.t[index];
	}


	public void setT(int[] t) {
		this.t = t;
	}

}
