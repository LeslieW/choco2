package parser.instances.checker;

import parser.instances.ResolutionStatus;
import choco.IPretty;
import choco.kernel.solver.search.checker.SolutionCheckerException;

public interface IStatusChecker extends IPretty {
	
	void checkStatus(Boolean doMaximize, ResolutionStatus status, Number objective) throws SolutionCheckerException;

}
