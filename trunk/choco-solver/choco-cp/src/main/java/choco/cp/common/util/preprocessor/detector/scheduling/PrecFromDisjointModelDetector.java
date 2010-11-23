package choco.cp.common.util.preprocessor.detector.scheduling;

import choco.cp.model.CPModel;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.TemporalConstraint;

public final class PrecFromDisjointModelDetector extends AbstractTemporalDetector {

	public PrecFromDisjointModelDetector(CPModel model, DisjunctiveModel disjMod) {
		super(model, disjMod);
	}

	@Override
	protected void apply(TemporalConstraint ct) {
		if(ct.isDirConstant()
				&& disjMod.safeAddArc(ct) ) delete(ct);
	}


	@Override
	protected ConstraintType getType() {
		return ConstraintType.PRECEDENCE_DISJOINT;
	}

}
