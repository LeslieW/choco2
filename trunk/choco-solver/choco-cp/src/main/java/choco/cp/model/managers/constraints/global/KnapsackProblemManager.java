package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.constraints.global.automata.costregular.CostKnapsack;
import choco.cp.solver.constraints.global.automata.fast_costregular.FastCostKnapSack;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Jan 18, 2010
 * Time: 12:34:19 PM
 */

public class KnapsackProblemManager extends IntConstraintManager {


    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, Set<String> options) {
        IntegerVariable cVar = variables[variables.length-1];
        IntegerVariable bVar = variables[variables.length-2];
        IntegerVariable[] vars = new IntegerVariable[variables.length-2];

        System.arraycopy(variables, 0, vars, 0, vars.length);

        if (parameters instanceof Object[])
        {
            Object[] param = (Object[]) parameters;
            if (param.length == 2)
            {
                int[] p1 = (int[]) param[0];
                int[] p2 = (int[]) param[1];

                return new FastCostKnapSack(solver.getVar(vars),solver.getVar(bVar),solver.getVar(cVar),p1,p2);
                //return CostKnapsack.make(solver.getVar(vars),solver.getVar(bVar),solver.getVar(cVar),p1,p2);
            }
        }

        return null;
    }
}