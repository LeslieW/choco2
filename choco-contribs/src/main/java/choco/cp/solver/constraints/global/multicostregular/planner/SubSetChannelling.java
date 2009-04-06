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
package choco.cp.solver.constraints.global.multicostregular.planner;

import choco.cp.model.managers.IntConstraintManager;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jan 19, 2009
 * Time: 12:55:34 PM
 */
public class SubSetChannelling extends AbstractLargeIntSConstraint {

    int n;

    public SubSetChannelling(IntDomainVar[] vars) {
        super(vars);
        this.n = vars.length/2;


    }

    private void eq(int idx1, int idx2) throws ContradictionException {
        IntDomainVar v0 = vars[idx1];
        IntDomainVar v1 = vars[idx2];
        int sup = Math.min(v0.getSup(),v1.getSup());
        int inf = Math.max(v0.getInf(),v1.getInf());
        if (v0.getSup() > sup) v0.updateSup(sup,cIndices[idx1]);
        else if (v1.getSup() > sup) v1.updateSup(sup,cIndices[idx2]);
        if (v0.getInf() < inf) v0.updateInf(inf,cIndices[idx1]);
        else if (v1.getInf() < inf) v1.updateInf(inf,cIndices[idx2]);

        for (int i = inf+1 ; i < sup ; i++)
        {
            if (v0.canBeInstantiatedTo(i) && !v1.canBeInstantiatedTo(i)) v0.removeVal(i,cIndices[idx1]);
            else if (v1.canBeInstantiatedTo(i) && !v0.canBeInstantiatedTo(i)) v1.removeVal(i,cIndices[idx1]);
        }


    }

    public void awakeOnRem(int idx, int val) throws ContradictionException {
        if (idx < n)
        {

            if (val < 3) vars[idx+n].removeVal(val,cIndices[idx+n]);

        }
        else
        {
            if (val < 3) vars[idx-n].removeVal(val,cIndices[idx-n]);
            else vars[idx-n].updateSup(2,cIndices[idx-n]);
        }

    }


    public void awakeOnInst(int idx) throws ContradictionException {

        //this.constAwake(false);
        if (idx <n) {
            if (vars[idx].getVal() < 3) vars[idx+n].instantiate(vars[idx].getVal(),cIndices[idx+n]);
            else vars[idx+n].instantiate(3,cIndices[idx+n]);
        }
        else
        {
            if (vars[idx].getVal() < 3) vars[idx-n].instantiate(vars[idx].getVal(),cIndices[idx-n]);
        }

    }

    public void awakeOnInf(int idx) throws ContradictionException {
        if (idx < n)
        {
            if (vars[idx].getInf() >= 3) vars[idx+n].instantiate(3,cIndices[idx+n]);
            else vars[idx+n].updateInf(vars[idx].getInf(),cIndices[idx+n]);
        }
        else
        {
            if (vars[idx-n].getInf() < vars[idx].getInf()) vars[idx-n].updateInf(vars[idx].getInf(),cIndices[idx-n]);
        }
    }

    public void awakeOnSup(int idx) throws ContradictionException {
        if (idx < n)
        {
            if (vars[idx].getSup() < 3) eq(idx,idx+n);
        }
        else
        {
            if (vars[idx].getSup() < 3 && vars[idx].getSup() < vars[idx-n].getSup()) eq(idx-n,idx);
        }
    }



    public void awake() throws ContradictionException {
        for (int i = 0 ; i < n; i++)
        {
            IntDomainVar v0 = vars[i];
            IntDomainVar v1 = vars[i+n];
            if (v0.getSup() < 3)
                eq(i,i+n);
            else if (v0.getInf() >= 3)
                v1.instantiate(3,i+n);
            if (v1.getSup() < 3)
                eq(i,i+n);
            if (v1.getInf() > v0.getInf()) v0.updateInf(v1.getInf(),i);
        }

    }

    public void propagate() throws ContradictionException {
             awake();


    }


    public static class SubSetManager extends IntConstraintManager
    {

        public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {
            if (parameters == null)
            {
                IntDomainVar[] vs = solver.getVar((IntegerVariable[]) variables);
                return new SubSetChannelling(vs);

            }
            return null;
        }
    }




}