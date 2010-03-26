package choco.cp.solver.search.integer.branching;


import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.integer.ValIterator;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.integer.IntDomainVar;


/**
 * History:
 * 2008-04-23 : Creation : dom / wdeg needs to be a branching not just an heuristic to allow to deal with backtracking events !
 * WARNING ! This implementation suppose that the variables will not change. It copies all variables in an array
 * at the beginning !!
 */
public class DomOverWDegBranching2 extends AbstractDomOverWDegBranching {

	private final ValIterator valIterator;

	// Le constructeur avec :
	// * le solver pour fournir les variables
	// * l'heuristique de valeurs pour instantier une valeur
	public DomOverWDegBranching2(Solver s, ValIterator valHeuri, IntDomainVar[] vars) {
		super(s, vars);
		valIterator = valHeuri;
	}

	public DomOverWDegBranching2(Solver s, ValIterator valHeuri) {
		this(s, valHeuri, buildVars(s));
	}


	@Override
	public boolean finishedBranching(IntBranchingDecision decision) {
		if (valIterator.hasNextVal(decision.getBranchingIntVar(), decision.getBranchingValue())) {
			return false;
		} else {
			updateVarWeights((AbstractVar) decision.getBranchingObject(), false);
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
		updateVarWeights((AbstractVar) decision.getBranchingObject(), true);
		decision.setBranchingValue( valIterator.getFirstVal(decision.getBranchingIntVar()) );
	}

	@Override
	public void setNextBranch(IntBranchingDecision decision) {
		decision.setBranchingValue( 
				valIterator.getNextVal(decision.getBranchingIntVar(), decision.getBranchingValue()) 
		);
	}
}


