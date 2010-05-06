package parser.instances.checker;

import choco.kernel.solver.search.checker.SolutionCheckerException;
import parser.instances.ResolutionStatus;

public class SatSChecker implements IStatusChecker {

	public final boolean isSatifiable;

	protected SatSChecker(boolean isSatifiable) {
		super();
		this.isSatifiable = isSatifiable;
	}

	public void fail(ResolutionStatus status) throws SolutionCheckerException{
		throw new SolutionCheckerException("check-status...["+pretty()+"][status:+"+status+ ']');
	}
	
	@Override
	public void checkStatus(Boolean doMaximize, ResolutionStatus status, Number Objective)
	throws SolutionCheckerException {
		switch (status) {
		case OPTIMUM:
		case SAT: {
			if( ! isSatifiable ) fail(status);
			else break;
		}
		case UNSAT : {
			if( isSatifiable ) fail(status);
			else break;
		}
		default: 
		}
	}

	@Override
	public String pretty() {
		return "check-sat:"+(isSatifiable ? "SAT" : "UNSAT");
	}
	
	@Override
	public String toString() {
		return pretty();
	}


}
