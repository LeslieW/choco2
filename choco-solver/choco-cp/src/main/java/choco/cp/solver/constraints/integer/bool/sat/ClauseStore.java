package choco.cp.solver.constraints.integer.bool.sat;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.propagation.VarEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.memory.IStateBitSet;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.trailing.StoredInt;
import gnu.trove.TLongIntHashMap;

import java.util.*;
import java.util.logging.Level;

/**
 * A global constraint to store and propagate all clauses
 */
public class ClauseStore extends AbstractLargeIntSConstraint {


    public static boolean nonincprop = false;

    //a flag to allow quick entailment tests
    public boolean efficient_entailment_test = false;

    // a data structure for managing all variable/value pairs
    protected Lits voc;

    protected ArrayList<WLClause> listclause;

    protected LinkedList<WLClause> listToPropagate;

    // if we get clause of size one, we instantiate them directly
    // to the correct value
    protected LinkedList<IntDomainVar> instToOne;
    protected LinkedList<IntDomainVar> instToZero;

    private final TLongIntHashMap indexes;

    protected int[] fineDegree;

    protected int nbNonBinaryClauses;
    //clause_entailed[i][0] : the set of NOT BINARY clauses (as a bitset of indexes)
    //                        entailed by setting x_i to 0
    //clause_entailed[i][1] : the set of NOT BINARY clauses (as a bitset of indexes)
    //                        entailed by setting x_i to 1    
    protected IStateBitSet[][] clauses_entailed;
    //the set of NOT BINARY clauses entailed
    protected IStateBitSet tot_clauses_entailed;

    /**
     * @param vars must be a table of BooleanVarImpl
     */
    public ClauseStore(IntDomainVar[] vars) {
        this(vars, new ArrayList<WLClause>(), new Lits());
        voc.init(vars);
    }

    public ClauseStore(IntDomainVar[] vars, ArrayList<WLClause> listclause, Lits voc) {
        super(vars);
        solver = vars[0].getSolver();
        this.voc = voc;
        this.listclause = listclause;
        listToPropagate = new LinkedList<WLClause>();
        instToOne = new LinkedList<IntDomainVar>();
        instToZero = new LinkedList<IntDomainVar>();
        nbNonBinaryClauses = 0;
        fineDegree = new int[vars.length];
        indexes = new TLongIntHashMap(vars.length);
        for (int v = 0; v < vars.length; v++) {
            indexes.put(vars[v].getIndex(), v);
        }
    }

    public ArrayList<WLClause> getClauses() {
        return listclause;
    }

    public void setEfficientEntailmentTest() {
        efficient_entailment_test = true;
    }

    public void clearEfficientEntailmentTest() {
            efficient_entailment_test = false;
    }


    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
    }

    public Lits getVoc() {
        return voc;
    }


    public void awakeOnInst(int idx) throws ContradictionException {
        if (nonincprop) {
            constAwake(false);
        } else {
            filterOnInst(idx);
        }
    }

    public void filterOnInst(int idx) throws ContradictionException {
        int val = vars[idx].getVal();
        int sidx = idx + 1;
        if (val == 1) {
            int vocidx = -sidx;
            Vec<WLClause> wlist = voc.watches(vocidx);
            if (wlist != null) {
                for (int i = 0; i < wlist.size(); i++) {
                    WLClause clause = wlist.get(i);
                    if (clause.propagate(vocidx, i)) i--;
                }
            }
            //for efficient entailment tests
            if (efficient_entailment_test) {
                tot_clauses_entailed.or(clauses_entailed[idx][1]);
            }
        } else {
            Vec<WLClause> wlist = voc.watches(sidx);
            if (wlist != null) {
                for (int i = 0; i < wlist.size(); i++) {
                    WLClause clause = wlist.get(i);
                    if (clause.propagate(sidx, i)) i--;
                }
            }
            //for efficient entailment tests
            if (efficient_entailment_test) {
                tot_clauses_entailed.or(clauses_entailed[idx][0]);
            }
        }
    }


    public void addClause(int[] lits) {
        listclause.add(new WLClause(lits, voc));
    }

    public IntDomainVar[] removeRedundantVars(IntDomainVar[] vs) {
        HashSet<IntDomainVar> filteredVars = new HashSet<IntDomainVar>();
        for (int i = 0; i < vs.length; i++) {
            if (!filteredVars.contains(vs[i]))
                filteredVars.add(vs[i]);
        }
        IntDomainVar[] filteredTab = new IntDomainVar[filteredVars.size()];
        filteredVars.toArray(filteredTab);
        return filteredTab;
    }

    public int[] computeLits(IntDomainVar[] plit, IntDomainVar[] nlit) {
        int[] lits = new int[plit.length + nlit.length];
        int cpt = 0;
        for (IntDomainVar aPlit : plit) {
            int lit = findIndex(aPlit);
            lits[cpt] = lit;
            cpt++;
        }
        for (IntDomainVar aNlit : nlit) {
            int lit = findIndex(aNlit);
            lits[cpt] = -lit;
            cpt++;
        }
        return lits;
    }

    public void updateDegree(int[] lit) {
        for (int i = 0; i < lit.length; i++) {
            int l = (lit[i] < 0) ? -lit[i] - 1 : lit[i] - 1;
            fineDegree[l]++;
        }
    }

    /**
     * add a clause in the store
     * WARNING : this method assumes that the variables are
     * in the scope of the ClauseStore
     *
     * @param positivelits
     * @param negativelits
     */
    public void addClause(IntDomainVar[] positivelits, IntDomainVar[] negativelits) {
        IntDomainVar[] plit = removeRedundantVars(positivelits);
        IntDomainVar[] nlit = removeRedundantVars(negativelits);

        int[] lits = computeLits(plit, nlit);
        updateDegree(lits);
        if (lits.length == 1) { //dealing with clauses of size one
            if (plit.length == 1) {
                instToOne.add(vars[lits[0] - 1]);
            } else {
                instToZero.add(vars[-lits[0] - 1]);
            }
        } else {
            WLClause cl;
            if (lits.length == 2)
                cl = new BinaryWLClause(lits, voc);
            else cl = new WLClause(lits, voc);
            cl.setIdx(listclause.size());
            listclause.add(cl);
            if (lits.length > 2) nbNonBinaryClauses++;
        }
    }

    public int findIndex(IntDomainVar v) {
        return indexes.get(v.getIndex()) + 1;
    }

    public DynWLClause addNoGood(IntDomainVar[] positivelits, IntDomainVar[] negativelits) {
        IntDomainVar[] plit = removeRedundantVars(positivelits);
        IntDomainVar[] nlit = removeRedundantVars(negativelits);

        int[] lits = computeLits(plit, nlit);
        updateDegree(lits);
        if (lits.length == 1) { //dealing with clauses of size one
            if (plit.length == 1) {
                instToOne.add(vars[lits[0] - 1]);
            } else {
                instToZero.add(vars[-lits[0] - 1]);
            }
            return null;
        } else {
            DynWLClause clause = new DynWLClause(lits, voc);
            clause.setIdx(listclause.size());
            listclause.add(clause);
            listToPropagate.addLast(clause);
            if (lits.length > 2) nbNonBinaryClauses++;
            return clause;
        }
    }

    /**
     * Add a clause given the set of literals
     *
     * @param lits
     * @return
     */
    public DynWLClause fast_addNoGood(int[] lits) {
        updateDegree(lits);
        if (lits.length == 1) { //dealing with clauses of size one
            if (lits[0] > 0) {
                instToOne.add(vars[lits[0] - 1]);
            } else {
                instToZero.add(vars[-lits[0] - 1]);
            }
            return null;
        } else {
            DynWLClause clause = new DynWLClause(lits, voc);
            clause.setIdx(listclause.size());
            listclause.add(clause);
            listToPropagate.addLast(clause);
            if (lits.length > 2) nbNonBinaryClauses++;
            return clause;
        }
    }

    /**
     * Remove a clause from the store
     *
     * @param wlc
     */
    public void delete(WLClause wlc) {
        if (wlc.getIdx() != (listclause.size() - 1)) {
            WLClause lastclause = listclause.remove(listclause.size() - 1);
            listclause.set(wlc.getIdx(), lastclause);
            lastclause.setIdx(wlc.getIdx());
        } else listclause.remove(listclause.size() - 1);
        if (wlc.getLits().length > 2) nbNonBinaryClauses--;
        listToPropagate.remove(wlc);
        wlc.unregister();
    }

    public void createEntailmentStructures() {
        tot_clauses_entailed = solver.getEnvironment().makeBitSet(listclause.size());
        clauses_entailed = new IStateBitSet[vars.length][2];
        for (int i = 0; i < vars.length; i++) {
            clauses_entailed[i][0] = solver.getEnvironment().makeBitSet(nbNonBinaryClauses);
            clauses_entailed[i][1] = solver.getEnvironment().makeBitSet(nbNonBinaryClauses);
            clauses_entailed[i][0].clear();
            clauses_entailed[i][1].clear();
        }
    }

    public void initEntailmentStructures() {
        int idxcl = 0;
        for (WLClause cl : listclause) {
            int[] lits = cl.lits;
            for (int i = 0; i < lits.length; i++) {
                int lit = lits[i];
                if (lit > 0) {
                    clauses_entailed[lit - 1][1].set(idxcl);
                } else {
                    clauses_entailed[-lit - 1][0].set(idxcl);
                }
            }
            idxcl++;

        }
    }

    public void awake() throws ContradictionException {
        if (efficient_entailment_test) {
            createEntailmentStructures();
            initEntailmentStructures();
        }

        for (WLClause cl : listclause) {
            if (!cl.isRegistered())
                cl.register(this);
        }
        propagate();
    }

    public void propagateUnitClause() throws ContradictionException {
        for (IntDomainVar v : instToOne) {
            v.instantiate(1, VarEvent.NOCAUSE);
        }
        for (IntDomainVar v : instToZero) {
            v.instantiate(0, VarEvent.NOCAUSE);
        }
    }

    public void propagate() throws ContradictionException {
        if (nonincprop) {
            filterFromScratch();
        } else {
            for (Iterator<WLClause> iterator = listToPropagate.iterator(); iterator.hasNext();) {
                WLClause cl = iterator.next();
                if (cl.register(this)) {
                    iterator.remove();
                }
            }
            propagateUnitClause();
        }
    }

    public void filterFromScratch() throws ContradictionException {
        for (WLClause cl : listclause) {
            cl.simplePropagation(this);
        }
    }

    public Boolean isEntailed() {
        if (efficient_entailment_test) {
            //System.out.println("card: " + tot_clauses_entailed.cardinality());
            //System.out.println("nbin: " + nbBinaryClauseEntailed.get());
            if (tot_clauses_entailed.cardinality() == listclause.size()) {
                return Boolean.TRUE;
            }
            return null;
        } else {
            boolean unknownflag = false;
            for (WLClause cl : listclause) {
                Boolean b = cl.isEntailed();
                if (b != null) {
                    if (!b) return Boolean.FALSE;
                } else unknownflag = true;
            }
            if (unknownflag) return null;
            else return Boolean.TRUE;
        }
    }

    public boolean isSatisfied() {
        for (WLClause cl : listclause) {
            // only check static clauses,
            // because nogoods can be unsatisfied due to the backtrack
            if (!cl.isNogood()) {
                if (!cl.isSatisfied())
                    return false;
            }
        }
        return true;
    }

    public boolean isSatisfied(int[] tuple) {
        for (WLClause cl : listclause) {
            // only check static clauses,
            // because nogoods can be unsatisfied due to the backtrack
            if (!cl.isNogood()) {
                int[] lit = cl.getLits();
                int[] clt = new int[lit.length];
                for (int i = 0; i < lit.length; i++) {
                    //the literals are offset by one
                    clt[i] = tuple[Math.abs(lit[i]) - 1];
                }
                if (!cl.isSatisfied(clt))
                    return false;
            }
        }
        return true;
    }

    //by default, no information is known
    public int getFineDegree(int idx) {
        return fineDegree[idx];    //To change body of overridden methods use File | Settings | File Templates.
    }

    public int getNbEntailedClauseFrom(int idx, int val) {
        int nbentailedclause = 0;
        for (int i = clauses_entailed[idx][val].nextSetBit(0); i > 0; i = clauses_entailed[idx][val].nextSetBit(i + 1)) {
            if (!tot_clauses_entailed.get(i)) {
                nbentailedclause++;
            }
        }
        return nbentailedclause;
    }

    public int getNbClause() {
        return listclause.size();
    }

    public final void printClauses() {
        if (LOGGER.isLoggable(Level.INFO)) {
            StringBuilder b = new StringBuilder();
            for (WLClause wlClause : listclause) {
                b.append(wlClause);
            }
            LOGGER.info(new String(b));
        }
    }
}

