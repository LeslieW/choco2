package choco.cp.model.managers.constraints.integer;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.Exactly;
import choco.kernel.model.ModelException;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.List;

public final class ExactlyManager extends IntConstraintManager {

        /**
         * Build a constraint for the given solver and "model variables"
         *
         * @param solver
         * @param variables
         * @param parameters : a "hook" to attach any kind of parameters to constraints
         * @param options
         * @return
         */
        @Override
        public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, List<String> options) {
            if (solver instanceof CPSolver) {
                if (parameters instanceof int[]) {
                    int[] values = (int[]) parameters;
                    return new Exactly(solver.getVar(variables), values[0], values[1]);
                }
            }
            throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
        }
    }