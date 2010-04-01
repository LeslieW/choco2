package choco.kernel.solver.constraints.global.scheduling;

import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

public interface IPrecedence {
	
	IntDomainVar getBoolVar();
	
	TaskVar getOrigin();
	
	TaskVar getDestination();
	
}
