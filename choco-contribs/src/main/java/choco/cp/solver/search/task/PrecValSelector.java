package choco.cp.solver.search.task;

import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.ITask;


public interface PrecValSelector {

	int getBestVal(StoredPrecedence precedence);

}
