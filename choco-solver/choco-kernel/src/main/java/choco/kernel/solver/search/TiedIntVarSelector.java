package choco.kernel.solver.search;

import choco.kernel.solver.variables.Var;

import java.util.List;

public interface TiedIntVarSelector<V extends Var> {

	List<V> selectTiedIntVars();
}
