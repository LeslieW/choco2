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

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.MathUtils;
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
 *
 * NVAR is the number of variables of the collection VARIABLES that take their value in SVAR.
 *
 * Propagator :
 * C. Bessière, E. Hebrard, B. Hnich, Z. Kiziltan, T. Walsh,
 * Among, common and disjoint Constraints
 * CP-2005
 */
public class AmongSet extends AbstractMixedSetIntSConstraint {


    private final int nb_vars;
    private final IntDomainVar[] ivars;
    private final SetVar s;
    private final IntDomainVar n;
    private final int idxS;
    private final int idxN;
    VarIdx[] bothK = new VarIdx[10];
    int kIdx = 0;
    VarIdx[] bothE = new VarIdx[10];
    int eIdx = 0;

    /**
     * Constructs a constraint with the specified priority.
     *
     * @param priority The wished priority.
     */
    @SuppressWarnings({"SuspiciousSystemArraycopy"})
    public AmongSet(Var[] vars) {
        super(vars);
        nb_vars = vars.length - 2;
        ivars = new IntDomainVar[nb_vars];
        System.arraycopy(vars, 0, ivars, 0, nb_vars);
        s = (SetVar) vars[nb_vars];
        idxS = nb_vars;
        n = (IntDomainVar) vars[nb_vars + 1];
        idxN = nb_vars + 1;
        init();
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
        int[] first_bounds = computeFirstBounds();
        int lb0 = first_bounds[0];
        int glb = first_bounds[1];
        int ub0 = first_bounds[2];
        int lub = first_bounds[3];

        int[][] bounds = computeBounds();

        updateN(glb, lb0, lub, ub0, bounds[0], bounds[1]);
        //if(n.getSup() < n.getInf())this.fail();
        updateSet(bounds[0], bounds[1], bounds[2]);
        if (n.isInstantiated()) {
            clear();
            int[] lb_ub = computeLastBounds();
            DisposableIntIterator it = null;
            if (lb_ub[0] == n.getInf()) {
                for (int i = 0; i < kIdx; i++) {
                    IntDomainVar v = bothK[i].var;
                    int idx = bothK[i].idx;
                    it = s.getDomain().getKernelIterator();
                    while (it.hasNext()) {
                        v.removeVal(it.next(), idx);
                    }
                    it.dispose();
                }
            }
            if (lb_ub[1] == n.getSup()) {
                for (int i = 0; i < eIdx; i++) {
                    IntDomainVar v = bothE[i].var;
                    int idx = bothE[i].idx;
                    it = v.getDomain().getIterator();
                    while (it.hasNext()) {
                        int val = it.next();
                        if (!s.isInDomainEnveloppe(val)) {
                            v.removeVal(val, idx);
                        }
                    }
                    it.dispose();
                }
            }
        }

    }

    private void updateN(int glb, int lb0, int lub, int ub0, int[] lbs, int[] ubs) throws ContradictionException {
        if (glb < n.getInf()) {
            int min = MathUtils.min(lbs);
            n.updateInf(min, cIndices[idxN]);
        } else {
            n.updateInf(lb0, cIndices[idxN]);
        }

        if (lub > n.getSup()) {
            int max = MathUtils.max(ubs);
            n.updateSup(max, cIndices[idxN]);
        } else {
            n.updateSup(ub0, cIndices[idxN]);
        }
    }

    private void updateSet(int[] lbs, int[] ubs, int[] values) throws ContradictionException {
        for (int i = 0; i < ubs.length; i++) {
            if (ubs[i] < n.getInf()) {
                s.addToKernel(values[i], cIndices[idxS]);
            }
        }
        for (int i = 0; i < lbs.length; i++) {
            if (lbs[i] > n.getSup()) {
                s.remFromEnveloppe(values[i], cIndices[idxS]);
            }
        }
    }

    /**
     * Compute lb[0], glb[0], ub[0] and lub[0].
     * 
     * @return
     */
    private int[] computeFirstBounds() {
        int lb = 0;
        int glb = nb_vars;
        int ub = nb_vars;
        int lub = 0;
        DisposableIntIterator it;
        for (int i = 0; i < nb_vars; i++) {
            IntDomainVar var = ivars[i];
            int nbK = 0;
            int nbE = 0;
            it = s.getDomain().getEnveloppeIterator();
            while (it.hasNext()) {
                int val = it.next();
                boolean contain = var.canBeInstantiatedTo(val);
                nbE += (contain ? 1 : 0);
                if (s.isInDomainKernel(val)) {
                    nbK += (contain ? 1 : 0);
                }
            }
            it.dispose();
            if (nbK == var.getDomainSize()) {
                lb++;
            } else if (nbK == 0) {
                glb--;
            }
            if (nbE == var.getDomainSize()) {
                lub++;
            } else if (nbE == 0) {
                ub--;
            }
        }
        return new int[]{lb, glb, ub, lub};
    }

    private int[] computeLastBounds() {
        int lb = 0;
        int ub = nb_vars;
        DisposableIntIterator it;
        for (int i = 0; i < nb_vars; i++) {
            IntDomainVar var = ivars[i];
            int nbK = 0;
            int nbE = 0;
            it = s.getDomain().getEnveloppeIterator();
            while (it.hasNext()) {
                int val = it.next();
                boolean contain = var.canBeInstantiatedTo(val);
                nbE += (contain ? 1 : 0);
                if (s.isInDomainKernel(val)) {
                    nbK += (contain ? 1 : 0);
                }
            }
            it.dispose();
            if (nbK == var.getDomainSize()) {
                lb++;
            } else {
                ensureCapacity(bothK, kIdx);
                bothK[kIdx++].set(var, cIndices[i]);
            }
            if (nbE == 0) {
                ub--;
            } else {
                ensureCapacity(bothE, eIdx);
                bothE[eIdx++].set(var, cIndices[i]);
            }
        }
        return new int[]{lb, ub};
    }

    //TODO : could be improved, it is not in O(nd) like told in the article
    private int[][] computeBounds() {
        int[][] lb_ub = new int[3][s.getEnveloppeDomainSize()];
        DisposableIntIterator it = s.getDomain().getEnveloppeIterator();
        int idx = 0;
        while (it.hasNext()) {
            int val = it.next();
            int lb = 0;
            int ub = nb_vars;
            if (!s.isInDomainKernel(val)) {
                for (int i = 0; i < nb_vars; i++) {
                    IntDomainVar var = ivars[i];
                    int nb = 0;
                    DisposableIntIterator itK = s.getDomain().getKernelIterator();
                    while (itK.hasNext()) {
                        nb += (var.canBeInstantiatedTo(itK.next()) ? 1 : 0);
                    }
                    itK.dispose();
                    if (nb == var.getDomainSize()
                            || (nb == (var.getDomainSize() - 1) && var.canBeInstantiatedTo(val))) {
                        lb++;
                    }
                    nb = 0;
                    DisposableIntIterator itV = var.getDomain().getIterator();
                    while(itV.hasNext()){
                        int vv= itV.next();
                        if(vv!=val){
                            nb+= (s.isInDomainEnveloppe(vv)?1:0);
                        }
                    }
                    if (nb == 0) {
                        ub--;
                    }
                }
            }
            lb_ub[0][idx] = lb;
            lb_ub[1][idx] = ub;
            lb_ub[2][idx] = val;
            idx++;
        }
        it.dispose();
        return lb_ub;
    }


    private void init(){
        for(int i = 0; i< bothE.length;i++){
            bothE[i] = new VarIdx();
        }
        for(int i = 0; i< bothK.length;i++){
            bothK[i] = new VarIdx();
        }
    }

    private void clear(){
        for (VarIdx aBothE : bothE) {
            aBothE.clear();
        }
        eIdx = 0;
        for (VarIdx aBothK : bothK) {
            aBothK.clear();
        }
        kIdx = 0;
    }

    private void ensureCapacity(VarIdx[] arr, int idx){
        if(idx>arr.length){
            VarIdx[] newArr = new VarIdx[arr.length * 2 /3 +1];
            System.arraycopy(arr,0, newArr, 0, idx);
            for(int i = idx; i < newArr.length; i++){
                newArr[i] = new VarIdx();
            }
            arr = newArr;
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
        if(isCompletelyInstantiated()){
            int nb = 0;
            for(IntDomainVar vars : ivars){
                int val = vars.getVal();
                if(s.isInDomainKernel(val)){
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
        for(int i = 0; i < nb_vars; i++){
            if(i>0)sb.append(",");
            sb.append(ivars[i].pretty());
        }
        sb.append("],").append(s.pretty()).append(",");
        sb.append(n.pretty()).append(")");
        return sb.toString();
    }

    private class VarIdx{
        IntDomainVar var;
        int idx;

        private void clear(){
            this.var = null;
            this.idx = -1;
        }

        private void set(IntDomainVar v, int idx){
            this.var = v;
            this.idx = idx;
        }
    }
}
