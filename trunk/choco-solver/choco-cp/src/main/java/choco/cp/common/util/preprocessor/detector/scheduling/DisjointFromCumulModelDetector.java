package choco.cp.common.util.preprocessor.detector.scheduling;

import choco.cp.model.CPModel;
import choco.kernel.model.constraints.ConstraintType;

public final class DisjointFromCumulModelDetector extends DisjointFromDisjModelDetector {

		
	public DisjointFromCumulModelDetector(CPModel model,
			DisjunctiveModel disjMod) {
		super(model, disjMod);
	}

	@Override
	protected ConstraintType getType() {
		return ConstraintType.CUMULATIVE;
	}

	@Override
	protected boolean isDisjoint(PPResource rsc, int i, int j) {
		return rsc.getMinHeight(i) + rsc.getMinHeight(j) > rsc.getMaxCapa();
	}
	
	
}
