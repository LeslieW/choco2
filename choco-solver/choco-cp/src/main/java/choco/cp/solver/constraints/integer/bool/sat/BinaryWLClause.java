package choco.cp.solver.constraints.integer.bool.sat;

import choco.kernel.solver.ContradictionException;

/**
 * A specific class for binary clauses
 */
public class BinaryWLClause extends WLClause {

    public BinaryWLClause(int[] ps, Lits voc) {
        super(ps, voc);
    }

    public boolean propagate(int p, int idxcl) throws ContradictionException {
        // Lits[1] doit contenir le litteral falsifie
        if (lits[0] == p) {
            lits[0] = lits[1];
            lits[1] = p;
        }
        //inutile de mettre a jour lits[1] si lits[0] est satisfait
        if (voc.isSatisfied(lits[0])) {
            return false;
        }
        // La clause est unitaire ou nulle, propager lits[0]
        updateDomain();
        return false;
    }

    public void updateDomain() throws ContradictionException {
        super.updateDomain();
    }
}
