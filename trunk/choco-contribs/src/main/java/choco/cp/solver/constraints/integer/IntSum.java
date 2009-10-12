package choco.cp.solver.constraints.integer;

import choco.kernel.solver.variables.integer.IntDomainVar;

public final class IntSum extends IntLinComb2 {

	public IntSum(IntDomainVar[] lvars, int[] coeffs, int nbPosVars,
			int cste) {
		super(lvars, coeffs, nbPosVars, cste);
	}

	
	@Override
	protected final int getInfNV(int i, int mylb) {
		return vars[i].getSup() + mylb;
	}

	@Override
	protected final int getInfPV(int i, int myub) {
		return vars[i].getSup() - myub;
	}

	@Override
	protected final int getSupNV(int i, int myub) {
		return vars[i].getInf() + myub;
	}

	@Override
	protected final int getSupPV(int i, int mylb) {
		return vars[i].getInf() - mylb;
	}
	
	
	@Override
	protected final int computeLowerBoundPV() {
		int lb = 0; 
		for (int i = 0; i < nbPosVars; i++) {
			lb += vars[i].getInf();
		}
		return lb;
	}
	
	@Override
	protected final int computeLowerBoundNV() {
		int lb = 0; 
		for (int i = nbPosVars; i < vars.length; i++) {
			lb -= vars[i].getSup();
		}
		return lb;
	}


	@Override
	protected final int computeUpperBoundPV() {
		int ub = 0; 
		for (int i = 0; i < nbPosVars; i++) {
			ub += vars[i].getSup();
		}
		return ub;
	}
	
	@Override
	protected final int computeUpperBoundNV() {
		int ub = 0; 
		for (int i = nbPosVars; i < vars.length; i++) {
			ub -= vars[i].getInf();
		}
		return ub;
	}
	


}
