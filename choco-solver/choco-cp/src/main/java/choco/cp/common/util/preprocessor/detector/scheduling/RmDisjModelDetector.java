package choco.cp.common.util.preprocessor.detector.scheduling;

import choco.cp.model.CPModel;
import choco.kernel.model.constraints.ConstraintType;

public final class RmDisjModelDetector extends AbstractRscDetector {

	
	public RmDisjModelDetector(CPModel model) {
		super(model, null);
	}

	@Override
	protected ConstraintType getType() {
		return ConstraintType.DISJUNCTIVE;
	}
	
	@Override
	protected void apply(PPResource rsc) {
		if(rsc.getParameters().isRegular()) delete(rsc.getConstraint());
	}

	

	
}
