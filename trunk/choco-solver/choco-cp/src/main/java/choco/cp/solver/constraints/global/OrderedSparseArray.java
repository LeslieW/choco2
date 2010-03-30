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
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * User : xlorca
 * Mail : xlorca(a)emn.fr
 * Date : 29 janv. 2010
 * Since : Choco 2.1.1
 */
public final class OrderedSparseArray {

    int[][] values;
    int[] nbVals;
    int[][] infos;
    int[] indices;
    boolean[] dirs; // dirs[i] true => increasing order, otherwise decreasing order
    int[] defaults;
    int[] previous;
    int n;
    boolean type; // true si minS maxS ou minP maxP otherwise false

    public OrderedSparseArray(final int n) {
        this(n, false);
    }

    public OrderedSparseArray(final int n, final boolean type) {
        this.n = n;
        this.type = type;
        this.values = new int[n][];
        this.nbVals = new int[n];
        this.infos = new int[n][];
        this.indices = new int[n];
        this.dirs = new boolean[n];
        this.defaults = new int[n];
        this.previous = new int[n];
    }

    public void allocate(final IntDomainVar[] vars, final int def) {
        for (int i = 0; i < n; i++) {
            values[i] = new int[vars[i].getDomainSize()];
            nbVals[i] = vars[i].getDomainSize();
            final DisposableIntIterator it = vars[i].getDomain().getIterator();
            int j = 0;
            while (it.hasNext()) {
                values[i][j] = it.next();
                j++;
            }
            it.dispose();
            infos[i] = new int[vars[i].getDomainSize()];
            for (j = 0; j < nbVals[i]; j++) {
                infos[i][j] = def;
            }
            defaults[i] = def;
        }
    }

    public void scanInit(final int i, final boolean dir) {
        dirs[i] = dir;
        previous[i] = defaults[i];
        if (dir) {
            indices[i] = -1;
        } else {
            indices[i] = nbVals[i];
        }
    }

    public int get(final int i, final int v) {
        if (dirs[i]) {
            while (indices[i] == -1 || (indices[i] <= nbVals[i] - 1 && v > values[i][indices[i]])) {
                if (type && indices[i] >= 0) {
                    previous[i] = infos[i][indices[i]];
                }
                indices[i]++;
            }
        } else {
            while (indices[i] == nbVals[i] || (indices[i] >= 0 && v < values[i][indices[i]])) {
                if (type && indices[i] < nbVals[i]) {
                    previous[i] = infos[i][indices[i]];
                }
                indices[i]--;
            }
        }
        if ((dirs[i] && (indices[i] > nbVals[i] - 1 || v < values[i][indices[i]])) || (!dirs[i] && (indices[i] < 0 || v > values[i][indices[i]]))) {
            if (!type) { // cas infSuffix supSuffix infPrefix supPrefix
                previous[i] = defaults[i];
                return defaults[i];
            } else {
                return previous[i];
            }
        } else {
            previous[i] = infos[i][indices[i]];
            return infos[i][indices[i]];
        }
    }

    public void set(final int i, final int v, final int info) {
        if (dirs[i]) {
            //assert(!(indices[i] >= 0 && values[i][indices[i]] > v));
            if (indices[i] == -1 || values[i][indices[i]] < v) {
                if (type && indices[i] >= 0) {
                    previous[i] = infos[i][indices[i]];
                }
                indices[i]++;
                //assert(indices[i] <= nbVals[i]+1);
                values[i][indices[i]] = v;
            }
        } else {
            //assert(!(indices[i] < nbVals[i]-1 && values[i][indices[i]] < v));
            if (indices[i] == nbVals[i] || values[i][indices[i]] > v) {
                if (type && indices[i] < nbVals[i]) {
                    previous[i] = infos[i][indices[i]];
                }
                indices[i]--;
                //assert(indices[i] >= 0);
                values[i][indices[i]] = v;
            }
        }
        infos[i][indices[i]] = info;
        previous[i] = info;
    }

    public String printer(final String name) {
        final StringBuilder s = new StringBuilder();
        s.append("name | coords | value | info\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < values[i].length; j++) {
                s.append(name).append(" | [").append(i).append("][").append(j).append("] |   ")
                        .append(values[i][j]).append("   | ").append(infos[i][j]).append('\n');
            }
        }
        return s.toString();
    }

}