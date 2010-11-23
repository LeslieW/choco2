package choco.cp.common.util.preprocessor.detector.scheduling;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.TemporalConstraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

public class DisjointFromDisjModelDetector extends AbstractRscDetector {

	public DisjointFromDisjModelDetector(CPModel model,
			DisjunctiveModel disjMod) {
		super(model, disjMod);
	}

	@Override
	protected ConstraintType getType() {
		return ConstraintType.DISJUNCTIVE;
	}

	protected boolean isDisjoint(PPResource rsc, int i, int j) {
		return true;
	}		
	
	@Override
	protected final void apply(PPResource rsc) {
		final int n = rsc.getParameters().getNbRegularTasks();
		for (int i = 0; i < n; i++) {
			final TaskVariable t1 = rsc.getTask(i);
			for (int j = i+1; j < n; j++) {
				final TaskVariable t2 = rsc.getTask(j);
				if( ! disjMod.containsRelation(t1, t2) && isDisjoint(rsc, i, j)) {
					IntegerVariable dir = Choco.makeBooleanVar(StringUtils.dirRandomName(t1.getName(), t2.getName()));
					TemporalConstraint c = (TemporalConstraint) Choco.precedenceDisjoint(t1, t2, dir);
					disjMod.addEdge(t1.getHook(), t2.getHook(), c);
					add(dir);add(c);
				}
			}
		}
	}
}
