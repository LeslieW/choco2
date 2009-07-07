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

import choco.kernel.solver.search.AbstractSearchHeuristic;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.Solver;
import choco.kernel.model.variables.integer.IntegerVariable;
import samples.multicostregular.asap.hci.abstraction.ASAPDataHandler;

import java.util.HashMap;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 12, 2009
 * Time: 3:18:49 PM
 */
public class ASAPValSelector extends AbstractSearchHeuristic implements ValSelector {


    HashMap<IntDomainVar,IntDomainVar[]> map = new HashMap<IntDomainVar,IntDomainVar[]>();
    HashMap<IntDomainVar,Integer> mapIdx = new HashMap<IntDomainVar,Integer>();

    IntDomainVar[][] vars;
    ASAPDataHandler d;


    public ASAPValSelector(Solver s, IntegerVariable[][] vars,ASAPDataHandler d)
    {
        this.vars= new IntDomainVar[vars.length][];
        this.d = d;
        for (int i = 0 ; i < vars.length ; i++)
        {
            this.vars[i] = s.getVar(vars[i]);
            for (int j = 0; j < this.vars[i].length ; j++) {
                map.put(this.vars[i][j],this.vars[i]);
                mapIdx.put(this.vars[i][j],i);
            }
        }


    }

    public int getBestVal(IntDomainVar x) {
        IntDomainVar[] col = map.get(x);
        int idx = mapIdx.get(x);
        int val = neededValue(x,idx,col);

        return val;
    }

    private int neededValue(IntDomainVar x, int idx,IntDomainVar[] col) {
        int lowb[] = Arrays.copyOf(d.getCPModel().lowb[idx],d.getCPModel().lowb[idx].length);
        int uppb[] = Arrays.copyOf(d.getCPModel().uppb[idx],d.getCPModel().uppb[idx].length);
        int[] occur = new int[lowb.length];
        for (int i = 0 ; i < col.length ;i++)
        {
            if (col[i].isInstantiated())
                occur[col[i].getVal()]++; 
        }

        for (int i = 0 ; i < lowb.length ; i++)
        {
            lowb[i]-= occur[i];
            uppb[i]-= occur[i];
        }
        if (max(x,lowb)[0] <= 0)
        {
            max(x,uppb);
        }

        return maxs[1];

        

    }

    static int[] maxs = new int[2];
    static int[] max(IntDomainVar x,int[] tmp)
    {
        maxs[0] = Integer.MIN_VALUE;
        for (int i = 0; i < tmp.length ;i++)
        {
            int a = tmp[i];
            if ( a > maxs[0] && x.canBeInstantiatedTo(i))
            {
                maxs[0] = a;
                maxs[1] = i;
            }
        }

        return maxs;
    }
}