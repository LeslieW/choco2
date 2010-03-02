/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  �(..)  |                           *
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

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateBitSet;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import gnu.trove.TIntArrayList;

/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 22 f�vr. 2010
 * Since : Choco 2.1.1
 *
 * GCCAT:
 * NVAR is the number of variables of the collection VARIABLES that take their value in VALUES.
 * {@link http://www.emn.fr/x-info/sdemasse/gccat/Camong.html}
 *
 * Propagator :
 * C. Bessi�re, E. Hebrard, B. Hnich, Z. Kiziltan, T. Walsh,
 * Among, common and disjoint Constraints
 * CP-2005
 */
public class AmongGAC extends AbstractLargeIntSConstraint {


    private final TIntArrayList valuesAsList;
    private final int[] values;
    private final int nb_vars;
    private final IStateBitSet both;
    private final IStateInt LB;
    private final IStateInt UB;

    /**
     * Constructs a constraint with the specified priority.
     *
     * The last variables of {@code vars} is the counter.
     * @param vars (n-1) variables + N as counter
     * @param values counted values
     * @param environment
     */
    public AmongGAC(IntDomainVar[] vars, int[] values, IEnvironment environment) {
        super(vars);
        nb_vars = vars.length - 1;
        this.values = values;
        this.valuesAsList = new TIntArrayList(values);
        both = environment.makeBitSet(nb_vars);
        LB = environment.makeInt(0);
        UB = environment.makeInt(0);
    }

    @Override
    public int getFilteredEventMask(int idx) {
        if(idx == nb_vars){
            return IntVarEvent.INSTINTbitvector + IntVarEvent.INCINFbitvector + IntVarEvent.DECSUPbitvector;
        }
        return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
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
        int lb = 0;
        int ub = nb_vars;
        for(int i = 0 ; i < nb_vars; i++){
            IntDomainVar var = vars[i];
            int nb = 0;
            for (int value : values) {
                nb += (var.canBeInstantiatedTo(value)?1:0);
            }
            if(nb == var.getDomainSize()){
                lb++;
            }else if(nb == 0){

                ub--;
            }else if(nb > 0){
                both.set(i, true);
            }
        }
        LB.set(lb);
        UB.set(ub);

        filter();
    }

    private void filter() throws ContradictionException {
        int lb = LB.get();
        int ub = UB.get();
        vars[nb_vars].updateInf(lb, cIndices[nb_vars]);
        vars[nb_vars].updateSup(ub, cIndices[nb_vars]);

        int min = Math.max(vars[nb_vars].getInf(), lb);
        int max = Math.min(vars[nb_vars].getSup(), ub);

        if(max < min) this.fail();

        if(lb == min && lb == max){
            removeOnlyValues();
            setEntailed();
        }

        if(ub == min && ub == max){
            removeButValues();
            setEntailed();
        }
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
        filter();
    }


    /**
     * Default propagation on instantiation: full constraint re-propagation.
     */
    @Override
    public void awakeOnInst(int idx) throws ContradictionException {
        if(idx ==  nb_vars){
            filter();
        }else{
            if(both.get(idx)){
                IntDomainVar var = vars[idx];
                int val = var.getVal();
                if(valuesAsList.contains(val)){
                    LB.add(1);
                    both.set(idx, false);
                    filter();
                }else{
                    UB.add(-1);
                    both.set(idx, false);
                    filter();
                }
            }
        }
    }

    /**
     * Default propagation on one value removal: propagation on domain revision.
     */
    @Override
    public void awakeOnRem(int varIdx, int val) throws ContradictionException {
        if(varIdx< nb_vars){
            if(both.get(varIdx)){
                IntDomainVar var = vars[varIdx];
                int nb = 0;
                for (int value : values) {
                    if(var.canBeInstantiatedTo(value)){
                        nb++;
                    }
                }
                if(nb == var.getDomainSize()){
                    LB.add(1);
                    both.set(varIdx, false);
                    filter();
                }else if(nb == 0){
                    UB.add(-1);
                    both.set(varIdx, false);
                    filter();
                }
            }
        }
    }

    /**
     * Default propagation on improved lower bound: propagation on domain revision.
     */
    @Override
    public void awakeOnInf(int varIdx) throws ContradictionException {
        if(varIdx ==  nb_vars){
            filter();
        }
    }

    /**
     * Default propagation on improved upper bound: propagation on domain revision.
     */
    @Override
    public void awakeOnSup(int varIdx) throws ContradictionException {
        if(varIdx ==  nb_vars){
            filter();
        }
    }

    /**
     * Remove from {@code v} every values contained in {@code values}.
     * @param v variable
     * @param cidx index of {@code v} in the constraint
     * @throws ContradictionException if contradiction occurs.
     */
    private void removeOnlyValues() throws ContradictionException {
        for(int i = both.nextSetBit(0); i >=0; i = both.nextSetBit(i+1)){
            IntDomainVar v = vars[i];
            for(int value : values){
                v.removeVal(value, cIndices[i]);
            }
        }
    }

    /**
     * Remove from {@code v} each value but {@code values}.
     * @param v variable
     * @param cidx index of {@code v} in the constraint
     * @throws ContradictionException if contradiction occurs.
     */
    private void removeButValues() throws ContradictionException {
        for(int i = both.nextSetBit(0); i >=0; i = both.nextSetBit(i+1)){
            IntDomainVar v = vars[i];
            DisposableIntIterator it = v.getDomain().getIterator();
            while(it.hasNext()){
                int val = it.next();
                if(!valuesAsList.contains(val)){
                    v.removeVal(val, cIndices[i]);
                }
            }
            it.dispose();
        }
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
        StringUtils.pretty(values);
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
                if(valuesAsList.contains(vars[i].getVal())){
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
            if(valuesAsList.contains(tuple[i])){
                nb++;
            }
        }
        return tuple[nb_vars] == nb;
    }
}