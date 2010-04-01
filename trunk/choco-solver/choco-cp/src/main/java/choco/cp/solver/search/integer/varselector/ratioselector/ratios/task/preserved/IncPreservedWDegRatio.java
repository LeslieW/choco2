package choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved;

import choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils;
import choco.kernel.solver.constraints.global.scheduling.IPrecedence;

public final class IncPreservedWDegRatio extends PreservedWDegRatio {

	public IncPreservedWDegRatio(IPrecedence precedence) {
		super(precedence);
	}

	@Override
	public int getDivisor() {
		return DIVISOR * DomWDegUtils.getVarExtension(getIntVar()).get();
	}

	
}
