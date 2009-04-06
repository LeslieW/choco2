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
package choco.cp.solver.constraints.strong;

import choco.cp.solver.constraints.strong.maxrpcrm.MaxRPCrm;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.integer.DoubleHeuristicIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.common.util.IntIterator;

import java.util.Iterator;

public final class DomOverDDegRPC extends DoubleHeuristicIntVarSelector {
	public DomOverDDegRPC(Solver solver) {
		super(solver);
	}

	public double getHeuristic(IntDomainVar v) {
		int ddeg = 0;
        int idx = 0;
        for (final IntIterator it = v.getIndexVector().getIndexIterator(); it
				.hasNext();) {
            idx = it.next();
            SConstraint ct = v.getConstraint(idx);
			if (ct instanceof MaxRPCrm) {
				ddeg += ((MaxRPCrm) ct).getDDeg(v);
			} else if (((AbstractSConstraint) ct).getNbVarNotInst() > 1) {
				ddeg+= ct.getFineDegree(v.getVarIndex(idx));
			}
        }
		return (ddeg == 0) ? Double.MAX_VALUE
				: ((double) v.getDomainSize() / ddeg);

	}
}
