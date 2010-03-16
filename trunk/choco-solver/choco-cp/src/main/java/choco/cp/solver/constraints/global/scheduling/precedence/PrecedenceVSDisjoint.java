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
import choco.kernel.solver.variables.scheduling.TaskVar;
/**
 * The precedence disjoint constraint with setup times and variable duration.
 * b = 1 <=> e1 + k1 <= s2
 * b = 0 <=> e2 + k2 <= s1
 */ 
public final class PrecedenceVSDisjoint extends AbstractPrecedenceConstraint {

	public PrecedenceVSDisjoint(IntDomainVar b, 
			IntDomainVar s1, IntDomainVar e1, int k1,
			IntDomainVar s2, IntDomainVar e2, int k2
	) {
		super(new IntDomainVar[]{b,s1,s2,e1,e2});
		this.k1 = k1;
		this.k2 = k2;
	}

	public PrecedenceVSDisjoint(IntDomainVar b, TaskVar t1, int k1, TaskVar t2, int k2) {
		this(b, t1.start(), t1.end(), k1, t2.start(), t2.end(), k2);
		setTasks(t1, t2);
	}


	@Override
	public Boolean isP1Entailed() {
		return isEntailed(3, k1, 2);
	}

	@Override
	public Boolean isP2Entailed() {
		return isEntailed(4, k2, 1);
	}

	@Override
	public void propagateP1() throws ContradictionException {
		propagate(3, k1, 2);
	}

	@Override
	public void propagateP2() throws ContradictionException {
		propagate(4, k2, 1);
	}

	@Override
	public boolean isSatisfied() {
		return vars[BIDX].isInstantiatedTo(1) ? isSatisfied(3, k1, 2) : isSatisfied(4, k2, 1);
	}

	@Override
	public boolean isSatisfied(int[] tuple) {
		return tuple[BIDX] == 1 ? tuple[3] + k1 <= tuple[2] : tuple[4] + k2 <= tuple[1];
	}

	@Override
	public String pretty() {
		return pretty( "Precedence Disjoint", pretty(3, k1, 2), pretty(4, k2, 1) );
	}

}
