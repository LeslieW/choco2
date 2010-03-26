package choco.cp.solver.constraints.integer.bool.sat;

/**
 * A clause that can be added at any point in the search
 * Todo
 */
public final class DynWLClause extends WLClause {

    public DynWLClause(int[] ps, Lits voc) {
        super(ps, voc);
        nogood = true;
    }

    public boolean learnt() {
        return true;
    }

}
