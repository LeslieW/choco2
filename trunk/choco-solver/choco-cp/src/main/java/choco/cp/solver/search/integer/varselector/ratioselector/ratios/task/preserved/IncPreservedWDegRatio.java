package choco.cp.solver.search.integer.varselector.ratioselector.ratios.task.preserved;

import choco.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils;

public final class IncPreservedWDegRatio extends PreservedWDegRatio {

	public IncPreservedWDegRatio(ITemporalSRelation precedence) {
		super(precedence);
	}

	@Override
	public int initializeDivisor() {
		return DomWDegUtils.getVarExtension(getIntVar()).get();
	}

	
}
