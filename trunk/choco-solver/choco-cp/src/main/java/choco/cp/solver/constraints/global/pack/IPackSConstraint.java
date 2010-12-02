/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  Â°(..)  |                           *
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
package choco.cp.solver.constraints.global.pack;

import choco.kernel.common.opres.nosum.NoSumList;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * @author Arnaud Malapert</br>
 * @since 5 déc. 2008 <b>version</b> 2.0.1</br>
 * @version 2.0.3</br>
 */
public interface IPackSConstraint {

	//TODO should be super-interface
	int getNbBins();
	
	int getNbItems();
	
	IntDomainVar[] getBins();
	
	IntDomainVar[] getLoads();

	IntDomainVar[] getSizes();

	void fail() throws ContradictionException;

	NoSumList getStatus(int bin);
	
	IStateIntVector getAvailableBins();

	void fireAvailableBins();
	
	/**
	 * Update the minimal load of a given bin.
	 *
	 * @param bin the index of bin
	 * @param load the new load
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	boolean updateInfLoad(final int bin,final int load) throws ContradictionException;


	/**
	 * Update the maximal load of a given bin.
	 *
	 * @param bin the index of bin
	 * @param load the new load
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	boolean updateSupLoad(final int bin,final int load) throws ContradictionException;

	/**
	 * update the number of non empty bins.
	 *
	 */
	boolean updateNbNonEmpty(int min, int max) throws ContradictionException;

	/**
	 * Pack an item into a bin
	 * @return true, if successful
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	boolean pack(final int item,final int bin) throws ContradictionException;


	/**
	 * Remove a possible assignment of an item into a bin.
	 *
	 * @return true, if successful
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	boolean remove(final int item,final int bin) throws ContradictionException;

}
