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

/**
 * A large relation that provides the seekNextSupport function from
 * a given support of given pair var/val and indexes the tuples
 * by integers to store (eventually) the support as StoredInt
 */
public interface IterLargeRelation {

	/**
	 * seek from the next support available from the index of the
	 * old support and the pair variable/value given in argument
	 * @param oldIdxSupport
	 * @param var
	 * @param val
	 * @return
	 */
	public int seekNextTuple(int oldIdxSupport, int var, int val);


	/**
	 * return the tuple corresponding to the given index
	 *
	 * @param support
	 * @return
	 */
	public int[] getTuple(int support);


	/**
	 * returns the number of supports for the pair (var,val)
	 * @param var
	 * @param val
	 * @return
	 */
	public int getNbSupport(int var,int val);
}
