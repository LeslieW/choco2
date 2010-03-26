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
package choco.cp.solver.constraints.global;

import choco.kernel.common.util.objects.Pair;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.List;

/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 22 févr. 2010
 * Since : Choco 2.1.1
 *
 * GCCAT:
 * NVAR is the number of variables of the collection VARIABLES that take their value in VALUES.
 * {@link http://www.emn.fr/x-info/sdemasse/gccat/Cexactly.html}
 *
 * Propagator :
 * C. Bessière, E. Hebrard, B. Hnich, Z. K?z?ltan, T. Walsh,
 * Among, common and disjoint Constraints
 * CP-2005
 *
 * Could be improved by defining awakes on XX... but required storable data structures and clever management of LB and UB
 * during INST + INF + SUP + REM.
 * So not sure it will be that interesting.
 */
public final class Exactly extends AbstractLargeIntSConstraint {


    private final int value;
    private final int nb_vars;
    private final int N;
    private final List<Pair<IntDomainVar, Integer>> BOTH = new ArrayList<Pair<IntDomainVar, Integer>>();

    /**
     * Constructs a constraint with the specified priority.
     *
     * The last variables of {@code vars} is the counter.
     * @param vars (n-1) variables + N as counter
     * @param N counter
     * @param value counted values
     */
    @SuppressWarnings({"unchecked"})
    public Exactly(IntDomainVar[] vars, int N, int value) {
        super(vars);
        nb_vars = vars.length;
        this.value = value;
        this.N = N;
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
        BOTH.clear();
        int lb = 0;
        int ub = nb_vars;
        for(int i = 0 ; i < nb_vars; i++){
            IntDomainVar var = vars[i];
            if(var.canBeInstantiatedTo(value)){
                if(var.isInstantiatedTo(value)){
                    lb++;
                }else{
                    BOTH.add(new Pair<IntDomainVar, Integer>(var,cIndices[i]));
                }
            }else{
                ub--;
            }
        }
        int min = Math.max(N, lb);
        int max = Math.min(N, ub);

        if(max < min) this.fail();

        if(lb == min && lb == max){
            for(Pair<IntDomainVar, Integer> pair : BOTH){
                removeOnlyValues(pair.fst, pair.snd);
            }
            setEntailed();
        }

        if(ub == min && ub == max){
            for(Pair<IntDomainVar, Integer> pair : BOTH){
                removeButValues(pair.fst, pair.snd);
            }
            setEntailed();
        }
    }



    /**
     * Remove from {@code v} every values contained in {@code values}.
     * @param v variable
     * @param cidx index of {@code v} in the constraint
     * @throws choco.kernel.solver.ContradictionException if contradiction occurs.
     */
    private void removeOnlyValues(IntDomainVar v, int cidx) throws ContradictionException {
        v.removeVal(value, this, false);
    }

    /**
     * Remove from {@code v} each value but {@code values}.
     * @param v variable
     * @param cidx index of {@code v} in the constraint
     * @throws choco.kernel.solver.ContradictionException if contradiction occurs.
     */
    private void removeButValues(IntDomainVar v, int cidx) throws ContradictionException {
        v.instantiate(value, this, false);
    }

    /**
     * Default implementation of the isSatisfied by
     * delegating to the isSatisfied(int[] tuple)
     *
     * @return
     */
    @Override
    public boolean isSatisfied() {
        int nbToValue  = 0;
        int nbNotToValue = 0;

        for(int i = 0; i < nb_vars; i++){
            if(vars[i].isInstantiatedTo(value)){
                nbToValue++;
            }else if(!vars[i].canBeInstantiatedTo(value)){
                nbNotToValue++;
            }
        }
        return nbToValue == N && (nbToValue + nbNotToValue) == nb_vars;
    }

    /**
     * TEMPORARY: if not overriden by the constraint, throws an error
     * to avoid bug using reified constraints in constraints
     * that have not been changed to fulfill this api yet !
     *
     * @param tuple
     * @return
     */
    @Override
    public boolean isSatisfied(int[] tuple) {
        int nb = 0;
        for(int tu : tuple){
            if(tu == value){
                nb++;
            }
        }
        return nb == N;
    }

    @Override
    public String pretty() {
        StringBuffer sb = new StringBuffer("EXACTLY(");
        sb.append(N).append(",[");
        for(int i = 0; i < nb_vars; i++){
            if(i>0)sb.append(",");
            sb.append(vars[i].pretty());
        }
        sb.append("],").append(value).append(")");
        return sb.toString();
    }
}