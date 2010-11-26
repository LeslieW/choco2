package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.constraints.global.automata.fast_costregular.CostRegular;
import choco.kernel.model.constraints.automaton.FA.IAutomaton;
import choco.kernel.model.constraints.automaton.FA.ICostAutomaton;
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

public final class CostRegularManager extends IntConstraintManager {

public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, List<String> options) {

        if (parameters instanceof Object[] && ((Object[])parameters).length == 2)
        {
                IntDomainVar[] vars = (solver.getVar((IntegerVariable[]) variables));

                IAutomaton auto = null;
                DirectedMultigraph<Node, Arc> graph = null;
                Node source = null;
                Object[] tmp = (Object[]) parameters;
                try {
                        auto = (IAutomaton)tmp[0];
                        try {
                                int[][][]csts = (int[][][])tmp[1];
                                return new CostRegular(vars,auto,csts,solver);
                        }
                        catch (Exception e)
                        {
                                int[][] csts = (int[][])tmp[1];
                                return new CostRegular(vars,auto,csts,solver);
                        }
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

                return new CostRegular(vars,graph,source, solver);
        }
        else if (parameters instanceof ICostAutomaton)
        {
                IntDomainVar[] vars = (solver.getVar((IntegerVariable[]) variables));
                return new CostRegular(vars,(ICostAutomaton)parameters,solver);
        }
        return null;

}
}
