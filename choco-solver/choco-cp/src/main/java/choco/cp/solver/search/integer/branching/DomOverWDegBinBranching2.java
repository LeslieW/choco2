package choco.cp.solver.search.integer.branching;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class DomOverWDegBinBranching2 extends AbstractDomOverWDegBranching {

	// L'heuristique pour le valeurs
	protected final ValSelector valSelector;

	public DomOverWDegBinBranching2(Solver solver,
			ValSelector valHeuri) {
		this(solver, valHeuri, buildVars(solver));
	}

	public DomOverWDegBinBranching2(Solver solver, ValSelector valHeuri,
			IntDomainVar[] vars) {
		super(solver, vars);
		this.valSelector = valHeuri;
	}


	public void setFirstBranch(final IntBranchingDecision decision) {
		assert decision.getBranchIndex() == 0;
		decision.setBranchingValue(valSelector.getBestVal(decision.getBranchingIntVar()));
	}

	public final void getNextBranch(final IntBranchingDecision decision) {
		assert decision.getBranchIndex() == 0;
		
	}

	public final boolean finishedBranching(final IntBranchingDecision decision) {
		return decision.getBranchIndex() > 0;
	}



	@Override
	public void goDownBranch(final IntBranchingDecision decision) throws ContradictionException {
		if (decision.getBranchIndex() == 0) {
			updateVarWeights( (Var) decision.getBranchingObject(), true);
			decision.setIntVal();
		} else {
			updateVarWeights( (Var) decision.getBranchingObject(), false);
			decision.remIntVal();
		}
	}

	@Override
	public String getDecisionLogMessage(IntBranchingDecision decision) {
		return getDefaultAssignOrForbidMsg(decision);
	}

	/**
	 * nothing to do
	 */
	@Override
	public void goUpBranch(IntBranchingDecision decision)
			throws ContradictionException {
		//nothing to do	
	}

	/**
	 * nothing to do
	 */
	@Override
	public void setNextBranch(IntBranchingDecision decision) {
		//nothing to do
	}

	

}
