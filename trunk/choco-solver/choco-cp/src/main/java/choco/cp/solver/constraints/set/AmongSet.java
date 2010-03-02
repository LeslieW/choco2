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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.set;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBitSet;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.set.AbstractMixedSetIntSConstraint;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;


/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 23 févr. 2010
 * Since : Choco 2.1.1
 * <p/>
 * NVAR is the number of variables of the collection VARIABLES that take their value in SVAR.
 * <p/>
 * Propagator :
 * C. Bessière, E. Hebrard, B. Hnich, Z. Kiziltan, T. Walsh,
 * Among, common and disjoint Constraints
 * CP-2005
 */
public final class AmongSet extends AbstractMixedSetIntSConstraint {


    private final int nb_vars;
    private final IntDomainVar[] ivars;
    private final SetVar s;
    private final IntDomainVar n;
    private final int idxS;
    private final int idxN;

    int[] bothK = new int[10];
    int kIdx = 0;
    int[] bothE = new int[10];
    int eIdx = 0;

    // INTERNAL STRUCTURES

    int envInf;
    int[][] lb_ub;
    int min, max;
    // internal structure to get values out of kernel but in enveloppe
    IStateBitSet outKinE;

    //internal structures to compute bounds
    IStateBitSet inKer; // lb[0]
    IStateBitSet inEnv; // lub[0]
    IStateBitSet outKer; // glb[0]
    IStateBitSet outEnv; // ub[0]
    IStateBitSet varToUpdate;

    private final IEnvironment environment;

    /**
     * Constructs a constraint with the specified priority.
     *
     * @param priority The wished priority.
     */
    @SuppressWarnings({"SuspiciousSystemArraycopy"})
    public AmongSet(Var[] vars, IEnvironment environment) {
        super(vars);
        nb_vars = vars.length - 2;
        ivars = new IntDomainVar[nb_vars];
        System.arraycopy(vars, 0, ivars, 0, nb_vars);
        s = (SetVar) vars[nb_vars];
        idxS = nb_vars;
        n = (IntDomainVar) vars[nb_vars + 1];
        idxN = nb_vars + 1;
        this.environment = environment;
    }

    @Override
    public int getFilteredEventMask(int idx) {
        return IntVarEvent.REMVALbitvector;
    }

    private void init() {
        int envSize = s.getEnveloppeDomainSize();
        envInf = s.getEnveloppeInf();
        outKinE = environment.makeBitSet(envSize);
        inKer = environment.makeBitSet(nb_vars);
        outKer = environment.makeBitSet(nb_vars);
        inEnv = environment.makeBitSet(nb_vars);
        outEnv = environment.makeBitSet(nb_vars);
        varToUpdate = environment.makeBitSet(nb_vars);
        lb_ub = new int[3][envSize];
        // init outKinE
        DisposableIntIterator it = s.getDomain().getEnveloppeIterator();
        while (it.hasNext()) {
            int val = it.next();
            outKinE.set(val - envInf, !s.isInDomainKernel(val));
        }

        // init lb[0], glb[0], ub[0], lub[0]
        computeForAllVar();

    }

    private void computeForAllVar() {
        for (int i = 0; i < nb_vars; i++) {
            computeForVar(i);
        }
    }

    private void computeForVar(int i) {
        IntDomainVar var = ivars[i];
        int dSize = var.getDomainSize();
        int nbK = 0;
        int nbE = 0;
        DisposableIntIterator it = s.getDomain().getEnveloppeIterator();
        while (it.hasNext()) {
            int val = it.next();
            boolean contain = var.canBeInstantiatedTo(val);
            nbE += (contain ? 1 : 0);
            if (s.isInDomainKernel(val)) {
                nbK += (contain ? 1 : 0);
            }
        }
        it.dispose();
        if (nbK == dSize) {
            inKer.set(i, true);
            outKer.set(i, false);
        } else if (nbK == 0) {
            inKer.set(i, false);
            outKer.set(i, true);
        } else {
            inKer.set(i, false);
            outKer.set(i, false);
        }
        if (nbE == dSize) {
            inEnv.set(i, true);
            outEnv.set(i, false);
        } else if (nbE == 0) {
            inEnv.set(i, false);
            outEnv.set(i, true);
        } else {
            inEnv.set(i, false);
            outEnv.set(i, false);
        }
        varToUpdate.set(i, false);
    }

    /**
     * <i>Propagation:</i>
     * Propagating the constraint for the very first time until local
     * consistency is reached.
     *
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public void awake() throws ContradictionException {
        init();
        propagate();
    }

    /**
     * <i>Propagation:</i>
     * Propagating the constraint until local consistency is reached.
     *
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */
    @Override
    public void propagate() throws ContradictionException {
        // update only for modified var...
        for (int v = varToUpdate.nextSetBit(0); v >= 0; v = varToUpdate.nextSetBit(v + 1)) {
            computeForVar(v);
        }

        computeBounds();

        updateN();
        updateSet();

        filter();
    }

    private void filter() throws ContradictionException {
        if (n.isInstantiated()) {
            DisposableIntIterator it = null;
            int[] lb_ub = computeLastBounds();

            if (lb_ub[0] == n.getInf()) {
                for (int j = 0; j < kIdx; j++) {
                    int i = bothK[j];
                    IntDomainVar v = ivars[i];
                    it = s.getDomain().getKernelIterator();
                    while (it.hasNext()) {
                        v.removeVal(it.next(), -1);
                    }
                    it.dispose();
                }
            }
            if (lb_ub[1] == n.getSup()) {
                for (int j = 0; j < eIdx; j++) {
                    int i = bothE[j];
                    IntDomainVar v = ivars[i];
                    it = v.getDomain().getIterator();
                    while (it.hasNext()) {
                        int val = it.next();
                        if (!s.isInDomainEnveloppe(val)) {
                            v.removeVal(val, -1);
                        }
                    }
                    it.dispose();
                }
            }
        }

    }

    private void updateN() throws ContradictionException {
        int lb0 = inKer.cardinality();
        int glb0 = nb_vars - outKer.cardinality();
        int ub0 = nb_vars - outEnv.cardinality();
        int lub0 = inEnv.cardinality();

        if (glb0 < n.getInf()) {
            if(min < Integer.MAX_VALUE){
                n.updateInf(min, cIndices[idxN]);
            }
        } else {
            n.updateInf(lb0, cIndices[idxN]);
        }

        if (lub0 > n.getSup()) {
            if(max > Integer.MIN_VALUE){
                n.updateSup(max, cIndices[idxN]);
            }
        } else {
            n.updateSup(ub0, cIndices[idxN]);
        }
    }

    private void updateSet() throws ContradictionException {
        int idx = 0;
        boolean mod = false;
        for (int e = outKinE.nextSetBit(0); e >= 0; e = outKinE.nextSetBit(e + 1)) {
            if (lb_ub[1][idx] < n.getInf()) {
                mod |= s.addToKernel(lb_ub[2][idx], cIndices[idxS]);
                outKinE.set(e, false);
            }
            if (lb_ub[0][idx] > n.getSup()) {
                mod |= s.remFromEnveloppe(lb_ub[2][idx], cIndices[idxS]);
                outKinE.set(e, false);
            }
            idx++;
        }
        if (mod) computeForAllVar();
    }

    private int[] computeLastBounds() {
        kIdx = 0;
        eIdx = 0;
        int lb = nb_vars;
        int ub = 0;
        for (int i = 0; i < nb_vars; i++) {
            IntDomainVar var = ivars[i];
            if (!inKer.get(i)) {
                lb += updateLB(var, i);
            }
            if (!outEnv.get(i)) {
                ub += updateUB(var, i);
            }
        }
        return new int[]{lb, ub};
    }

    private int updateLB(final IntDomainVar var, final int i) {
        if (var.getDomainSize() <= s.getKernelDomainSize()) {
            final DisposableIntIterator it = var.getDomain().getIterator();
            while (it.hasNext()) {
                int val = it.next();
                if (!s.isInDomainKernel(val)) {
                    ensureCapacity(bothK, kIdx);
                    bothK[kIdx++] = i;
                    it.dispose();
                    return -1;
                }
            }
            it.dispose();
            return 0;
        }
        ensureCapacity(bothK, kIdx);
        bothK[kIdx++] = i;
        return -1;
    }

    private int updateUB(IntDomainVar var, int i) {
        DisposableIntIterator it = s.getDomain().getEnveloppeIterator();
        while (it.hasNext()) {
            int val = it.next();
            if (var.canBeInstantiatedTo(val)) {
                ensureCapacity(bothE, eIdx);
                bothE[eIdx++] = i;
                it.dispose();
                return 1;
            }
        }
        it.dispose();
        return 0;
    }

    //TODO : could be improved, it is not in O(nd) like told in the article
    private void computeBounds() {
        int idx = 0;
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
        for (int e = outKinE.nextSetBit(0); e >= 0; e = outKinE.nextSetBit(e + 1)) {
            int val = e + envInf;
            int lb = inKer.cardinality();
            int ub = nb_vars - outEnv.cardinality();
            for (int i = 0; i < nb_vars; i++) {
                int nb = 0;
                IntDomainVar var = ivars[i];
                if(!inKer.get(i)){
                    if(var.canBeInstantiatedTo(val)){
                        DisposableIntIterator it = s.getDomain().getKernelIterator();
                        while (it.hasNext()) {
                            if(var.canBeInstantiatedTo(it.next())){
                                nb++;
                            }
                        }
                        it.dispose();
                        if (nb == var.getDomainSize()-1) {
                            lb++;
                        }
                    }
                }
                if(!outEnv.get(i)){
                    nb = 0;
                    DisposableIntIterator it = var.getDomain().getIterator();
                    while(it.hasNext()){
                        int vv= it.next();
                        if(vv!=val){
                            if(s.isInDomainEnveloppe(vv)){
                                nb++;
                                break;
                            }
                        }
                    }
                    it.dispose();
                    if (nb == 0) {
                        ub--;
                    }
                }
            }
            lb_ub[0][idx] = lb;
            min = Math.min(min, lb);
            lb_ub[1][idx] = ub;
            max = Math.max(max, ub);
            lb_ub[2][idx] = val;
            idx++;
        }
    }


    private void ensureCapacity(int[] arr, int idx) {
        if (idx > arr.length) {
            int[] newArr = new int[arr.length * 2 / 3 + 1];
            System.arraycopy(arr, 0, newArr, 0, idx);
            arr = newArr;
        }
    }

    @Override
    public void awakeOnKer(int varIdx, int x) throws ContradictionException {
        outKinE.set(x - envInf, false);
        computeForAllVar();
        this.constAwake(false);
    }

    @Override
    public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
        outKinE.set(x - envInf, false);
        computeForAllVar();
        this.constAwake(false);
    }

    @Override
    public void awakeOnInst(int varIdx) throws ContradictionException {
        if (varIdx == idxS) {
            outKinE.clear();
            computeForAllVar();
        }
        this.constAwake(false);
    }

    /**
     * Default propagation on one value removal: propagation on domain revision.
     */
    @Override
    public void awakeOnRem(int varIdx, int val) throws ContradictionException {
        if (varIdx < nb_vars) {
            varToUpdate.set(varIdx, true);
            this.constAwake(false);
        } else {
            this.constAwake(false);
        }
    }

    /**
     * <i>Semantic:</i>
     * Testing if the constraint is satisfied.
     * Note that all variables involved in the constraint must be
     * instantiated when this method is called.
     *
     * @return true if the constraint is satisfied
     */
    @Override
    public boolean isSatisfied() {
        if (isCompletelyInstantiated()) {
            int nb = 0;
            for (IntDomainVar vars : ivars) {
                int val = vars.getVal();
                if (s.isInDomainKernel(val)) {
                    nb++;
                }
            }
            return nb == n.getVal();
        }
        return false;
    }

    @Override
    public String pretty() {
        StringBuffer sb = new StringBuffer("AMONG(");
        sb.append("[");
        for (int i = 0; i < nb_vars; i++) {
            if (i > 0) sb.append(",");
            sb.append(ivars[i].pretty());
        }
        sb.append("],").append(s.pretty()).append(",");
        sb.append(n.pretty()).append(")");
        return sb.toString();
    }

    private class VarIdx {
        IntDomainVar var;
        int idx;

        private void clear() {
            this.var = null;
            this.idx = -1;
        }

        private void set(IntDomainVar v, int idx) {
            this.var = v;
            this.idx = idx;
        }
    }
}
