package choco.cp.common.util.preprocessor.detector.scheduling;

import choco.cp.common.util.preprocessor.detector.scheduling.DisjunctiveModel.AddEdgeStatus;
import choco.cp.model.CPModel;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.TemporalConstraint;

public final class DisjointModelDetector extends AbstractTemporalDetector {
	
	public DisjointModelDetector(CPModel model, DisjunctiveModel disjMod) {
		super(model, disjMod);
	}

	@Override
	protected void apply(TemporalConstraint ct) {
		if( ! ct.IsFixed() ) {
			final AddEdgeStatus status = disjMod.safeAddEdge(ct);
			if(status.deleteC) delete(ct);
			if(status.repV != null) replaceBy(ct.getDirection(), status.repV);
			if(status.oppV != null) replaceBy(ct.getDirection(), status.oppV);
		}
				
	}

	@Override
	protected ConstraintType getType() {
		return ConstraintType.PRECEDENCE_DISJOINT;
	}

}
