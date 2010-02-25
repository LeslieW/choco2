package choco.kernel.solver.variables.scheduling;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

public interface IRMakespan {

	IntDomainVar getMakespan();
	
	void updateInf(int value) throws ContradictionException;
	
	void updateSup(int value) throws ContradictionException;
}
