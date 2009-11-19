package choco.cp.solver.constraints.global.ordered;

/**
 * Created by IntelliJ IDEA.
 * User: thierry.petit(a)emn.fr
 * Date: 9 nov. 2009
 * Time: 17:59:00
 *
 */

import choco.cp.model.managers.IntConstraintManager;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

public class OrderedGccManager extends IntConstraintManager {

	public SConstraint makeConstraint(Solver solver,
            						  IntegerVariable[] variables,
            						  Object parameters,
            						  HashSet<String> options){
		IntDomainVar[] vars = new IntDomainVar[variables.length];
		for(int i=0; i<vars.length; i++) {
			vars[i] = solver.getVar(variables[i]);
		}
		Object[] params = (Object[]) parameters;
		int[] Imax = (int[]) params[0];
		int minBot = (Integer) params[1];
		return new OrderedGcc(vars,Imax,minBot);
	}

}
