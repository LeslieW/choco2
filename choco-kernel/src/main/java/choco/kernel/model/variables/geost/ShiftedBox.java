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
package choco.kernel.model.variables.geost;

import java.util.Arrays;

/**
 * This is the class that represents a Shifted Box. Each shifted box belongs to a shape (therefore the shape id variable) and has two lists
 * that specify its offset (basically origin, lower left corner) and its size in every dimension.
 */
public class ShiftedBox {
	
	private int sid; //shape Id
	private int[] t; //the offset
	private int[] l; //the size
	
	public ShiftedBox(int shapeId, int[] offset, int[] size)
	{
		this.sid = shapeId;
		this.t = offset;
		this.l = size;
	}
	
	public ShiftedBox(){}
	
	public void setOffset(int index, int value)
	{
		this.t[index] = value;
	}
	
	public void setOffset(int[] off)
	{
		this.t = off;
	}
	
	public int getOffset(int index)
	{
		return this.t[index];
	}
	
	public void setSize(int index, int value)
	{
		this.l[index] = value;
	}
	
	public void setSize(int[] s)
	{
		this.l = s;
	}
	
	public int getSize(int index)
	{
		return this.l[index];
	}
	
	public int getShapeId()
	{
		return this.sid;
	}
	
	public void setShapeId(int id)
	{
		this.sid = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ShiftedBox other = (ShiftedBox) obj;
		if (!Arrays.equals(l, other.l))
			return false;
		if (sid != other.sid)
			return false;
		if (!Arrays.equals(t, other.t))
			return false;
		return true;
	}

	

}
