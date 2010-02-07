package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.constraints.global.automata.fast_costregular.FastCostRegular;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Feb 5, 2010
 * Time: 5:54:26 PM
 */

public class FastCostRegularManager extends IntConstraintManager {

    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, HashSet<String> options) {

            if (parameters instanceof Object[] && ((Object[])parameters).length == 2)
            {
                IntDomainVar[] vars = (solver.getVar((IntegerVariable[]) variables));

                Automaton auto;
                double [][][] csts;
                Object[] tmp = (Object[]) parameters;
                try {
                    auto = (Automaton)tmp[0];
                    csts = (double[][][])tmp[1];
                }
                catch (Exception e)
                {
                    LOGGER.severe("Invalid parameters in costregular manager");
                    return null;
                }
                return new FastCostRegular(vars,auto,csts);

            }
            return null;

        }
}
