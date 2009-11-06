package choco.cp.solver.constraints.integer.bool.sat;

import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.ContradictionException;
import choco.cp.solver.variables.integer.BooleanVarImpl;
import choco.cp.solver.variables.integer.BooleanDomain;

/**
 * A clause that can be added at any point in the search
 * Todo
 */
public class DynWLClause extends WLClause {

    public DynWLClause(int[] ps, Lits voc) {
        super(ps, voc);
    }

    public boolean learnt() {
        return true;
    }

}
