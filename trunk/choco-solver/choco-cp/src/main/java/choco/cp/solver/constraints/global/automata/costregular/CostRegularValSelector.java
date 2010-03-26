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
package choco.cp.solver.constraints.global.automata.costregular;

import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Dec 12, 2007
 * Since : Choco 2.0.0
 *
 */

public class CostRegularValSelector implements ValSelector {

    boolean max;
    CostRegular cr;
    public CostRegularValSelector(CostRegular cr,boolean max)
    {
        this.cr = cr;
        this.max = max;
    }

    public int getBestVal(IntDomainVar x) {
        int idx = x.getVarIndex(cr.getConstraintIdx(0));
        if (idx == cr.myVars.length)
            return x.getInf();
        else
        {
            CostRegular.State s = null;//cr.source;
            CostRegular.State next = cr.source;
            for (int i = 0 ; i <= idx ; i++)
            {
                s = next;
                next = cr.getLayer(i+1).get(max ? s.pgcSucc.get() : s.pccSucc.get());
            }
            int bestCost = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            CostRegular.Arc out = null;
            for (int k = 0; k < s.outArcs.size() ; k++)
            {
                CostRegular.Arc n = (CostRegular.Arc) s.outArcs.get(k);
                if (n.getArcOrigin().equals(s) && n.getArcDestination().equals(next))
                {
                    int cost = cr.getCost(idx,n.getArcLabel());
                    boolean b = (max ? (bestCost < cost) : (bestCost > cost));
                    if (b)
                    {
                        bestCost = cost;
                        out = n;
                    }
                }

            }
            return out.getArcLabel();

        }
    }
}
