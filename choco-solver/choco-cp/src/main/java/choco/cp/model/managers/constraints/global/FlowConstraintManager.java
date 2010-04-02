package choco.cp.model.managers.constraints.global;

import choco.cp.solver.constraints.global.flow.FlowConstraint;
import choco.cp.solver.constraints.global.flow.SCapaEdge;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintManager;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Mar 31, 2010
 * Time: 1:33:38 PM
 */
public class FlowConstraintManager extends ConstraintManager<Variable>
{

@Override
public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, Set<String> options)
{
        int soidx = -1;
        int siidx = -1;
        if (parameters instanceof Object[])
        {
                Object[] param = (Object[]) parameters;
                if (param.length == 2)
                {
                        soidx = (Integer) param[0];
                        siidx = (Integer) param[1];

                }
        }

        int lgth = variables.length-1;
        int sqrt = (int) Math.sqrt(lgth);
        SCapaEdge[][] graph = new SCapaEdge[sqrt][sqrt];
        for (int i = 0 ; i < lgth ; i++)
        {
                graph[i/sqrt][i%sqrt] = (SCapaEdge) solver.getVar(variables[i]);
        }
        IntDomainVar var = (IntDomainVar) solver.getVar(variables[lgth]);
        if (soidx != -1)
                return new FlowConstraint(solver,graph,soidx,siidx,var);
        else
                return new FlowConstraint(solver,graph,var);
}

@Override
public SConstraint[] makeConstraintAndOpposite(Solver solver, Variable[] variables, Object parameters, Set<String> options)
{
        return new SConstraint[0];  //To change body of implemented methods use File | Settings | File Templates.
}

@Override
public int[] getFavoriteDomains(Set<String> strings) {
        return new int[]{IntDomainVar.BITSET,
                IntDomainVar.LINKEDLIST,
                IntDomainVar.BIPARTITELIST,
                IntDomainVar.BINARYTREE,
                IntDomainVar.BOUNDS,
        };
}
@Override
public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars)
{
        return null;
}
}
