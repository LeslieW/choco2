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
package choco.cp.solver.search.integer.branching;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.pack.PrimalDualPack;
import choco.kernel.common.util.IntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;


/**
 * /**
 * A specialized search strategy for packing problem.
 * At every backtrack, we check if the bin is empty after removal, then we can state that the item will not be packed into another empty bin.
 * @author Arnaud Malapert</br> 
 * @since 10 avr. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class PackWeakDynRemovals extends AssignVar {

	public final PrimalDualPack pack;


	public PackWeakDynRemovals(VarSelector varSel, ValSelector valHeuri,
			PrimalDualPack pack) {
		super(varSel, valHeuri);
		this.pack = pack;
	}


	/**
	 * @see choco.cp.solver.search.integer.branching.AssignVar#goUpBranch(java.lang.Object, int)
	 */
	@Override
	public void goUpBranch(final Object x, final int i) throws ContradictionException {
		super.goUpBranch(x, i);
		final IntDomainVar bin= (IntDomainVar) x;
		if(pack.isEmpty(i) ) {
			//there was a single item into the bin, so we cant pack the item into a empty bin again
			final IntIterator iter=bin.getDomain().getIterator();
			while(iter.hasNext()) {
				final int b=iter.next();
				if(pack.isEmpty(b)) {
					bin.remVal(b);
				}
			}
		}

	}


}
