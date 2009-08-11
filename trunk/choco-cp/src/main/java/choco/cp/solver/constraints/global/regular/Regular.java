/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.global.regular;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.IntEnumeration;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.structure.StoredIndexedBipartiteSet;
import choco.kernel.memory.trailing.IndexedObject;
import choco.kernel.model.constraints.automaton.DFA;
import choco.kernel.model.constraints.automaton.LightLayeredDFA;
import choco.kernel.model.constraints.automaton.LightState;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.propagation.VarEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.*;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Enforce the sequence of variable vs to be a word recognized by DFA auto
 */
public class Regular extends AbstractLargeIntSConstraint {


    public final static boolean INCREMENTAL = true;
    public final static boolean DEBUG = false;

    // the list of states acting as support of assignment V_i = j
    protected StoredIndexedBipartiteSet[] Qij;

    // data structure to speedup the acces to index of Q_ij
    protected int[] offset;
    protected int[] start;
    protected int[] sizes;

    protected LightLayeredDFA autom;

    protected int nbNode;
    /**
     * Stored data structured map to the original automaton
     */
    protected PropagationData sdata;

    public Regular(DFA auto, IntDomainVar[] vs, int[] lbs, int[] dsize) {
        super(vs);
        init(auto.lightGraph, lbs, dsize);
    }

    /**
     * Enforce the sequence of variable vs to be a word recognized by DFA auto
     *
     * @param auto
     * @param vs
     */
    public Regular(DFA auto, IntDomainVar[] vs) {
        super(vs);
        int[] offset = new int[vars.length];
        int[] sizes = new int[vars.length];
        for (int i = 0; i < vars.length; i++) {
            offset[i] = vars[i].getInf();
            sizes[i] = vars[i].getSup() - vars[i].getInf() + 1;
        }
        init(auto.lightGraph, offset, sizes);
    }

    public void init(LightLayeredDFA auto, int[] lbs, int[] dsize) {
        autom = auto;
        cste = vars.length;
        start = new int[vars.length];
        offset = lbs;
        sizes = dsize;
        // nbNode = autom.getNbStates();
        nbNode = autom.getAutomateSize();
        sdata = new PropagationData(this);
        start[0] = 0;
        for (int i = 0; i < vars.length; i++) {
            if (i > 0) start[i] = start[i - 1] + sizes[i - 1];
        }
        Qij = new StoredIndexedBipartiteSet[start[cste - 1] + sizes[cste - 1]];
        ArrayList<IndexedObject>[] qijvalues = new ArrayList[Qij.length];
        for (int i = 0; i < qijvalues.length; i++) {
            qijvalues[i] = new ArrayList<IndexedObject>();
        }
        initQij(qijvalues);
        for (int i = 0; i < Qij.length; i++) {
            Qij[i] = (StoredIndexedBipartiteSet) vars[0].getSolver().getEnvironment().makeBipartiteSet(qijvalues[i]);
        }
    }

    //<hca> this is initialized regarding the current domains and thus
    // can not be used as a cut...
    public void initQij(ArrayList[] qijvalues) {
        mark = new BitSet(nbNode);
        Ni = new ArrayList[cste + 1]; // le dernier niveau
        for (int i = 0; i < cste + 1; i++) {
            Ni[i] = new ArrayList<LightState>();
        }
        Ni[0].add(autom.getInitState());
        for (int i = 0; i < Ni.length - 1; i++) {
            for (LightState st : Ni[i]) {
                DisposableIntIterator domIt = vars[i].getDomain().getIterator();
                for (; domIt.hasNext();) {
                    int val = domIt.next();
                    if (st.hasDelta(val - autom.getOffset(i))) {
                        qijvalues[start[i] + val - offset[i]].add(st);
                        LightState nst = st.delta(val - autom.getOffset(i));
                        if (!mark.get(nst.getIdx())) { // st is a candidate for support
                            Ni[i + 1].add(nst);
                            mark.set(nst.getIdx());
                        }
                    }
                }
                domIt.dispose();
            }
        }
    }

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.REMVALbitvector;
        // return 0x0B;
    }

    public StoredIndexedBipartiteSet getQij(int var, int val) {
        return Qij[start[var] + val - offset[var]];
    }

    /***************************************************/
    /*************** Initial propagation ***************/
    /***************************************************/


    // temporary data structures to intialize the Qij set
    protected ArrayList<LightState>[] Ni; // the list of states of each level (each variable)
    protected BitSet mark;
    protected HashSet<IndexedObject>[] qijvalues;

    public void initData() {
        mark = new BitSet(nbNode);
        qijvalues = new HashSet[Qij.length];
        for (int i = 0; i < Qij.length; i++) {
            qijvalues[i] = new HashSet<IndexedObject>();
        }
        Ni = new ArrayList[cste + 1]; // le dernier niveau
        for (int i = 0; i < cste + 1; i++) {
            Ni[i] = new ArrayList<LightState>();
        }
    }

    /**
     * marks allow to know whether a state is reachable from q_0 (during
     * the forward phase) or whether a state can not reach q_n (during the backward phase).
     * they are therefore re-initialized between the two phase
     */
    public void initMarck() {
        mark.clear();
        mark.set(0);
        mark.set(nbNode - 1);
    }


    /**
     * Only consider states st that can be reached from q0 (which are on a path (qo ~> st))
     */
    public void forwardUpdate() {
        Ni[0].add(autom.getInitState());
        for (int i = 0; i < Ni.length - 1; i++) {
            forwardOnLevel(i);
        }
    }

    public void forwardOnLevel(int i) {
        for (LightState st : Ni[i]) {
            DisposableIntIterator domIt = vars[i].getDomain().getIterator();
            for (; domIt.hasNext();) {
                int val = domIt.next();
                if (st.hasDelta(val - autom.getOffset(i))) {
                    qijvalues[start[i] + val - offset[i]].add(st);                    
                    LightState nst = st.delta(val - autom.getOffset(i));
                    if (!mark.get(nst.getIdx())) { // st is a candidate for support                       
                        Ni[i + 1].add(nst);
                        mark.set(nst.getIdx());
                    }
                }
            }domIt.dispose();
        }
    }


    /**
     * Only consider states st that reached qn (which are on a path (st ~> qn))
     */
    public void backwardUpdate() {
        for (int i = Ni.length - 2; i >= 0; i--) {
            backward2OnLevel(i);
            backwardOnLevel(i);
            for (Iterator it = Ni[i].iterator(); it.hasNext();) {
                LightState st = (LightState) it.next();
                if (!mark.get(st.getIdx()))
                    it.remove();
            }
        }
    }

    public void backwardOnLevel(int i) {
        DisposableIntIterator domIt = vars[i].getDomain().getIterator();
        for (; domIt.hasNext();) {
            int val = domIt.next();
            StoredIndexedBipartiteSet qij = getQij(i, val);
            StoredIndexedBipartiteSet.BipartiteSetIterator it = qij.getObjectIterator();
            while(it.hasNext()) {
                LightState st = (LightState) it.nextObject();
                LightState nst = st.delta(val - autom.getOffset(i));
                if (nst != null && mark.get(nst.getIdx())) { //isMark(ctIdx)) {     // st confirmed as a support
                    mark.set(st.getIdx()); //st.mark(ctIdx);
                    sdata.incrementOutdeg(st);
                    sdata.incrementIndeg(nst);
                } else {
                    it.remove();
                }
            }
            it.dispose();
        }
        domIt.dispose();
    }

    public void backward2OnLevel(int i) {
        DisposableIntIterator domIt = vars[i].getDomain().getIterator();
        for (; domIt.hasNext();) {
            int val = domIt.next();
            StoredIndexedBipartiteSet qij = getQij(i, val);
            StoredIndexedBipartiteSet.BipartiteSetIterator it = qij.getObjectIterator();
            while(it.hasNext()) {
                LightState st = (LightState) it.nextObject();
                if (!qijvalues[start[i] + val - offset[i]].contains(st)) { //isMark(ctIdx)) {     // st confirmed as a support
                    it.remove();
                }
            }
            it.dispose();
        }
        domIt.dispose();
    }


    /**
     * removes values that are not supported by any state of the automata
     *
     * @throws choco.kernel.solver.ContradictionException
     *
     */
    public void cleanUp() throws ContradictionException {
        for (int i = 0; i < cste; i++) {
            int fin = i == (cste - 1) ? Qij.length : start[i + 1];
            for (int j = start[i]; j < fin; j++) {
                if (Qij[j].isEmpty()) {
                    int val = j - start[i];
                    if (vars[i].canBeInstantiatedTo(val + offset[i])) {// why Qij is empty ?
                        prune(i, val + offset[i]);
                    }
                }
            }
        }
    }

    public void propagate() throws ContradictionException {
        if (!autom.isEmpty()) {
            sdata.resetPropagationData(nbNode);
            initData();
            initMarck();
            forwardUpdate();
            initMarck();
            backwardUpdate();
            cleanUp();
            mark = null; // free memory
            Ni = null;
        } else this.fail();
    }

    /*******************************************************/
    /*************** Incremental propagation ***************/
    /*******************************************************/

    /**
     *
     * @param i
     * @param val
     * @throws ContradictionException
     */
    public void prune(int i, int val) throws ContradictionException {
        if (DEBUG && vars[i].canBeInstantiatedTo(val))
            LOGGER.log(Level.INFO, "remove {0} from {1}", new Object[]{val, vars[i]});
        vars[i].removeVal(val, cIndices[i]);
    }


    public void awake() throws ContradictionException {
        propagate();
    }

    /**
     * Incremental propagation of a value removal
     *
     * @throws ContradictionException
     */
    public void propagateRemoval(int i, int j) throws ContradictionException {
        StoredIndexedBipartiteSet qij = getQij(i, j);
        for (int k = 0; k < qij.size(); k++) {
            LightState st = (LightState) qij.getObject(k);
            LightState nst = st.delta(j - autom.getOffset(i));
            decrement_outdeg(st, i);
            decrement_indeg(nst, i + 1);
        }
        qij.clear();
    }

    /**
     * Decrement the out-degree of state st located on the i-th layer
     */
    public void decrement_outdeg(LightState st, int i) throws ContradictionException {
        sdata.decrementOutdeg(st);
        if (sdata.getOutdeg(st) == 0) {
            propagateNullOutDeg(st, i);
        }
    }

    public void propagateNullOutDeg(LightState st, int i) throws ContradictionException {
        Enumeration pred = st.getEnumerationPred();
        while (pred.hasMoreElements()) {
            LightState.Arcs ap = (LightState.Arcs) pred.nextElement();
            LightState pst = ap.getSt();
            IntEnumeration valpred = ap.getEnumerationPred();
            if (sdata.isAccurate(pst)) {
                while (valpred.hasMoreElements()) {
                    int val = valpred.nextElement();
                    int realval = val + autom.getOffset(i - 1);
                    if (vars[i - 1].canBeInstantiatedTo(realval)) {
                        StoredIndexedBipartiteSet supports = getQij(i - 1, realval);
                        supports.remove(pst);
                        if (supports.isEmpty()) {
                            prune(i - 1, realval);
                        }
                        decrement_outdeg(pst, i - 1);
                    }
                }
            }
        }
    }

    /**
     * Decrement the in-degree of state st located on the i-th layer
     */
    public void decrement_indeg(LightState st, int i) throws ContradictionException {
        sdata.decrementIndeg(st);
        if (sdata.getIndeg(st) == 0) {
            propagateNullInDeg(st, i);
        }
    }

    public void propagateNullInDeg(LightState st, int i) throws ContradictionException {
        Enumeration succ = st.getEnumerationSucc();
        while (succ.hasMoreElements()) {
            int val = (Integer) succ.nextElement();
            int realval = val + autom.getOffset(i);
            LightState nst = st.delta(val);
            if (vars[i].canBeInstantiatedTo(realval)) {
                StoredIndexedBipartiteSet supports = getQij(i, realval);
                supports.remove(st);
                if (supports.isEmpty()) {
                    prune(i, realval);
                }
                decrement_indeg(nst, i + 1);
            }
        }
    }


    public void awakeOnRem(int idx, int x) throws ContradictionException {
        if (DEBUG) LOGGER.log(Level.INFO, "----------------On recoit {0} != {1}", new Object[]{vars[idx], x});
        if (INCREMENTAL) {// && domaincopy[idx].get(x - offset[idx])) {

            propagateRemoval(idx, x);
        } else this.constAwake(false);
        if (!vars[idx].hasEnumeratedDomain()){
            StoredIndexedBipartiteSet supports = getQij(idx, vars[idx].getInf());
            if (supports.isEmpty()) {
                vars[idx].removeVal(vars[idx].getInf(), VarEvent.domOverWDegIdx(cIndices[idx]));
            }
            supports = getQij(idx, vars[idx].getSup());
            if (supports.isEmpty()) {
                vars[idx].removeVal(vars[idx].getSup(),VarEvent.domOverWDegIdx(cIndices[idx]));           
            }
        }
    }


    public boolean isSatisfied(int[] tuple) {
        LightState tmp = autom.getInitState();
        for (int i = 0; i < tuple.length; i++) {
            tmp = tmp.delta(tuple[i] - autom.getOffset(i));
            if (tmp == null)
                return false;
        }
        return autom.getLastState() == tmp;
    }

    public String pretty() {
        StringBuilder sb = new StringBuilder();
        sb.append("Regular({");
        for (int i = 0; i < vars.length; i++) {
            if (i > 0) sb.append(", ");
            IntDomainVar var = vars[i];
            sb.append(var.pretty());
        }
        sb.append("})");
        return sb.toString();
    }


    public String toString() {
        String autstring = "auto : ";
        for (int i = 0; i < vars.length; i++) {
            autstring += vars[i] + " ";
        }
        return autstring;
    }

    /*******************************************************/
    /*************** Propragation data structure ***********/
    /**
     * ***************************************************
     */

    class PropagationData {

        protected Solver solver;
        /**
         * in degre of the state (for incremental propagation of the automaton)
         */
        protected IStateInt[] indeg;

        /**
         * out degre of the state (for incremental propagation of the automaton)
         */
        protected IStateInt[] outdeg;

        protected int fstate;

        public PropagationData(AbstractSConstraint ct) {
            solver = ct.getSolver();
            initDegree(autom.getAutomateSize(), ct);
        }

        public void initDegree(int nbNode, AbstractSConstraint ct) {
            indeg = new IStateInt[nbNode];
            outdeg = new IStateInt[nbNode];
            fstate = nbNode - 1;
            for (int node = 0; node < nbNode; node++) {
                indeg[node] = (ct.getSolver()).getEnvironment().makeInt(0);
                outdeg[node] = (ct.getSolver()).getEnvironment().makeInt(0);
            }
        }

        public void resetPropagationData(int nbNode) {
            for (int node = 0; node < nbNode; node++) {
                indeg[node].set(0);
                outdeg[node].set(0);
            }
        }

        public boolean isAccurate(LightState st) {
            if (st.getIdx() == 0) return outdeg[st.getIdx()].get() > 0;
            if (st.getIdx() == fstate) return indeg[st.getIdx()].get() > 0;
            return (indeg[st.getIdx()].get() > 0) && (outdeg[st.getIdx()].get() > 0);
        }

        public int getIndeg(LightState st) {
            return indeg[st.getIdx()].get();
        }

        public void setIndeg(IStateInt indeg, LightState st) {
            this.indeg[st.getIdx()] = indeg;
        }

        public int getOutdeg(LightState st) {
            return outdeg[st.getIdx()].get();
        }

        public void setOutdeg(IStateInt outdeg, LightState st) {
            this.outdeg[st.getIdx()] = outdeg;
        }

        public void incrementIndeg(LightState st) {
            indeg[st.getIdx()].add(1);
        }

        public void decrementIndeg(LightState st) {
            indeg[st.getIdx()].add(-1);
        }

        public void incrementOutdeg(LightState st) {
            outdeg[st.getIdx()].add(1);
        }

        public void decrementOutdeg(LightState st) {
            outdeg[st.getIdx()].add(-1);
        }
    }
}
