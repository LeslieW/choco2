/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  �(..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.search.integer.varselector.ratioselector;

import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.addFailure;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.getConstraintFailures;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.getVariableWDeg;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.initConstraintExtensions;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.IntRatio;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.propagation.listener.PropagationEngineListener;
import choco.kernel.solver.variables.integer.IntDomainVar;

public final class DomOverWDegSelector extends MinRatioSelector implements PropagationEngineListener {

	
	public DomOverWDegSelector(Solver solver, IntDomainVar[] vars) {
		this(solver, RatioFactory.createDomWDegRatio(vars, false));
	}
	
	public DomOverWDegSelector(Solver solver, IntRatio[] varRatios) {
		super(solver, varRatios);
		initConstraintExtensions(solver);
	}

	@Override
	public final void safeDelete() {
		solver.getPropagationEngine().removePropagationEngineListener(this);
	}
	
	public void contradictionOccured(ContradictionException e) {
		addFailure(e.getDomOverDegContradictionCause());
	}

	@Override
	public String toString() {
		return getVariableWDeg(solver) + "\n" + getConstraintFailures(solver);
	}
	
	
}
