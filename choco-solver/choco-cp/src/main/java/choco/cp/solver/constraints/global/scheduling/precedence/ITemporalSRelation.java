package choco.cp.solver.constraints.global.scheduling.precedence;

import choco.kernel.model.constraints.ITemporalRelation;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

public interface ITemporalSRelation extends ITemporalRelation<TaskVar<?>, IntDomainVar> {

	public final static int PRESERVED_PRECISION = 1 << 12;
	
	int getTotalSlack();
	
	double getForwardPreserved();
	
	double getBackwardPreserved();
	
}
