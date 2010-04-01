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
package choco.cp.solver.search.integer.branching.domwdeg;

import choco.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class DomOverWDegBinBranchingNew extends AbstractDomOverWDegBinBranching {

	// L'heuristique pour le valeurs
	protected final ValSelector valSelector;

	public DomOverWDegBinBranchingNew(Solver solver, IntDomainVar[] vars, ValSelector valHeuri, Number seed) {
		super(solver, RatioFactory.createDomWDegRatio(vars, true), seed);
		this.valSelector = valHeuri;
	}


	public void setFirstBranch(final IntBranchingDecision decision) {
		decision.setBranchingValue(valSelector.getBestVal(decision.getBranchingIntVar()));
	}

	@Override
	public void goDownBranch(final IntBranchingDecision decision) throws ContradictionException {
		if (decision.getBranchIndex() == 0) {
			updateVarWeights( decision.getBranchingIntVar(), true);
			decision.setIntVal();
		} else {
			assert decision.getBranchIndex() == 1;
			updateVarWeights( decision.getBranchingIntVar(), false);
			decision.remIntVal();
		}
	}



}
