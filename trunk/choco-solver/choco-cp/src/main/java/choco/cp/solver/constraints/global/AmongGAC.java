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

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.objects.IntPair;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import gnu.trove.TIntArrayList;

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
 * {@link http://www.emn.fr/x-info/sdemasse/gccat/Camong.html}
 *
 * Propagator :
 * C. Bessière, E. Hebrard, B. Hnich, Z. Kiziltan, T. Walsh,
 * Among, common and disjoint Constraints
 * CP-2005
 */
public class AmongGAC extends AbstractLargeIntSConstraint {


    private final TIntArrayList values;
    private final int nb_vars;
    private final List<IntPair<IntDomainVar>> both = new ArrayList<IntPair<IntDomainVar>>();

    /**
     * Constructs a constraint with the specified priority.
     *
     * The last variables of {@code vars} is the counter.
     * @param vars (n-1) variables + N as counter
     * @param values counted values
     */
    public AmongGAC(IntDomainVar[] vars, int[] values) {
        super(vars);
        nb_vars = vars.length - 1;
        this.values = new TIntArrayList(values);
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
        both.clear();
        int lb = 0;
        int ub = nb_vars;
        for(int i = 0 ; i < nb_vars; i++){
            IntDomainVar var = vars[i];
            int nb = 0;
            for (int value : values.toNativeArray()) {
                nb += (var.canBeInstantiatedTo(value)?1:0);
            }
            if(nb == var.getDomainSize()){
                lb++;
            }else if(nb == 0){

                ub--;
            }else if(nb > 0){
                both.add(new IntPair<IntDomainVar>(var,cIndices[i]));
            }
        }

        vars[nb_vars].updateInf(lb, cIndices[nb_vars]);
        vars[nb_vars].updateSup(ub, cIndices[nb_vars]);

        int min = Math.max(vars[nb_vars].getInf(), lb);
        int max = Math.min(vars[nb_vars].getSup(), ub);

        if(max < min) this.fail();

        if(lb == min && lb == max){
            for(IntPair<IntDomainVar> pair : both){
                removeOnlyValues(pair.o, pair.idx);
            }
            setEntailed();
        }

        if(ub == min && ub == max){
            for(IntPair<IntDomainVar> pair : both){
                removeButValues(pair.o, pair.idx);
            }
            setEntailed();
        }
    }

    /**
     * Remove from {@code v} every values contained in {@code values}.
     * @param v variable
     * @param cidx index of {@code v} in the constraint
     * @throws ContradictionException if contradiction occurs.
     */
    private void removeOnlyValues(IntDomainVar v, int cidx) throws ContradictionException {
        for(int i = 0; i < values.size(); i++){
            v.removeVal(values.get(i), cidx);
        }
    }

    /**
     * Remove from {@code v} each value but {@code values}.
     * @param v variable
     * @param cidx index of {@code v} in the constraint
     * @throws ContradictionException if contradiction occurs.
     */
    private void removeButValues(IntDomainVar v, int cidx) throws ContradictionException {
        DisposableIntIterator it = v.getDomain().getIterator();
        while(it.hasNext()){
            int val = it.next();
            if(!values.contains(val)){
                v.removeVal(val, cidx);
            }
        }
        it.dispose();
    }

    @Override
    public String pretty() {
        StringBuffer sb = new StringBuffer("AMONG(");
        sb.append("[");
        for(int i = 0; i < nb_vars; i++){
            if(i>0)sb.append(",");
            sb.append(vars[i].pretty());
        }
        sb.append("],{");
        StringUtils.pretty(values.toNativeArray());
        sb.append("},");
        sb.append(vars[nb_vars].pretty()).append(")");
        return sb.toString();
    }

    /**
     * Default implementation of the isSatisfied by
     * delegating to the isSatisfied(int[] tuple)
     *
     * @return
     */
    @Override
    public boolean isSatisfied() {
        if(isCompletelyInstantiated()){
            int nb = 0;
            for(int i = 0; i< nb_vars; i++){
                if(values.contains(vars[i].getVal())){
                    nb++;
                }
            }
            return vars[nb_vars].getVal() == nb;
        }
        return false;
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
        for(int i = 0; i< nb_vars; i++){
            if(values.contains(tuple[i])){
                nb++;
            }
        }
        return tuple[nb_vars] == nb;
    }
}
