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
package samples.seminar.tsp;

import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;


public class MyValSelector implements ValSelector {

    protected IntDomainVar objective;
    protected IntDomainVar[] vars;
    protected int[][] matrix;
    protected int src;
    protected int dest;

    protected boolean optimize;

    public MyValSelector(IntDomainVar objective, IntDomainVar[] vars, int[][] matrix, int src, int dest) {
        this.objective = objective;
        this.vars = vars;
        this.matrix = matrix;
        this.src = src;
        this.dest = dest;
        this.optimize = false;
        int idx = 0;
        while(idx < vars.length && !this.optimize) {
            if(vars[idx].equals(objective)) this.optimize = true;
            idx++;
        }
    }

    public int getBestVal(IntDomainVar v) {
        int n;
        if (optimize) n = vars.length-1;
        else n = vars.length;
        if (v.equals(objective)) {
            return objective.getInf();
        } else {
            if (v.equals(vars[dest])) {
                return src;
            } else {
                int idx = -1;
                do{
                    idx++;
                } while(!vars[idx].equals(v));
                int cost = Integer.MAX_VALUE;
                int val = -1;
                for(int i = 0; i < n; i++) {
                    if(matrix[idx][i] < cost && v.canBeInstantiatedTo(i)) {
                        cost = matrix[idx][i];
                        val = i;
                    }
                }
                return val;
            }
        }
    }

}
