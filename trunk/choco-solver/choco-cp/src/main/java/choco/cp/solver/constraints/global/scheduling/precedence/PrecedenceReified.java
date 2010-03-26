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
package choco.cp.solver.constraints.global.scheduling.precedence;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;


/**
 * <ul>
 *<li> b = 1 <=> x1 + k1 <= x2
 *<li> b = 0 <=> x1 + k1 > x2
 *</ul>
 */
public final class PrecedenceReified extends AbstractPrecedenceSConstraint {

	/**
	 */
	public PrecedenceReified(IntDomainVar x1, int k1, IntDomainVar x2, IntDomainVar b) {
		super( new IntDomainVar[]{b, x1, x2});
		this.k1 = k1;
	}


	@Override
	public void propagateP1() throws ContradictionException {
		propagate(1, k1, 2);
	}

	@Override
	public void propagateP2() throws ContradictionException {
		vars[2].updateSup(vars[1].getSup() + k1 - 1, this, false);
		vars[1].updateInf(vars[2].getInf() - k1 + 1, this, false);
	}

	@Override
	public Boolean isP1Entailed() {
		return isEntailed(1, k1, 2);
	}

	@Override
	public Boolean isP2Entailed() {
		return null;
	}

	@Override
	public void filterOnP1P2TowardsB() throws ContradictionException {
		reuseBool = isP1Entailed();
		if (reuseBool == Boolean.TRUE) {
			vars[BIDX].instantiate(1, this, false);
		} else if(reuseBool == Boolean.FALSE){
			vars[BIDX].instantiate(0, this, false);
		}
	}

	@Override
	public boolean isSatisfied() {
		return vars[0].isInstantiatedTo(1) ? isSatisfied(1, k1, 2) : vars[1].getVal() + k1 > vars[2].getVal();
	}


	@Override
	public boolean isSatisfied(int[] tuple) {
		return tuple[0] == 1 ? tuple[1]  + k1 <= tuple[2] : tuple[1] + k1 > tuple[2];
	}

	@Override
	public String pretty() {
		return pretty("Precedence Reified", pretty(1, k1, 2),  vars[1]+" + "+ k1+ " > " + vars[2]);
	}

}
