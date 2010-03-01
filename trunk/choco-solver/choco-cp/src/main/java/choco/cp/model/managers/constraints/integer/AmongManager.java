package choco.cp.model.managers.constraints.integer;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.AmongGAC;
import choco.cp.solver.constraints.integer.Among;
import choco.kernel.model.ModelException;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.Set;

public class AmongManager extends IntConstraintManager {

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
        public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, Set<String> options) {
            if (solver instanceof CPSolver) {
                if (parameters instanceof int[]) {
                    int[] values = (int[]) parameters;
                    if(variables.length == 1){
                        return new Among(solver.getVar(variables[0]), values);
                    }else{
                        return new AmongGAC(solver.getVar(variables), values, solver.getEnvironment());
                    }

                }
            }
            throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
        }
    }
