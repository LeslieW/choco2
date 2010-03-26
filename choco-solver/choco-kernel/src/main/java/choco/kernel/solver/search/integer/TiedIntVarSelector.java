package choco.kernel.solver.search.integer;

import java.util.List;

import choco.kernel.solver.variables.integer.IntDomainVar;

public interface TiedIntVarSelector {

	List<IntDomainVar> selectTiedIntVars();
}
