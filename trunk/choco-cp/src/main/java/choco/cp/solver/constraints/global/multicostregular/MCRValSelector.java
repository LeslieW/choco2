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
package choco.cp.solver.constraints.global.multicostregular;

import choco.kernel.solver.search.AbstractSearchHeuristic;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.Solver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.cp.solver.CPSolver;
import choco.cp.model.CPModel;



import static choco.Choco.*;
import gnu.trove.TObjectIntHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Nov 17, 2008
 * Time: 6:03:16 PM
 */
public class MCRValSelector extends AbstractSearchHeuristic implements ValSelector {
    MultiCostRegular[] cons;
    boolean max;

    public MCRValSelector(MultiCostRegular[] cons, boolean max)
    {
        this.cons = cons;
        this.max = max;
    }


    public int getBestVal(IntDomainVar x)
    {
        for (MultiCostRegular con : cons) {
            int tmp = con.map.get(x);
            if (con.map.containsKey(x))
            {
                int j = max ? con.lastLp[tmp].getLabel() : con.lastSp[tmp].getLabel();
                if (x.canBeInstantiatedTo(j)) return j;
                else break;
            }

        }
            return max ? x.getSup() : x.getInf();

    }


}

