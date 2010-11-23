package choco.cp.solver.search.integer.varselector.ratioselector.ratios.degree;

import choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils;
import choco.kernel.solver.variables.integer.IntDomainVar;

public final class IncDomWDegRatio extends DomDegRatio {
		
	public IncDomWDegRatio(IntDomainVar var) {
		super(var);
	}

	@Override
	public int initializeDivisor() {
		return DomWDegUtils.getVarExtension(var).get();
	}
	

}
