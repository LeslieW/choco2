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

    // jl = 0 or 1
    public void findLiteral(int start) {
        int maxlevel = -1;
        int lit = -1;
        for (int i = start; i < lits.length; i++) {
            int var = (lits[i] < 0) ? -lits[i] : lits[i];
            BooleanVarImpl v = voc.boolvars[var];
            if (!v.isInstantiated()) {
                int tlit = lits[start];
                lits[start] = lits[i];
                lits[i] = tlit;
                return;
            } else {
                int levdec = getLevelDec(v);
                if (maxlevel <= levdec) {
                    maxlevel = levdec;
                    lit = i;
                }
            }
        }
        int tlit = lits[start];
        lits[start] = lits[lit];
        lits[lit] = tlit;
    }

    //retrieve the world to which the variable has been
    //intantiated
    public int getLevelDec(BooleanVarImpl v) {
        return ((BooleanDomain) v.getDomain()).getStoredList().findIndexOfInt(((BooleanDomain) v.getDomain()).getOffset());
    }

    public void register(ClauseStore propagator) throws ContradictionException {
        super.register(propagator);
        isreg = true;
    }

    public boolean isRegistered() {
        return isreg;
    }

    public boolean learnt() {
        return true;
    }

}
