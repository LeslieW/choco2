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
package samples.multicostregular.asap.heuristics;

import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.integer.AbstractIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 12, 2009
 * Time: 2:46:42 PM
 */
public class ASAPVarSelector extends AbstractIntVarSelector {

    IntDomainVar[][] vars;
    AbstractIntVarSelector[] varselec;

    public ASAPVarSelector(IntDomainVar[][] vars, Solver solver)
    {
        this.vars=  vars;
        this.varselec = new AbstractIntVarSelector[vars.length];
        for (int i = 0; i < this.varselec.length ;i++)
        {
            varselec[i] = new MinDomain(solver,vars[i]);
        }

    }
    public ASAPVarSelector(Solver s,IntegerVariable[][] vars)
    {

        this.vars = new IntDomainVar[vars.length][];
        for (int i = 0 ; i < vars.length ; i++)
        {
            this.vars[i] = s.getVar(vars[i]);
        }
        this.varselec = new AbstractIntVarSelector[vars.length];
        for (int i = 0; i < this.varselec.length ;i++)
        {
            varselec[i] = new MinDomain(s,this.vars[i]);
        }

    }

    public int getNbInstanciated(int i)
    {
        int out = 0;
        for (IntDomainVar v : vars[i])
        {
            if (v.isInstantiated()) out++;
        }
        return out;

    }

    public IntDomainVar selectIntVar() throws ContradictionException {
        int idx =0;
        int num = -1;
        int n = vars[0].length;
        for (int i = 0 ; i < vars.length ;i++)
        {
            int nb = this.getNbInstanciated(i);
            if (nb <n && nb > num)
            {
                idx = i;
                num = nb;
            }
        }


        return varselec[idx].selectIntVar();
    }


}