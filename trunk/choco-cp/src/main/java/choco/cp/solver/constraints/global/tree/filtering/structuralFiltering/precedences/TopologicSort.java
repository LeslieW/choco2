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
package choco.cp.solver.constraints.global.tree.filtering.structuralFiltering.precedences;


import choco.kernel.memory.trailing.StoredBitSet;

import java.util.BitSet;


public class TopologicSort {

    protected BitSet[] RGS;
    protected int[] numTable;
    protected BitSet sorted;

    public TopologicSort(StoredBitSet[] rgs) {
        this.RGS = new BitSet[rgs.length];
        for (int i = 0; i < rgs.length; i++) {
            RGS[i] = rgs[i].copyToBitSet();
        }
        this.numTable = new int[RGS.length];
        for (int i = 0; i < numTable.length; i++) numTable[i] = -1;
        this.sorted = new BitSet(RGS.length);
    }

    public int[] sort() {
        exec_sort(0);
        return numTable;
    }

    protected void exec_sort(int lvl) {
        BitSet sources = getSources();
        if (sources.cardinality() > 0) {
            for (int i = sources.nextSetBit(0); i >= 0; i = sources.nextSetBit(i + 1)) {
                numTable[i] = lvl;
                sorted.set(i, true);
                updateRGS(i);
            }
            lvl++;
            exec_sort(lvl);
        }
    }

    protected void updateRGS(int v) {
        for (int i = 0; i < RGS.length; i++) {
            if (i != v) {
                for (int j = RGS[i].nextSetBit(0); j >= 0; j = RGS[i].nextSetBit(j + 1)) {
                    if (j == v) RGS[i].set(j, true);
                }
            }
        }
        RGS[v].clear();
    }

    protected BitSet getSources() {
        BitSet res = new BitSet(RGS.length);
        for (int i = 0; i < RGS.length; i++) {
            if (!sorted.get(i)) {
                boolean src = true;
                for (BitSet aRGS : RGS) {
                    if (aRGS.get(i)) src = false;
                }
                if (src) res.set(i, true);
            }
        }
        return res;
    }
}
