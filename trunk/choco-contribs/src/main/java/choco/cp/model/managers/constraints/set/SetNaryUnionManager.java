/*
 * Created on 19 août 08 by coletta 
 *
 */
package choco.cp.model.managers.constraints.set;

import choco.cp.model.managers.SetConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.set.SetNaryUnion;
import choco.kernel.model.ModelException;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.Set;

public class SetNaryUnionManager extends SetConstraintManager {

    
    @Override
    public SConstraint makeConstraint(Solver solver, SetVariable[] variables,
            Object parameters, Set<String> options) {
        if (solver instanceof CPSolver) {
            return new SetNaryUnion(solver.getVar((SetVariable[])variables), solver.getEnvironment());
        }
        throw new ModelException("Could not found implementation for setNaryUnion !");
    }
}