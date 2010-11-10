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
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.global.automata.fast_multicostregular.valselector;



import choco.cp.solver.constraints.global.automata.fast_multicostregular.FastMultiCostRegular;
import choco.kernel.solver.search.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Nov 17, 2008
 * Time: 6:03:16 PM
 */
public class MCRValSelector implements ValSelector<IntDomainVar> {
    FastMultiCostRegular[] cons;
    boolean max;

    public MCRValSelector(FastMultiCostRegular[] cons, boolean max)
    {
        this.cons = cons;
        this.max = max;
    }


    public int getBestVal(IntDomainVar x)
    {
        for (FastMultiCostRegular con : cons) {
            int tmp = con.map.get(x);
            if (con.map.containsKey(x))
            {
                int j = max ? con.getGraph().GArcs.values[con.lastLp[tmp]] : con.getGraph().GArcs.values[con.lastSp[tmp]];
                if (x.canBeInstantiatedTo(j)) return j;
                else break;
            }

        }
            return max ? x.getSup() : x.getInf();

    }


}

