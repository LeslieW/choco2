package choco.cp.model.managers.variables;

import choco.cp.solver.constraints.global.flow.SCostCapaEdge;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableManager;
import choco.kernel.model.variables.flow.CostCapaEdge;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Mar 31, 2010
 * Time: 1:31:40 PM
 */
public class CostCapaEdgeManager  implements VariableManager<CostCapaEdge>
{
@Override
public Var makeVariable(Solver solver, CostCapaEdge variable)
{
       IntDomainVar v = solver.getVar(variable.capa);
       return new SCostCapaEdge(v,variable.dest,variable.cost);

}

@Override
public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars)
{
        return null;
}
}
