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
 * @author Arnaud Malapert</br> 
 * @since 28 août 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public final class PrecedenceImplied extends AbstractPrecedenceConstraint {


	/**
	 * b = 1 => x1 + k1 <= x2
	 */
	public PrecedenceImplied(IntDomainVar x1, int k1, IntDomainVar x2, IntDomainVar b) {
		super( new IntDomainVar[]{b, x1, x2});
		this.k1 = k1;
	}
	

	@Override
	public void propagateP1() throws ContradictionException {
		propagate(1, k1, 2);
	}

	@Override
	public void propagateP2() throws ContradictionException {}

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
		if(isP1Entailed() == Boolean.FALSE){
			vars[BIDX].instantiate(0, this, false);
		}
	}

	@Override
	public boolean isSatisfied() {
		return vars[BIDX].isInstantiatedTo(1) ? isSatisfied(1, k1, 2) : true;
	}

	
	@Override
	public boolean isSatisfied(int[] tuple) {
		return tuple[BIDX] == 1 ?tuple[1] + k1 <= tuple[2] : true;
	}

	@Override
	public String pretty() {
		return pretty( "Precedence Implied", pretty(1, k1, 2), "TRUE");
	}

}
