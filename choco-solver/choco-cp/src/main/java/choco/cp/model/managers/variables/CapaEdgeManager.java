package choco.cp.model.managers.variables;

import choco.cp.solver.constraints.global.flow.SCapaEdge;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableManager;
import choco.kernel.model.variables.flow.CapaEdge;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Mar 31, 2010
 * Time: 1:21:18 PM
 */
public class CapaEdgeManager  implements VariableManager<CapaEdge>
{
@Override
public Var makeVariable(Solver solver, CapaEdge variable)
{
       IntDomainVar v = solver.getVar(variable.capa);
       return new SCapaEdge(v,variable.dest);

}

@Override
public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars)
{
        return null;
}
}
