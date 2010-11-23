package choco.cp.common.util.preprocessor.detector.scheduling;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.TemporalConstraint;
import choco.kernel.model.variables.integer.IntegerVariable;


public final class PrecFromImpliedModelDetector extends AbstractTemporalDetector {

	public PrecFromImpliedModelDetector(CPModel model,
			DisjunctiveModel disjMod) {
		super(model, disjMod);
	}

	@Override
	protected ConstraintType getType() {
		return ConstraintType.PRECEDENCE_IMPLIED;
	}

	@Override
	protected void apply(TemporalConstraint ct) {
		final IntegerVariable dir = ct.getDirection();
		if(dir.isConstant()) {
			delete(ct);
			if(! dir.canBeEqualTo(0)) {
				add(Choco.precedence(ct.getOrigin(), ct.getDestination(), ct.forwardSetup()));
			}
		} else reformulateImpliedReified(ct);
	}
}