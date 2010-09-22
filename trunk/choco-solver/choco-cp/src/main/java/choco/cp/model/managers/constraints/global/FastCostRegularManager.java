package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.constraints.global.automata.fast_costregular.FastCostRegular;
import choco.kernel.model.constraints.automaton.FA.FiniteAutomaton;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.global.automata.fast_costregular.structure.Arc;
import choco.kernel.solver.constraints.global.automata.fast_costregular.structure.Node;
import choco.kernel.solver.variables.integer.IntDomainVar;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Feb 5, 2010
 * Time: 5:54:26 PM
 */

public final class FastCostRegularManager extends IntConstraintManager {

    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, List<String> options) {

        if (parameters instanceof Object[] && ((Object[])parameters).length == 2)
        {
            IntDomainVar[] vars = (solver.getVar((IntegerVariable[]) variables));

            FiniteAutomaton auto = null;
            int [][][] csts = new int[0][][];
            DirectedMultigraph<Node, Arc> graph = null;
            Node source = null;
            Object[] tmp = (Object[]) parameters;
            try {
                auto = (FiniteAutomaton)tmp[0];
                csts = (int[][][])tmp[1];
            }
            catch (Exception e)
            {
                try {
                    graph = (DirectedMultigraph<Node,Arc>) tmp[0];
                    source = (Node) tmp[1];
                }
                catch (Exception e2)
                {
                    LOGGER.severe("Invalid parameters in costregular manager");
                    return null;
                }
            }
            if (auto != null)
                return new FastCostRegular(vars,auto,csts, solver);
            else
                return new FastCostRegular(vars,graph,source, solver);
        }
        return null;

    }
}
