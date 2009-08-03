package choco.cp.solver.search.integer.branching;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractBinIntBranching;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.integer.ValSelector;

public abstract class AbstractAssignOrForbidBranching extends AbstractBinIntBranching {


	// L'heuristique pour le svaleurs
	protected ValSelector valSelector;


	public AbstractAssignOrForbidBranching(ValSelector valSelector) {
		super();
		this.valSelector = valSelector;
	}

	@Override
	public void setFirstBranch(final IntBranchingDecision decision) {
		decision.setBranchingValue( valSelector.getBestVal(decision.getBranchingIntVar()));
	}


	@Override
	public String getDecisionLogMessage(final IntBranchingDecision decision) {
		return getDefaultAssignOrForbidMsg(decision);
	}

	/**
	 * nothing to do
	 */
	@Override
	public void goUpBranch(final IntBranchingDecision decision)	throws ContradictionException {
		//nothing to do
	}

}

