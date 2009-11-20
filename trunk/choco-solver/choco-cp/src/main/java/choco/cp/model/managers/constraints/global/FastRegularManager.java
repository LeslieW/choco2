package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.constraints.global.fast_regular.FastRegular;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Nov 20, 2009
 * Time: 3:05:34 PM
 */
public class FastRegularManager extends IntConstraintManager {
     public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, HashSet<String> options) {
            if (parameters instanceof Automaton)
            {
                Automaton auto = (Automaton) parameters;
                IntDomainVar[] vs = solver.getVar((IntegerVariable[]) variables);
                return new FastRegular(vs,auto);

            }
            return null;
        }

}
