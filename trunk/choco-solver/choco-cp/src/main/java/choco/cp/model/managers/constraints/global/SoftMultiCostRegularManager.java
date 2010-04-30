package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.automata.fast_multicostregular.SoftMultiCostRegular;
import choco.kernel.model.constraints.automaton.FA.FiniteAutomaton;
import choco.kernel.model.constraints.automaton.penalty.PenaltyFunction;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Apr 27, 2010
 * Time: 5:39:49 PM
 */
public class SoftMultiCostRegularManager extends IntConstraintManager
{
@Override
public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, Set<String> options)
{
        IntDomainVar[] allVars = solver.getVar(variables);
        Object[] param = (Object[]) parameters;
        int xl = (Integer)param[0];
        int yl = (Integer) param[1];

        IntDomainVar[] x = new IntDomainVar[xl];
        System.arraycopy(allVars,0,x,0,xl);

        IntDomainVar[] y =  new IntDomainVar[yl];
        System.arraycopy(allVars,xl,y,0,yl);

        IntDomainVar[] z = new IntDomainVar[yl];
        System.arraycopy(allVars,xl+yl,z,0,yl);

        IntDomainVar Z = allVars[xl+2*yl];

        PenaltyFunction[] penalty  = (PenaltyFunction[]) param[2];
        FiniteAutomaton pi = (FiniteAutomaton) param[3];


        int[][][][] costs = (int[][][][]) param[4];

        return new SoftMultiCostRegular(x,y,z,Z,penalty,pi,costs,(CPSolver)solver);



}
}
