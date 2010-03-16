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

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 *
 * Let b be a boolean variables; x0, x1 be two integer variables and k1, k2 two integers.
 * This constraint enforce x0 before x1 if b is true or x1 before x0 if b is false.
 * b0 = 1 <=> x0 + k1 <= x1
 * b0 = 0 <=> x1 + k2 <= x0
 **/
public final class PrecedenceDisjoint extends AbstractPrecedenceConstraint {

	/**
	 * b = 1 <=> x1 + k1 <= x2
	 * b = 0 <=> x2 + k2 <= x1
	 */
	public PrecedenceDisjoint(IntDomainVar x1, int k1, IntDomainVar x2, int k2, IntDomainVar b) {
		super( new IntDomainVar[]{b, x1, x2});
		this.k1 = k1;
		this.k2 = k2;
	}
	
	public PrecedenceDisjoint(TaskVar t1, int k1, TaskVar t2, int k2, IntDomainVar b) {
		this(t1.start(), k1, t2.start(), k2, b);
		setTasks(t1, t2);
	}

	
	@Override
	public final void propagateP1() throws ContradictionException {
		propagate(1, k1, 2);
	}

	@Override
	public final void propagateP2() throws ContradictionException {
		propagate(2, k2, 1);
	}

	@Override
	public final Boolean isP1Entailed() {
		return isEntailed(1, k1, 2);
	}

	@Override
	public final Boolean isP2Entailed() {
		return isEntailed(2, k2, 1);
	}

	@Override
	public boolean isSatisfied() {
		return vars[0].isInstantiatedTo(1) ? isSatisfied(1, k1, 2) : isSatisfied(2, k2, 1);
	}

	@Override
	public boolean isSatisfied(int[] tuple) {
		return tuple[0] == 1 ? tuple[1] + k1 <= tuple[2] : tuple[2] + k2 <= tuple[1];
	}


	@Override
	public String pretty() {
		return pretty( "Precedence Disjoint", pretty(1, k1, 2), pretty(2, k2, 1) );
	}

	@Override
	public String toString() {
		return pretty();
	}

}
