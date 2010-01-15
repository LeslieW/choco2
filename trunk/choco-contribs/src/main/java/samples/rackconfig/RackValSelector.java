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
package samples.rackconfig;

import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.solver.search.AbstractSearchHeuristic;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import gnu.trove.TIntArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Nov 25, 2009
 * Time: 12:32:31 AM
 */
public class RackValSelector extends AbstractSearchHeuristic implements ValSelector {

    int[] ordered;

    public RackValSelector(int[] cost)
    {
        TIntArrayList arr = new TIntArrayList(cost);
        this.ordered = new int[cost.length];
        int k = 0 ;
        while(!arr.isEmpty())
        {
            int idx = arr.indexOf(arr.min());
            arr.remove(idx);
            ordered[k++] = idx;
        }
    }

    public int getBestVal(IntDomainVar x) {
        for (int i : ordered)
        {
            if (x.canBeInstantiatedTo(i))
                return i;
        }

        return x.getSup();
    }
}