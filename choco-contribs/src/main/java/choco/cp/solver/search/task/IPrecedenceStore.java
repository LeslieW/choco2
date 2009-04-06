package choco.cp.solver.search.task;

import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.ITask;

public interface IPrecedenceStore {

	StoredPrecedence getStoredPrecedence(ITask t1, ITask t2);
	
	void addPrecedence(ITask t1, ITask t2, IntDomainVar direction);

	boolean isReified(ITask t1, ITask t2);
	
	int getNbReifiedPrecedence();
	
	boolean containsReifiedPrecedence();

}

