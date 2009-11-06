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
package choco.cp.solver.constraints.global.scheduling;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

/**
 * if b is true then t1 ends before t2, otherwise t2 ends before t1.
 * same as {@link PrecedenceDisjoint} when at least one tasks has a variable duration.
 * b = 1 <=> e1 <= s2
 * b = 0 <=> e2 <= s1
 */ 
public final class PrecedenceVDisjoint extends AbstractPrecedenceConstraint {

	public PrecedenceVDisjoint(IntDomainVar b, 
			IntDomainVar s1, IntDomainVar e1,
			IntDomainVar s2, IntDomainVar e2
	) {
		super(new IntDomainVar[]{b,s1,s2,e1,e2});
	}
	
	public PrecedenceVDisjoint(IntDomainVar b, TaskVar t1, TaskVar t2) {
		this(b, t1.start(), t1.end(), t2.start(), t2.end());
		setTasks(t1, t2);
	}
	
	
	@Override
	public Boolean isP1Entailed() {
		return isEntailed(3,2);
	}

	@Override
	public Boolean isP2Entailed() {
		return isEntailed(4,1);
	}

	@Override
	public void propagateP1() throws ContradictionException {
		propagate(3, 2);

	}

	@Override
	public void propagateP2() throws ContradictionException {
		propagate(4, 1);
	}


	@Override
	public boolean isSatisfied() {
		return vars[BIDX].isInstantiatedTo(1) ? isSatisfied(3, 2) : isSatisfied(4, 1);
	}


	@Override
	public boolean isSatisfied(int[] tuple) {
		return tuple[BIDX] == 1 ? ( tuple[3] <= tuple[2] ) : ( tuple[4] <= tuple[1] );
	}

	@Override
	public String pretty() {
		return pretty( "Precedence Disjoint", pretty(3, 2), pretty(4, 1) );
	}
	
	
	
}
