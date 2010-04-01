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
import choco.kernel.solver.search.integer.ValIterator;
import choco.kernel.solver.variables.integer.IntDomainVar;


/**
 * History:
 * 2008-04-23 : Creation : dom / wdeg needs to be a branching not just an heuristic to allow to deal with backtracking events !
 * WARNING ! This implementation suppose that the variables will not change. It copies all variables in an array
 * at the beginning !!
 */
public class DomOverWDegBranchingNew extends AbstractDomOverWDegBranching {

	private final ValIterator valIterator;

	// Le constructeur avec :
	// * le solver pour fournir les variables
	// * l'heuristique de valeurs pour instantier une valeur
	public DomOverWDegBranchingNew(Solver s, IntDomainVar[] vars, ValIterator valHeuri, Number seed) {
		super(s, RatioFactory.createDomWDegRatio(vars, true), seed);
		valIterator = valHeuri;
	}

	@Override
	public boolean finishedBranching(IntBranchingDecision decision) {
		final IntDomainVar var = decision.getBranchingIntVar();
		if (valIterator.hasNextVal(var, decision.getBranchingValue())) {
			return false;
		} else {
			updateVarWeights(var, false);
			return true;
		}
	}

	@Override
	public String getDecisionLogMessage(IntBranchingDecision decision) {
		return getDefaultAssignMsg(decision);
	}

	@Override
	public void goDownBranch(IntBranchingDecision decision)
	throws ContradictionException {
		decision.setIntVal();

	}

	@Override
	public void goUpBranch(IntBranchingDecision decision)
	throws ContradictionException {
		//The weights are updated for the current branching object in setFirstBranch and finishedBranching.
		//We cant use a selector yet because the condition in finishedBranching is never activated and the weights become inconsistent.

	}

	@Override
	public void setFirstBranch(IntBranchingDecision decision) {
		final IntDomainVar var = decision.getBranchingIntVar();
		updateVarWeights(var, true);
		decision.setBranchingValue( valIterator.getFirstVal(var));
	}

	@Override
	public void setNextBranch(IntBranchingDecision decision) {
		decision.setBranchingValue( 
				valIterator.getNextVal(decision.getBranchingIntVar(), decision.getBranchingValue()) 
		);
	}
}


