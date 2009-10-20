package choco.cp.solver.constraints.integer;

import choco.kernel.solver.variables.integer.IntDomainVar;

public final class IntSum extends AbstractIntLinComb {

	public IntSum(IntDomainVar[] lvars, int[] coeffs, int nbPosVars,
			int cste) {
		super(lvars, coeffs, nbPosVars, cste);
	}

	
	@Override
	protected int getInfNV(int i, int mylb) {
		return vars[i].getSup() + mylb;
	}

	@Override
	protected int getInfPV(int i, int myub) {
		return vars[i].getSup() - myub;
	}

	@Override
	protected int getSupNV(int i, int myub) {
		return vars[i].getInf() + myub;
	}

	@Override
	protected int getSupPV(int i, int mylb) {
		return vars[i].getInf() - mylb;
	}
	
	@Override
	public int computeLowerBound() {
		int lb = cste;
		for (int i = 0; i < nbPosVars; i++) {
			lb += vars[i].getInf();
		}
		for (int i = nbPosVars; i < vars.length; i++) {
			lb -= vars[i].getSup();
		}
		return lb;
	}

	
	@Override
	public int computeUpperBound() {
		int ub = cste;
		for (int i = 0; i < nbPosVars; i++) {
			ub += vars[i].getSup();
		}
		for (int i = nbPosVars; i < vars.length; i++) {
			ub -=  vars[i].getInf();
		}
		return ub;
	}
	


}
