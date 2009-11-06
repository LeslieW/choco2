package choco.cp.solver.constraints.integer.bool.sat;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.VarEvent;

/**
 * A clause is a set of litterals used within the watched literals
 * propagation based global constraint for clauses (ClauseStore)
 */
public class WLClause {

    // a table of assignments (i,j) such that V_{i,j} x_i = j doit etre verifie pour la recherche future
    // Each assignment is indexed by an integer managed by the vocabulary
    protected final int[] lits;

    protected final Lits voc;

    protected boolean isreg = false;


    public WLClause(int[] ps, Lits voc) {
        lits = ps;
        this.voc = voc;
    }

    public int getLitZero() {
        return lits[0];
    }

    // jl = 0 or 1
    public void findLiteral(int start) {
        for (int i = start; i < lits.length; i++) {
            if (!voc.isFalsified(lits[i])) {
                int tlit = lits[start];
                lits[start] = lits[i];
                lits[i] = tlit;
                break;
            }
        }
    }

    /**
     * register this clause in the watching lists of the propagator.
     * Basically find two literals to watch
     *
     * @param propagator
     * @throws ContradictionException
     */
    public boolean register(ClauseStore propagator) throws ContradictionException {
        assert lits.length > 1;
        if (isreg) return true;
        findLiteral(0);  // find a non falsified literal and exchange it with lits[0]
        if (voc.isFalsified(lits[0])) { // if none, raise a contradiction
            propagator.fail();
        }
        findLiteral(1);  // find a second non falsified literal and exchange it with lits[1]

        // ajoute la clause a la liste des clauses controles.
        if (voc.isFalsified(lits[1])) { // if none, propagate lits[0]
            updateDomain();
        }
        if (voc.isFree(lits[0]) && voc.isFree(lits[1])) {
            isreg = true;
            voc.watch(lits[0], this);
            voc.watch(lits[1], this);
        }
        return isreg;
    }

    /**
     * propagate the clause because one of the watched literals has changed
     *
     * @param p     the watched literals that has just changed
     * @param idxcl the index of the clause within the propagator
     * @return if the literals being watche have changed
     * @throws ContradictionException
     */
    public boolean propagate(int p, int idxcl) throws ContradictionException {
        // Lits[1] doit contenir le litteral falsifie
        if (lits[0] == p) {
            lits[0] = lits[1];
            lits[1] = p;
        }

        //inutile de mettre a jour lits[1] si lits[0] est satisfait
        if (voc.isSatisfied(lits[0]))
            return false;

        // Recherche un nouveau litteral
        for (int i = 2; i < lits.length; i++) {
            if (!voc.isFalsified(lits[i])) {
                lits[1] = lits[i];
                lits[i] = p;
                voc.unwatch(p, idxcl);
                voc.watch(lits[1], this);
                return true;
            }
        }
        // La clause est unitaire ou nulle, propager lits[0]
        updateDomain();
        return false;
    }

    public void updateDomain() throws ContradictionException {
        if (lits[0] > 0) {
            voc.boolvars[lits[0]].instantiate(1, VarEvent.NOCAUSE);//propagator.cIndices[lits[0] - 1]);
        } else {
            voc.boolvars[-lits[0]].instantiate(0, VarEvent.NOCAUSE);//propagator.cIndices[-lits[0] - 1]);
        }
    }


    /**
     * propagate the clause from scratch
     *
     * @param propagator
     * @return
     * @throws ContradictionException
     */
    public boolean simplePropagation(ClauseStore propagator) throws ContradictionException {
        int ivalid = -1;
        for (int i = 0; i < lits.length; i++) {
            if (!voc.isFalsified(lits[i])) {
                if (ivalid != -1) return false;
                ivalid = i;
            }
        }
        if (ivalid == -1) {
            propagator.fail();
        } else {
            int litzero = lits[0];
            lits[0] = lits[ivalid];
            lits[ivalid] = litzero;
            updateDomain();
        }
        return true;
    }

    /**
     * @return true if the clause is properly watched by two free literals
     * @throws ContradictionException
     */
    public boolean update() throws ContradictionException {
        if (voc.isFalsified(lits[0]) && !voc.isSatisfied(lits[1])) {
            int temp = lits[0];
            lits[0] = lits[1];
            lits[1] = temp;
            updateDomain();
        } else if (voc.isFalsified(lits[1]) && !voc.isSatisfied(lits[0])) {
            updateDomain();
        }
        return voc.isFree(lits[0]) && voc.isFree(lits[1]);
    }


    public boolean learnt() {
        return false;
    }

    public int size() {
        return lits.length;
    }

    public Lits getVocabulary() {
        return voc;
    }

    public int[] getLits() {
        int[] tmp = new int[size()];
        System.arraycopy(lits, 0, tmp, 0, size());
        return tmp;
    }

    public boolean isSatisfied() {
        for (int i = 0; i < lits.length; i++) {
            if (voc.isSatisfied(lits[i])) return true;
        }
        return false;
    }

    public boolean isSatisfied(int[] tuple) {
        for (int i = 0; i < lits.length; i++) {
            if (voc.isSatisfied(lits[i], tuple[i])) return true;
        }
        return false;
    }

    public boolean isRegistered() {
        return isreg;
    }

    public String toString() {
        String clname = "";
        for (int i = 0; i < lits.length; i++) {
            if (lits[i] > 0) {
                clname += voc.boolvars[lits[i]];
            } else clname += "!" + voc.boolvars[-lits[i]];
            if (i < lits.length - 1)
            clname += " v ";
        }
        return clname;
    }

}
