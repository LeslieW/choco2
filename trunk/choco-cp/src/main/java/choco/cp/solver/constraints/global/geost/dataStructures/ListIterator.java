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
package choco.cp.solver.constraints.global.geost.dataStructures;

final class ListIterator
{
	LinkedList owner;
	ListItem pos;

	ListIterator(LinkedList owner, ListItem pos){
		this.owner = owner;
		this.pos = pos;
	}

	/*
	 * check whether object owns the iterator
	 */
	public boolean belongsTo(Object owner)
	{
		return this.owner == owner;
	}

	/*
	 * move to head position
	 */
	public void head()
	{
		pos = owner.head;
	}

	/*
	 * move to next position
	 */
	public void next()
	{
		pos = pos.next;
	}

	/*
	 * move to previous position
	 */
	public void previous()
	{
		pos = pos.previous;
	}
}