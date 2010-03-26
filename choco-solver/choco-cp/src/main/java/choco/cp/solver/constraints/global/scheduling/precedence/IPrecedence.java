package choco.cp.solver.constraints.global.scheduling.precedence;

import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

public interface IPrecedence {
	
	IntDomainVar getBoolVar();
	
	TaskVar getOrigin();
	
	TaskVar getDestination();
	
}
