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
package choco.cp.solver.search.integer.valselector;

import choco.cp.solver.constraints.global.pack.PrimalDualPack;
import choco.kernel.common.util.IntIterator;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * @author Arnaud Malapert</br>
 * @since 7 déc. 2008 version 2.0.1</br>
 * @version 2.0.1</br>
 */
public class BestFit implements ValSelector {

	public final PrimalDualPack pack;

	public BestFit(PrimalDualPack cstr) {
		super();
		this.pack = cstr;
	}

	@Override
	public int getBestVal(IntDomainVar x) {
		final IntIterator iter=x.getDomain().getIterator();
		int bin= iter.next();
		int max=pack.getRemainingSpace(bin);
		while(iter.hasNext()) {
			final int  b =iter.next();
			final int space=pack.getRemainingSpace(b);
			if(space<max) {
				max =space;
				bin = b;
			}
		}
		return bin;
	}



}
