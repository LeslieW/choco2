package samples.multicostregular.nsp;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractLargeIntBranchingStrategy;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Dec 9, 2008
 * Time: 1:03:46 PM
 */
public class NSPBranching extends AbstractLargeIntBranchingStrategy {

	NSPVarSelector varselec;
	NSPValSelector valselec;
	IntDomainVar nextVar;

	public NSPBranching(NSPVarSelector varselec, NSPValSelector valselec)
	{
		this.varselec = varselec;
		this.valselec = valselec;
	}



	public void setFirstBranch(final IntBranchingDecision decision) {
		decision.setBranchingValue( valselec.getBestVal( decision.getBranchingIntVar()));
	}


	private IntDomainVar reuseVar;

	public void setNextBranch(final IntBranchingDecision decision) {
		decision.setBranchingValue( valselec.getBestVal( reuseVar));
	}

	public boolean finishedBranching(final IntBranchingDecision decision) {
		if(decision.getBranchingIntVar().getDomainSize() == 0) {
			return true;
		}else {
			reuseVar = varselec.selectVar();
			return reuseVar == null;
		}

	}

	public Object selectBranchingObject() throws ContradictionException {
		return varselec.selectVar();
	}

	public void goDownBranch(final IntBranchingDecision decision) throws ContradictionException {
		if ( decision.getBranchIndex() == 0) {
			decision.setIntVal();
		} else {
			reuseVar.setVal( decision.getBranchingValue());
		}
	}

	public void goUpBranch(final IntBranchingDecision decision) throws ContradictionException {
		if ( decision.getBranchIndex() == 0) {
			decision.remIntVal();
		} else {
			reuseVar.remVal( decision.getBranchingValue());
		}    
	}



	@Override
	public String getDecisionLogMessage(IntBranchingDecision decision) {
		return (decision.getBranchIndex() == 0 ? decision.getBranchingObject() : reuseVar) + LOG_DECISION_MSG_ASSIGN + decision.getBranchingValue();
	}
	
	
}
