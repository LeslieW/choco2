package choco.cp.common.util.preprocessor.detector.scheduling;

import static choco.Choco.endsAfterBegin;
import static choco.Choco.precedence;
import choco.cp.model.CPModel;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.TemporalConstraint;
import choco.kernel.model.variables.integer.IntegerVariable;


public final class PrecFromReifiedModelDetector extends AbstractTemporalDetector {

	public PrecFromReifiedModelDetector(CPModel model,
			DisjunctiveModel disjMod) {
		super(model, disjMod);
	}

	@Override
	protected ConstraintType getType() {
		return ConstraintType.PRECEDENCE_REIFIED;
	}
	
	@Override
	protected void apply(TemporalConstraint ct) {
		final IntegerVariable dir = ct.getDirection();
		if(dir.isConstant()) {
			delete(ct);
			if(dir.getLowB() == 0) {
				add(endsAfterBegin(ct.getOrigin(), ct.getDestination(), - ct.forwardSetup() - 1));
			} else {
				add(precedence(ct.getOrigin(), ct.getDestination(), ct.forwardSetup()));
			} 
		} else reformulateImpliedReified(ct);
	}

}
