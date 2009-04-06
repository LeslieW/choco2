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
package samples.pack.search;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.pack.PrimalDualPack;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.kernel.common.util.IntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A specialized search strategy for packing problem.
 * At every backtrack, we state that the item can not be packed in equivalent bins.
 * Two bins are equivalent if they have the same remaining space.
 * @author Arnaud Malapert</br>
 * @since 7 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 */
public class PackDynRemove extends AssignVar {

	public final PrimalDualPack pack;


	public PackDynRemove(VarSelector varSel, ValSelector valHeuri,
			PrimalDualPack pack) {
		super(varSel, valHeuri);
		this.pack = pack;
	}


	public void removeEmptyBins(IntDomainVar bin) throws ContradictionException {
		final IntIterator iter=bin.getDomain().getIterator();
		while(iter.hasNext()) {
			final int b=iter.next();
			if(pack.isEmpty(b)) {
				bin.remVal(b);
			}
		}
	}
	public void fail() throws ContradictionException {
		manager.solver.getPropagationEngine().raiseContradiction(this, ContradictionException.UNKNOWN);
	}

	public void removeEquivalentBins(IntDomainVar bin,int bup) throws ContradictionException {
		final IntIterator iter=bin.getDomain().getIterator();
		final int space = pack.getRemainingSpace(bup);
		while(iter.hasNext()) {
			final int b=iter.next();
			if(pack.getRemainingSpace(b)==space) {bin.remVal(b);}
		}
	}
	/**
	 * @see choco.cp.solver.search.integer.branching.AssignVar#goUpBranch(java.lang.Object, int)
	 */
	@Override
	public void goUpBranch(final Object x, final int i) throws ContradictionException {
		super.goUpBranch(x, i);
		final IntDomainVar bin= (IntDomainVar) x;
		CPSolver.flushLogs();
		//remove other empty bin
		if(pack.svars[i].isInstantiated()) {
			//if we remove an item from a filled bin, fail.
			fail();
		}else if(pack.isEmpty(i)) {
			removeEmptyBins(bin);
		} else {
			removeEquivalentBins(bin, i);
		}
	}


}
