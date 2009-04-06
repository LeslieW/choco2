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
package choco.cp.solver.search.integer.varselector;

import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.integer.DoubleHeuristicIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.common.util.IntIterator;

import java.util.Iterator;

/* 
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 10 oct. 2006
 * Since : Choco 2.0.0
 *
 */
public final class DomOverDynDeg extends DoubleHeuristicIntVarSelector {
	public DomOverDynDeg(Solver solver) {
		super(solver);

	}

	public DomOverDynDeg(Solver solver, IntDomainVar[] vs) {
		super(solver);
		vars = vs;
	}

	public double getHeuristic(IntDomainVar v) {
		int dsize = v.getDomainSize();
		int deg = getDynDeg(v);
		if (deg == 0)
			return Double.MAX_VALUE;
		else
			return (double) dsize / (double) deg;
	}

	public int getDynDeg(IntDomainVar v) {
		int ddeg = 0;
        int idx = 0;
        IntIterator it = v.getIndexVector().getIndexIterator();
		while (it.hasNext()) {
            idx = it.next();
            AbstractSConstraint ct = (AbstractSConstraint) v.getConstraint(idx);
            if (ct.getNbVarNotInst() > 1) {
                ddeg+= ct.getFineDegree(v.getVarIndex(idx));
			}
        }
		return ddeg;
	}

}
