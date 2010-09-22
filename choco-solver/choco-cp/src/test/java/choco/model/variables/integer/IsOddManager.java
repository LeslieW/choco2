package choco.model.variables.integer;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.List;

//totex isoddmanager
public class IsOddManager extends IntConstraintManager {
    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, List<String> options) {
        if (solver instanceof CPSolver) {
            return new IsOdd(solver.getVar(variables[0]));
        }
        return null;
    }
}
//totex
