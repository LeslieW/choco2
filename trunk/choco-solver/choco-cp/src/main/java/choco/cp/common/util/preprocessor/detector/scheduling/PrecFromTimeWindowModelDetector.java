package choco.cp.common.util.preprocessor.detector.scheduling;

import choco.cp.common.util.preprocessor.AbstractDetector;
import choco.cp.model.CPModel;
import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.scheduling.TaskVariable;

public final class PrecFromTimeWindowModelDetector extends AbstractDetector {

	public final DisjunctiveModel disjMod;

	public PrecFromTimeWindowModelDetector(CPModel model, DisjunctiveModel disjMod) {
		super(model);
		this.disjMod = disjMod;
	}

	@Override
	public void apply() {
		assert disjMod.isEmpty();
		int n = model.getNbStoredMultipleVars();
		for (int i = 0; i < n; i++) {
			MultipleVariables mv = model.getStoredMultipleVar(i);
			if (mv instanceof TaskVariable) {
				TaskVariable t1 = (TaskVariable) mv;
				for (int j = i+1; j < n; j++) {
					mv = model.getStoredMultipleVar(j);
					if (mv instanceof TaskVariable) {
						TaskVariable t2 = (TaskVariable) mv;
						disjMod.safeAddArc(t1, t2);
						disjMod.safeAddArc(t2, t1);
					}
				}
			}
		}

	}
}