package choco.cp.solver.search.integer.branching;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.integer.IntVarValPair;
import choco.kernel.solver.search.integer.VarValPairSelector;

public class AssignOrForbidIntVarValPair extends
AbstractAssignOrForbidBranching {


	public final VarValPairSelector pairSelector;

	public AssignOrForbidIntVarValPair(VarValPairSelector pairSelector) {
		super(null);
		this.pairSelector = pairSelector;
	}

	@Override
	public void setFirstBranch(final IntBranchingDecision decision) {}

	@Override
	public void goDownBranch(IntBranchingDecision decision)
	throws ContradictionException {
		IntVarValPair pair = (IntVarValPair) decision.getBranchingObject();
		if( decision.getBranchIndex() == 0) {
			pair.var.setVal(pair.val);
		}else {
			assert(decision.getBranchIndex() == 1);
			pair.var.remVal(pair.val);
		}
	}

	@Override
	public Object selectBranchingObject() throws ContradictionException {
		return pairSelector.selectVarValPair();
	}

	@Override
	public String getDecisionLogMessage(IntBranchingDecision decision) {
		final IntVarValPair pair = (IntVarValPair) decision.getBranchingObject();
		return pair.var + 
		(decision.getBranchIndex() == 0 ? LOG_DECISION_MSG_ASSIGN : LOG_DECISION_MSG_REMOVE) + 
		pair.val;
	}



}
