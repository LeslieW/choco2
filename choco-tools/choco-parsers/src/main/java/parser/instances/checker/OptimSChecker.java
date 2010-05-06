package parser.instances.checker;

import choco.kernel.solver.search.checker.SolutionCheckerException;
import parser.instances.ResolutionStatus;

public final class OptimSChecker implements IOptimChecker {
	
	protected final int minObjValue;

	protected final int maxObjValue;

	public OptimSChecker(int optObjValue) {
		this(optObjValue, optObjValue);
	}
	
	public OptimSChecker(int minObjValue, int maxObjValue) {
		super();
		this.minObjValue = minObjValue;
		this.maxObjValue = maxObjValue;
	}

	public int getMinObjValue() {
		return minObjValue;
	}

	public int getMaxObjValue() {
		return maxObjValue;
	}

	public boolean checkLowerBound(boolean doMaximize, int lbVal) {
		return doMaximize ? lbVal >= minObjValue : lbVal <= maxObjValue;
	}

	public boolean checkOptimum(int optVal) {
		return optVal >= minObjValue && optVal <= maxObjValue;
	}

	public boolean checkUpperBound(boolean doMaximize, int ubVal) {
		return doMaximize ? ubVal <= maxObjValue : ubVal >= minObjValue;
	}

	public boolean checkLowerBound(boolean doMaximize, Number lbVal) {
		return lbVal != null && checkLowerBound(doMaximize, lbVal.intValue());
	}

	public boolean checkUpperBound(boolean doMaximize, Number ubVal) {
		return ubVal != null && checkUpperBound(doMaximize, ubVal.intValue());
	}

	public boolean checkOptimum(Number optVal) {
		return optVal != null && checkOptimum(optVal.intValue());
	}
	
	private void fail(ResolutionStatus status, Number objective)  throws SolutionCheckerException {
		throw new SolutionCheckerException("check-status...["+pretty()+"][status:"+status+"][obj:"+objective+ ']');
	}
	
	@Override
	public void checkStatus(Boolean doMaximize, ResolutionStatus status, Number objective) throws SolutionCheckerException {
		if(doMaximize == null) throw new SolutionCheckerException("check-status...[invalid-state]");
		switch (status) {
		case OPTIMUM: {
			if( ! checkOptimum(objective) ) fail(status, objective);
			else break;
		}
		case SAT: {
			if( ! checkUpperBound(doMaximize, objective)) fail(status, objective);
			else break;
		}
		case UNSAT : {
			fail(status, objective);
		}
		default: 
		}
	}

	@Override
	public String pretty() {
		return "check-optim:"+( minObjValue == maxObjValue ? minObjValue : "[" + minObjValue + ',' +maxObjValue+ ']');
	}

	@Override
	public String toString() {
		return pretty();
	}

	
}
