package choco.cp.solver.constraints.integer.bool.sat;

import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.cp.solver.variables.integer.BooleanVarImpl;

/**
 * A literal is a boolean variable or its negation
 * This structure stores the lists of watched literals to ensure
 * propagation of clauses
 */
public class Lits {

    //number of boolean variables
    protected int nblits;

    protected BooleanVarImpl[] boolvars;

    // watches for assignments x_i = 1
    protected Vec<WLClause>[] poswatches;

    // watches for assignments x_i = 0
    protected Vec<WLClause>[] negwatches;


    
    public void init(IntDomainVar[] vars) {
        boolvars = new BooleanVarImpl[vars.length+1];
        for (int i = 1; i < vars.length+1; i++) {
            boolvars[i] = (BooleanVarImpl) vars[i-1];
        }
        nblits = boolvars.length + 1;
        poswatches = new Vec[nblits];
        negwatches = new Vec[nblits];
    }

    public boolean isFree(int lit) {
        //negative indexes denote negative literals,
        //similarly positive indexes denote positive literal
        if (lit < 0) {
            return !boolvars[-lit].isInstantiated();
        } else {
            return !boolvars[lit].isInstantiated();
        }
    }

    public boolean isFalsified(int lit) {
        //negative indexes denote negative literals,
        //similarly positive indexes denote positive literal
        if (lit < 0) {
            return boolvars[-lit].isInstantiatedTo(1);
        } else {
            return boolvars[lit].isInstantiatedTo(0);
        }
    }

    public boolean isSatisfied(int lit) {
        if (lit < 0) {
            return boolvars[-lit].isInstantiatedTo(0);
        } else {
            return boolvars[lit].isInstantiatedTo(1);
        }
    }

    public boolean isPositive(int lit) {
        return lit > 0;
    }

    public void watch(int lit, WLClause c) {
        if (lit < 0) {
            int rlit = -lit;
            if (poswatches[rlit] == null) {
                poswatches[rlit] = new Vec<WLClause>();
            }
            poswatches[rlit].push(c);
        } else {
            if (negwatches[lit] == null) {
                negwatches[lit] = new Vec<WLClause>();
            }
            negwatches[lit].push(c);
        }
    }

    public void unwatch(int lit, WLClause c) {
        if (lit < 0) {
            poswatches[-lit].remove(c);
        } else {
            negwatches[lit].remove(c);
        }
    }

    public void unwatch(int lit, int idxClause) {
        if (lit < 0) {
            poswatches[-lit].delete(idxClause);
        } else {
            negwatches[lit].delete(idxClause);
        }
    }

    public Vec<WLClause> pos_watches(int idx) {
        return poswatches[idx];// - offsets[idx]];
    }

    public Vec<WLClause> neg_watches(int idx) {
        return negwatches[idx];// - offsets[idx]];
    }

    public Vec<WLClause> watches(int lit) {
        if (lit < 0) {
            return poswatches[-lit];
        } else return negwatches[lit];
    }

    public void reset() {
        for (int i = 0; i < nblits; i++) {
            poswatches[i] = null;
            negwatches[i] = null;
        }
    }

}
