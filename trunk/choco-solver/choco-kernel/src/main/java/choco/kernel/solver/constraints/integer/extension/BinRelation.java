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
package choco.kernel.solver.constraints.integer.extension;

public interface BinRelation {

	/**
	 * return true if couple (x,y) is feasible according
	 * to the definition of the relation. e.g if the relation is defined
	 * with infeasible tuples, it returns true if (x,y) is one of them.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean checkCouple(int x, int y);

	/**
	 * Test whether the couple (x,y) is consistent
	 *
	 * @param x
	 * @param y
	 * @return true if (x,y) is a consistent couple
	 */
	public boolean isConsistent(int x, int y);

}
